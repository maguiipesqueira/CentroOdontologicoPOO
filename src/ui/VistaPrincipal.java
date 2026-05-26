package ui;

import java.util.Scanner;

public class VistaPrincipal {

    private final VistaPaciente vistaPaciente;
    private final VistaOdontologo vistaOdontologo;
    private final VistaTurno vistaTurno;
    private final Scanner scanner;

    public VistaPrincipal(VistaPaciente vistaPaciente,
                          VistaOdontologo vistaOdontologo,
                          VistaTurno vistaTurno,
                          Scanner scanner) {
        this.vistaPaciente = vistaPaciente;
        this.vistaOdontologo = vistaOdontologo;
        this.vistaTurno = vistaTurno;
        this.scanner = scanner;
    }

    public void iniciar() {
        int opcion;

        do {

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE GESTIÓN ODONTOLÓGICA        ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Gestión de Pacientes                 ║");
            System.out.println("║  2. Gestión de Odontólogos               ║");
            System.out.println("║  3. Gestión de Turnos                    ║");
            System.out.println("║  0. Salir                                ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("  Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> vistaPaciente.mostrarMenu();
                case 2 -> vistaOdontologo.mostrarMenu();
                case 3 -> vistaTurno.mostrarMenu();
                case 0 -> System.out.println("Sistema cerrado. Hasta pronto!");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }

        } while (opcion != 0);
    }

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}
    