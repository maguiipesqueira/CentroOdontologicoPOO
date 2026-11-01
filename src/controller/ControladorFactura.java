package controller;

import entity.DesgloseMonto;
import entity.Factura;
import entity.Turno;
import exception.DatoInvalidoException;
import pago.MedioPago;
import service.CierreDelDia;
import service.ServicioFactura;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Coordina la vista previa, el cobro y las consultas de facturación. */
public class ControladorFactura {

    private final ServicioFactura servicio;
    private final Clock clock;

    public ControladorFactura(ServicioFactura servicio) {
        this(servicio, Clock.systemDefaultZone());
    }

    public ControladorFactura(ServicioFactura servicio, Clock clock) {
        this.servicio = Objects.requireNonNull(servicio, "El servicio de facturación es obligatorio.");
        this.clock = Objects.requireNonNull(clock, "El reloj es obligatorio.");
    }

    public List<Turno> listarTurnosPorCobrar() { return servicio.listarTurnosPorCobrar(); }
    public List<MedioPago> listarMediosPago() { return servicio.listarMediosPago(); }
    public String nombreMedioPago(String codigo) { return servicio.nombreMedioPago(codigo == null ? null : codigo.trim()); }

    public DesgloseMonto calcularVistaPrevia(long turnoId) throws DatoInvalidoException {
        return servicio.calcularVistaPrevia(turnoId);
    }

    public DesgloseMonto calcularVistaPrevia(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        return servicio.calcularVistaPrevia(turnoId, medioPago);
    }

    public Factura cobrarTurno(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        return servicio.cobrarTurno(turnoId, medioPago);
    }

    public List<Factura> listarTodos() { return servicio.listarTodos(); }

    public CierreDelDia cerrarDia() throws DatoInvalidoException {
        return servicio.cerrarDia(LocalDate.now(clock));
    }

    public CierreDelDia cerrarDia(LocalDate fecha) throws DatoInvalidoException {
        return servicio.cerrarDia(fecha);
    }
}
