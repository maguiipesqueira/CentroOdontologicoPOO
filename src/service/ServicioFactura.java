package service;

import entity.DesgloseMonto;
import entity.Factura;
import entity.MotivoConsulta;
import entity.Paciente;
import entity.Turno;
import exception.DatoInvalidoException;
import pago.MedioPago;
import repository.GeneradorIds;
import repository.RepositorioFactura;

import java.time.Clock;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;

// Cobra los turnos: coordina el tarifario, el calculador de montos y los medios de pago, y genera la factura
public class ServicioFactura {

    private final RepositorioFactura repositorio;
    private final GeneradorIds generadorIds = new GeneradorIds();
    private final ServicioTurno servicioTurno;
    private final Tarifario tarifario;
    private final CalculadorMonto calculador;
    private final List<MedioPago> mediosPago;
    private final Clock clock;

    // Recibe sus colaboradores ya construidos; los medios de pago llegan por la interfaz
    // para no depender de implementaciones concretas.
    public ServicioFactura(RepositorioFactura repositorio, ServicioTurno servicioTurno, Tarifario tarifario,
                           CalculadorMonto calculador, List<MedioPago> mediosPago) {
        this(repositorio, servicioTurno, tarifario, calculador, mediosPago, Clock.systemDefaultZone());
    }

    // el reloj se inyecta para poder probar la fecha de emision de las facturas
    public ServicioFactura(RepositorioFactura repositorio, ServicioTurno servicioTurno, Tarifario tarifario,
                           CalculadorMonto calculador, List<MedioPago> mediosPago, Clock clock) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio de facturas es obligatorio.");
        this.servicioTurno = Objects.requireNonNull(servicioTurno, "El servicio de turnos es obligatorio.");
        this.tarifario = Objects.requireNonNull(tarifario, "El tarifario es obligatorio.");
        this.calculador = Objects.requireNonNull(calculador, "El calculador de montos es obligatorio.");
        this.mediosPago = new ArrayList<>(Objects.requireNonNull(mediosPago, "La lista de medios de pago es obligatoria."));
        this.clock = Objects.requireNonNull(clock, "El reloj es obligatorio.");
    }

    // turnos que se pueden cobrar: no cancelados ni ausentes y sin factura, ordenados por fecha y hora.
    // Decision de diseno: se puede cobrar un turno pendiente o confirmado (pago anticipado), no hace falta
    // que ya este completado. Si el negocio exigiera cobrar solo turnos completados, se cambia
    // EstadoTurno.esCobrable() y se actualiza la prueba correspondiente.
    public List<Turno> listarTurnosPorCobrar() {
        return servicioTurno.listarTodos().stream()
                .filter(t -> t.getEstado().esCobrable())
                .filter(t -> !repositorio.existePorTurno(t.getId()))
                .sorted(Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora))
                .collect(Collectors.toList());
    }

    // medios de pago disponibles (para llenar el selector de la pantalla)
    public List<MedioPago> listarMediosPago() {
        return new ArrayList<>(mediosPago);
    }

    // Devuelve el codigo actual de un medio, aceptando tambien nombres de archivos anteriores.
    public String codigoMedioPago(String codigoONombre) {
        if (codigoONombre == null) return null;
        return mediosPago.stream()
                .filter(medio -> medio.getCodigo().equals(codigoONombre)
                        || medio.getNombre().equals(codigoONombre))
                .map(MedioPago::getCodigo)
                .findFirst()
                .orElse(codigoONombre);
    }

    // Resuelve el nombre visible a partir del codigo guardado en la factura.
    public String nombreMedioPago(String codigo) {
        return mediosPago.stream()
                .filter(medio -> medio.getCodigo().equals(codigo))
                .map(MedioPago::getNombre)
                .findFirst()
                .orElse(codigo);
    }

    // calcula cuanto costaria el turno sin elegir todavia el medio de pago (solo obra social)
    public DesgloseMonto calcularVistaPrevia(long turnoId) throws DatoInvalidoException {
        Turno turno = buscarTurnoCobrable(turnoId);
        return calculador.calcular(precioDe(turno), tieneObraSocial(turno));
    }

    // calcula cuanto costaria el turno con el medio de pago elegido, sin guardar nada
    public DesgloseMonto calcularVistaPrevia(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        Turno turno = buscarTurnoCobrable(turnoId);
        validarMedioDisponible(medioPago);
        return calculador.calcular(precioDe(turno), tieneObraSocial(turno), medioPago);
    }

    // cobra el turno: valida, calcula el monto y registra la factura
    public Factura cobrarTurno(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        if (medioPago == null) {
            throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago para cobrar.");
        }

        validarMedioDisponible(medioPago);
        Turno turno = buscarTurnoCobrable(turnoId);
        DesgloseMonto desglose = calculador.calcular(precioDe(turno), tieneObraSocial(turno), medioPago);
        Paciente paciente = turno.getPaciente();

        Factura factura = Factura.builder()
                .id(generadorIds.siguienteId(() -> repositorio.listarTodos().stream()
                        .mapToLong(Factura::getId)
                        .max()
                        .orElse(0L)))
                .turnoId(turno.getId())
                .pacienteId(paciente.getId())
                .pacienteNombre(paciente.getNombreCompleto())
                .concepto(motivoDe(turno).name())
                .fecha(LocalDate.now(clock))
                .metodoPago(medioPago.getCodigo())
                .desglose(desglose)
                .build();

        repositorio.guardar(factura);
        return factura;
    }

    // todas las facturas emitidas, de la mas vieja a la mas nueva
    public List<Factura> listarTodos() {
        return repositorio.listarTodos().stream()
                .sorted(Comparator.comparingLong(Factura::getId))
                .collect(Collectors.toList());
    }

    // cierre del dia: suma lo recibido en esa fecha y lo desglosa por medio de pago (si no hubo facturas, el total es 0)
    public CierreDelDia cerrarDia(LocalDate fecha) throws DatoInvalidoException {
        if (fecha == null) {
            throw new DatoInvalidoException("Fecha", "La fecha del cierre no puede ser nula.");
        }

        List<Factura> facturasDelDia = repositorio.listarPorFecha(fecha);

        Map<String, String> nombresPorCodigo = new LinkedHashMap<>();
        for (MedioPago medio : mediosPago) nombresPorCodigo.put(medio.getCodigo(), medio.getNombre());
        return CierreDelDia.calcular(fecha, facturasDelDia, nombresPorCodigo);
    }

    // busca el turno y comprueba que se pueda cobrar (existe, no esta cancelado y no tiene factura)
    private Turno buscarTurnoCobrable(long turnoId) throws DatoInvalidoException {
        Turno turno = servicioTurno.buscarPorId(turnoId);

        if (!turno.getEstado().esCobrable()) {
            throw new DatoInvalidoException("Turno",
                    "No se puede cobrar un turno " + turno.getEstado().getNombreLegible().toLowerCase() + ".");
        }

        if (repositorio.existePorTurno(turnoId)) {
            throw new DatoInvalidoException("Turno", "El turno ya fue cobrado.");
        }

        return turno;
    }

    // el medio elegido tiene que ser uno de los que ofrece el servicio (se compara por codigo)
    private void validarMedioDisponible(MedioPago medioPago) throws DatoInvalidoException {
        if (medioPago == null) {
            throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago.");
        }
        boolean disponible = mediosPago.stream().anyMatch(m -> m.getCodigo().equals(medioPago.getCodigo()));
        if (!disponible) {
            throw new DatoInvalidoException("Método de pago", "El medio de pago elegido no está disponible.");
        }
    }

    // precio de lista del tratamiento del turno
    private BigDecimal precioDe(Turno turno) throws DatoInvalidoException {
        return tarifario.precioDecimalDe(motivoDe(turno));
    }

    // motivo del turno; si se registro sin motivo no hay nada que cobrar
    private MotivoConsulta motivoDe(Turno turno) throws DatoInvalidoException {
        return turno.getMotivoConsulta().orElseThrow(
                () -> new DatoInvalidoException("Tratamiento", "El turno no tiene un tratamiento cargado."));
    }

    // true si el paciente del turno accede al descuento de obra social
    private boolean tieneObraSocial(Turno turno) {
        return turno.getPaciente().tieneObraSocial();
    }
}