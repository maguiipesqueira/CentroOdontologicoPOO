package main;

import controller.ControladorFactura;
import entity.*;
import exception.ClinicaException;
import pago.*;
import repository.*;
import service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// PRUEBA TEMPORAL de la Etapa 8: cobra turnos con distintos medios y prueba el cierre del dia. Se corre y se borra
public class PruebaCierreDelDia {

    public static void main(String[] args) throws ClinicaException {
        ServicioPaciente servPaciente = new ServicioPaciente(new RepositorioPaciente());
        ServicioOdontologo servOdontologo = new ServicioOdontologo(new RepositorioOdontologo());
        ServicioTurno servTurno = new ServicioTurno(new RepositorioTurno());

        List<MedioPago> medios = List.of(new MercadoPago(), new Tarjeta(), new Efectivo());
        RepositorioFactura repoFactura = new RepositorioFactura();
        ServicioFactura servFactura = new ServicioFactura(repoFactura, servTurno, new Tarifario(), new CalculadorMonto(), medios);
        ControladorFactura ctrl = new ControladorFactura(servFactura);
        MedioPago tarjeta = medios.get(1);
        MedioPago efectivo = medios.get(2);

        Domicilio dom = new Domicilio("Calle", 1, "Loc", "Prov", TipoHogar.CASA);
        Paciente conOS = servPaciente.registrarPaciente("Fatima", "Persi", 111, "f@x.com", dom, true);
        Paciente sinOS = servPaciente.registrarPaciente("Tomas", "Barea", 222, "t@x.com", dom, false);

        Odontologo od = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        servOdontologo.registrarOdontologo(od);

        LocalDate dia = LocalDate.now().plusDays(1);
        Turno t1 = servTurno.registrarTurno(conOS, od, dia, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        Turno t2 = servTurno.registrarTurno(sinOS, od, dia, LocalTime.of(10, 0), TipoCirugia.IMPLANTE);
        Turno t3 = servTurno.registrarTurno(sinOS, od, dia, LocalTime.of(11, 0), TipoConsulta.CONTROL);

        System.out.println("Cierre sin facturas (esperado: 0 facturas, total 0.0): " + ctrl.cerrarDia());

        ctrl.cobrarTurno(t1.getId(), efectivo);  // 15750
        ctrl.cobrarTurno(t2.getId(), tarjeta);   // 450000
        ctrl.cobrarTurno(t3.getId(), efectivo);  // 13500

        // una factura de ayer que NO debe entrar en el cierre de hoy
        repoFactura.guardar(new Factura(repoFactura.siguienteId(), 99L, 1L, "Otro Paciente", "LIMPIEZA",
                LocalDate.now().minusDays(1), "Mercado Pago", 99999.0, 0.0, 0.0, 99999.0));

        System.out.println("Cierre de hoy (esperado: 3 facturas, total 479250.0, Tarjeta 450000.0, Efectivo 29250.0):");
        System.out.println("  " + ctrl.cerrarDia());
        System.out.println("Cierre de ayer (esperado: 1 factura, total 99999.0, Mercado Pago 99999.0):");
        System.out.println("  " + ctrl.cerrarDia(LocalDate.now().minusDays(1)));
        System.out.println("Cierre de manana (esperado: 0 facturas, total 0.0): " + ctrl.cerrarDia(LocalDate.now().plusDays(1)));

        System.out.println("Todas las facturas (esperado 4, ordenadas por numero):");
        for (Factura f : ctrl.listarTodos()) System.out.println("  #" + f.getId() + " " + f.getFecha() + " " + f.getMetodoPago() + " $" + f.getMontoTotal());

        try {
            ctrl.cerrarDia(null);
        } catch (ClinicaException e) {
            System.out.println("Cierre con fecha nula -> ERROR esperado: " + e.getMessage());
        }
    }
}