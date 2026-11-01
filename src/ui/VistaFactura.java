package ui;

import controller.ControladorFactura;
import entity.Factura;
import entity.Turno;
import exception.DatoInvalidoException;
import pago.MedioPago;
import service.CierreDelDia;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/** Interfaz de consola para cobros, consulta de facturas y cierre diario. */
public class VistaFactura {
    private final ControladorFactura controlador;
    private final LectorConsola lector;
    private final MenuConsola menu;

    public VistaFactura(ControladorFactura controlador, LectorConsola lector) {
        this.controlador = controlador;
        this.lector = lector;
        this.menu = new MenuConsola(lector);
    }

    public VistaFactura(ControladorFactura controlador, Scanner scanner) {
        this(controlador, new LectorConsola(scanner));
    }

    public void mostrarMenu() {
        menu.ejecutar("=========== FACTURACIÓN ===========", List.of(
                new OpcionMenu(1, "Cobrar turno", this::cobrarTurno),
                new OpcionMenu(2, "Listar facturas emitidas", this::listarFacturas),
                new OpcionMenu(3, "Cierre del día", this::cerrarDia)
        ));
    }

    private void cobrarTurno() {
        List<Turno> pendientes = controlador.listarTurnosPorCobrar();
        if (pendientes.isEmpty()) {
            System.out.println("No hay turnos pendientes de cobro.");
            return;
        }
        System.out.println("Turnos disponibles para cobrar:");
        pendientes.forEach(t -> System.out.println("ID " + t.getId() + " | " + t));
        long turnoId = lector.leerLong("ID del turno a cobrar: ");
        List<MedioPago> medios = controlador.listarMediosPago();
        if (medios.isEmpty()) {
            System.out.println("No hay medios de pago configurados.");
            return;
        }
        System.out.println("Medios de pago:");
        for (int i = 0; i < medios.size(); i++) {
            System.out.println((i + 1) + ". " + medios.get(i).getNombre());
        }
        int seleccion;
        do {
            seleccion = lector.leerEntero("Seleccione medio de pago: ");
            if (seleccion < 1 || seleccion > medios.size()) System.out.println("Opción inválida.");
        } while (seleccion < 1 || seleccion > medios.size());
        MedioPago medio = medios.get(seleccion - 1);
        try {
            System.out.println("Vista previa: " + controlador.calcularVistaPrevia(turnoId, medio));
            if (!lector.leerSiNo("¿Confirmar cobro? (S/N): ")) {
                System.out.println("Cobro cancelado.");
                return;
            }
            Factura factura = controlador.cobrarTurno(turnoId, medio);
            System.out.println("Cobro realizado correctamente:");
            System.out.println(factura);
        } catch (DatoInvalidoException e) {
            System.out.println("No se pudo realizar el cobro: " + e.getMessage());
        }
    }

    private void listarFacturas() {
        List<Factura> facturas = controlador.listarTodos();
        if (facturas.isEmpty()) System.out.println("Todavía no hay facturas emitidas.");
        else facturas.forEach(System.out::println);
    }

    private void cerrarDia() {
        LocalDate fecha = lector.leerFecha("Fecha del cierre (YYYY-MM-DD): ");
        try {
            CierreDelDia cierre = controlador.cerrarDia(fecha);
            System.out.println(cierre);
            cierre.getTotalesPorMedioDecimal().forEach(
                    (medio, total) -> System.out.println("  " + medio + ": $" + total));
        } catch (DatoInvalidoException e) {
            System.out.println("No se pudo generar el cierre: " + e.getMessage());
        }
    }
}
