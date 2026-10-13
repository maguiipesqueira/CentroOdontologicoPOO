package service;

import entity.EstadoTurno;
import entity.Factura;
import entity.Paciente;
import entity.Turno;
import exception.DatoInvalidoException;
import pago.MedioPago;
import repository.RepositorioFactura;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Cobra los turnos: coordina el tarifario, el calculador de montos y los medios de pago, y genera la factura
public class ServicioFactura {

    private final RepositorioFactura repositorio;
    private final ServicioTurno servicioTurno;
    private final Tarifario tarifario;
    private final CalculadorMonto calculador;
    private final List<MedioPago> mediosPago;

    // recibe sus colaboradores ya armados (los crea el Main). Los medios de pago llegan como lista de la interfaz,
    // asi este servicio no conoce ninguno en particular
    public ServicioFactura(RepositorioFactura repositorio, ServicioTurno servicioTurno, Tarifario tarifario,
                           CalculadorMonto calculador, List<MedioPago> mediosPago) {
        this.repositorio = repositorio;
        this.servicioTurno = servicioTurno;
        this.tarifario = tarifario;
        this.calculador = calculador;
        this.mediosPago = new ArrayList<>(mediosPago);
    }

    // turnos que se pueden cobrar: no cancelados y sin factura, ordenados por fecha y hora
    public List<Turno> listarTurnosPorCobrar() {
        return servicioTurno.listarTodos().stream()
                .filter(t -> t.getEstado() != EstadoTurno.CANCELADO)
                .filter(t -> !repositorio.existePorTurno(t.getId()))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    // medios de pago disponibles (para llenar el selector de la pantalla)
    public List<MedioPago> listarMediosPago() {
        return new ArrayList<>(mediosPago);
    }

    // calcula cuanto costaria el turno sin elegir todavia el medio de pago (solo obra social)
    public DesgloseMonto calcularVistaPrevia(long turnoId) throws DatoInvalidoException {
        Turno turno = buscarTurnoCobrable(turnoId);
        return calculador.calcular(precioDe(turno), tieneObraSocial(turno));
    }

    // calcula cuanto costaria el turno con el medio de pago elegido, sin guardar nada
    public DesgloseMonto calcularVistaPrevia(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        Turno turno = buscarTurnoCobrable(turnoId);
        return calculador.calcular(precioDe(turno), tieneObraSocial(turno), medioPago);
    }

    // cobra el turno: valida, calcula el monto y registra la factura
    public Factura cobrarTurno(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        if (medioPago == null) {
            throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago para cobrar.");
        }

        Turno turno = buscarTurnoCobrable(turnoId);
        DesgloseMonto desglose = calculador.calcular(precioDe(turno), tieneObraSocial(turno), medioPago);
        Paciente paciente = turno.getPaciente();

        Factura factura = new Factura(
                repositorio.siguienteId(),
                turno.getId(),
                paciente.getId(),
                paciente.getNombreCompleto(),
                turno.getMotivoConsultaTexto(),
                LocalDate.now(),
                medioPago.getNombre(),
                desglose.getMontoBase(),
                desglose.getDescuentoObraSocial(),
                desglose.getDescuentoEfectivo(),
                desglose.getMontoTotal()
        );

        repositorio.guardar(factura);
        return factura;
    }

    // busca el turno y comprueba que se pueda cobrar (existe, no esta cancelado y no tiene factura)
    private Turno buscarTurnoCobrable(long turnoId) throws DatoInvalidoException {
        Turno turno = servicioTurno.buscarPorId(turnoId);

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new DatoInvalidoException("Turno", "No se puede cobrar un turno cancelado.");
        }

        if (repositorio.existePorTurno(turnoId)) {
            throw new DatoInvalidoException("Turno", "El turno ya fue cobrado.");
        }

        return turno;
    }

    // precio de lista del tratamiento del turno
    private double precioDe(Turno turno) throws DatoInvalidoException {
        return tarifario.precioDe(turno.getMotivoConsulta());
    }

    // true si el paciente del turno accede al descuento de obra social
    private boolean tieneObraSocial(Turno turno) {
        return turno.getPaciente().tieneObraSocial();
    }
}