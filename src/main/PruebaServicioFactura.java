package main;

import entity.*;
import exception.ClinicaException;
import exception.DatoInvalidoException;
import pago.*;
import repository.*;
import service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// PRUEBA TEMPORAL de la Etapa 7: arma el sistema en memoria y prueba el cobro. Se corre y se borra
public class PruebaServicioFactura {

    public static void main(String[] args) throws ClinicaException {
        ServicioPaciente servPaciente = new ServicioPaciente(new RepositorioPaciente());
        ServicioOdontologo servOdontologo = new ServicioOdontologo(new RepositorioOdontologo());
        ServicioTurno servTurno = new ServicioTurno(new RepositorioTurno());

        List<MedioPago> medios = List.of(new MercadoPago(), new Tarjeta(), new Efectivo());
        ServicioFactura servFactura = new ServicioFactura(new RepositorioFactura(), servTurno,
                new Tarifario(), new CalculadorMonto(), medios);
        MedioPago efectivo = medios.get(2);
        MedioPago tarjeta = medios.get(1);

        Domicilio dom = new Domicilio("Calle", 1, "Loc", "Prov", TipoHogar.CASA);
        Paciente conOS = servPaciente.registrarPaciente("Fatima", "Persi", 111, "f@x.com", dom, true);
        Paciente sinOS = servPaciente.registrarPaciente("Tomas", "Barea", 222, "t@x.com", dom, false);

        Odontologo od = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        servOdontologo.registrarOdontologo(od);

        LocalDate dia = LocalDate.now().plusDays(1);
        Turno tA = servTurno.registrarTurno(conOS, od, dia, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        Turno tB = servTurno.registrarTurno(conOS, od, dia, LocalTime.of(10, 0), TipoConsulta.CONTROL);
        servTurno.cancelarTurno(tB.getId());
        Turno tC = servTurno.registrarTurno(sinOS, od, dia, LocalTime.of(11, 0), TipoCirugia.IMPLANTE);
        Turno tD = servTurno.registrarTurno(sinOS, od, dia, LocalTime.of(12, 0)); // sin motivo

        System.out.println("Por cobrar (esperado: turnos " + tA.getId() + ", " + tC.getId() + " y " + tD.getId() + "; no el cancelado " + tB.getId() + "):");
        for (Turno t : servFactura.listarTurnosPorCobrar()) System.out.println("  " + t);

        System.out.println("Vista previa A sin medio (esperado total 17500): " + servFactura.calcularVistaPrevia(tA.getId()));
        System.out.println("Vista previa A efectivo (esperado total 15750): " + servFactura.calcularVistaPrevia(tA.getId(), efectivo));

        Factura f = servFactura.cobrarTurno(tA.getId(), efectivo);
        System.out.println("Cobrado: " + f);
        System.out.println("Por cobrar luego de cobrar A: " + servFactura.listarTurnosPorCobrar().size() + " (esperado 2)");

        probarError("Cobrar A de nuevo", () -> servFactura.cobrarTurno(tA.getId(), efectivo));
        probarError("Cobrar turno cancelado", () -> servFactura.cobrarTurno(tB.getId(), efectivo));
        probarError("Cobrar sin medio de pago", () -> servFactura.cobrarTurno(tC.getId(), null));
        probarError("Cobrar turno sin tratamiento", () -> servFactura.cobrarTurno(tD.getId(), efectivo));
        probarError("Cobrar turno inexistente", () -> servFactura.cobrarTurno(999L, efectivo));

        Factura f2 = servFactura.cobrarTurno(tC.getId(), tarjeta);
        System.out.println("Cobrado C con tarjeta (esperado total 450000): " + f2);
        System.out.println("Medios disponibles: " + servFactura.listarMediosPago());
    }

    interface Accion { void ejecutar() throws DatoInvalidoException; }

    static void probarError(String nombre, Accion accion) {
        try {
            accion.ejecutar();
            System.out.println(nombre + " -> MAL: no tiro error");
        } catch (DatoInvalidoException e) {
            System.out.println(nombre + " -> ERROR esperado: " + e.getMessage());
        }
    }
}