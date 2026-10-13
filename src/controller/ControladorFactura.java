package controller;

import entity.Factura;
import entity.Turno;
import exception.DatoInvalidoException;
import pago.MedioPago;
import service.CierreDelDia;
import service.DesgloseMonto;
import service.ServicioFactura;

import java.time.LocalDate;
import java.util.List;

public class ControladorFactura {

    private final ServicioFactura servicio;

    // constructor guarda el servicio
    public ControladorFactura(ServicioFactura servicio) {
        this.servicio = servicio;
    }

    // lista los turnos que todavia se pueden cobrar
    public List<Turno> listarTurnosPorCobrar() {
        return servicio.listarTurnosPorCobrar();
    }

    // lista los medios de pago disponibles
    public List<MedioPago> listarMediosPago() {
        return servicio.listarMediosPago();
    }

    // calcula el monto sin elegir medio de pago, dejando pasar el error de datos
    public DesgloseMonto calcularVistaPrevia(long turnoId) throws DatoInvalidoException {
        return servicio.calcularVistaPrevia(turnoId);
    }

    // calcula el monto con el medio elegido, dejando pasar el error de datos
    public DesgloseMonto calcularVistaPrevia(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        return servicio.calcularVistaPrevia(turnoId, medioPago);
    }

    // cobra el turno dejando pasar el error de datos
    public Factura cobrarTurno(long turnoId, MedioPago medioPago) throws DatoInvalidoException {
        return servicio.cobrarTurno(turnoId, medioPago);
    }

    // lista todas las facturas emitidas
    public List<Factura> listarTodos() {
        return servicio.listarTodos();
    }

    // cierre del dia de hoy
    public CierreDelDia cerrarDia() throws DatoInvalidoException {
        return servicio.cerrarDia(LocalDate.now());
    }

    // cierre del dia de una fecha cualquiera
    public CierreDelDia cerrarDia(LocalDate fecha) throws DatoInvalidoException {
        return servicio.cerrarDia(fecha);
    }
}