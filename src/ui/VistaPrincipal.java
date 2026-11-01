package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Menú principal de consola; cada entrada delega en un comando de la vista correspondiente. */
public class VistaPrincipal {
    private final MenuConsola menu;
    private final List<OpcionMenu> opciones;

    public VistaPrincipal(VistaPaciente vistaPaciente, VistaOdontologo vistaOdontologo,
                          VistaTurno vistaTurno, VistaFactura vistaFactura, LectorConsola lector) {
        this.menu = new MenuConsola(lector);
        List<OpcionMenu> comandos = new ArrayList<>();
        comandos.add(new OpcionMenu(1, "Gestión de Pacientes", vistaPaciente::mostrarMenu));
        comandos.add(new OpcionMenu(2, "Gestión de Odontólogos", vistaOdontologo::mostrarMenu));
        comandos.add(new OpcionMenu(3, "Gestión de Turnos", vistaTurno::mostrarMenu));
        comandos.add(new OpcionMenu(4, "Facturación", vistaFactura::mostrarMenu));
        this.opciones = List.copyOf(comandos);
    }

    /** Constructor de compatibilidad para clientes anteriores a la opción de facturación. */
    public VistaPrincipal(VistaPaciente vistaPaciente, VistaOdontologo vistaOdontologo,
                          VistaTurno vistaTurno, Scanner scanner) {
        this.menu = new MenuConsola(new LectorConsola(scanner));
        this.opciones = List.of(
                new OpcionMenu(1, "Gestión de Pacientes", vistaPaciente::mostrarMenu),
                new OpcionMenu(2, "Gestión de Odontólogos", vistaOdontologo::mostrarMenu),
                new OpcionMenu(3, "Gestión de Turnos", vistaTurno::mostrarMenu)
        );
    }

    public void iniciar() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GESTIÓN ODONTOLÓGICA        ║");
        System.out.println("╚══════════════════════════════════════════╝");
        menu.ejecutar("MENÚ PRINCIPAL", opciones);
        System.out.println("Sistema cerrado. Hasta pronto!");
    }
}
