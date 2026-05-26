package ui;

import controller.ControladorTurno;
import entity.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import exception.ClinicaException;

public class VistaTurno {

    private final ControladorTurno controlador;
    private final Scanner scanner;

    public VistaTurno(ControladorTurno controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("\n=========== GESTIÓN DE TURNOS ===========");
            System.out.println("1. Registrar turno");
            System.out.println("2. Buscar turno por ID");
            System.out.println("3. Listar turnos");
            System.out.println("4. Listar por paciente");
            System.out.println("5. Listar por odontólogo");
            System.out.println("6. Confirmar turno");
            System.out.println("7. Cancelar turno");
            System.out.println("8. Completar turno");
            System.out.println("9. Eliminar turno");
            System.out.println("10. Listar por rango de fechas");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarTurno();
                case 2 -> buscarPorId();
                case 3 -> listarTodos();
                case 4 -> listarPorPaciente();
                case 5 -> listarPorOdontologo();
                case 6 -> confirmarTurno();
                case 7 -> cancelarTurno();
                case 8 -> completarTurno();
                case 9 -> eliminarTurno();
                case 10 -> listarPorRangoFechas();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    // registra capturando si el paciente o el odontólogo no existen
    private void registrarTurno() {
        System.out.print("ID paciente: ");
        long pacienteId = leerLong();

        System.out.print("ID odontólogo: ");
        long odontologoId = leerLong();

        System.out.print("Fecha (YYYY-MM-DD): ");
        LocalDate fecha = leerFecha();

        if (fecha == null) return;

        System.out.print("Hora (HH:MM): ");
        LocalTime hora = leerHora();

        if (hora == null) return;

        try {
            Turno turno = controlador.registrarTurno(pacienteId, odontologoId, fecha, hora);
            System.out.println("Turno registrado: " + turno);
        } catch (ClinicaException e) {
            System.out.println(e);
        }
    }

    private void buscarPorId() {
        System.out.print("ID turno: ");
        long id = leerLong();

        try {
            Turno turno = controlador.buscarPorId(id);
            System.out.println("Turno encontrado: " + turno);
        } catch (ClinicaException e) {
            System.out.println(e);
        }
    }

    private void listarTodos() {
        List<Turno> turnos = controlador.listarTodos();
        mostrarLista(turnos);
    }

    private void listarPorPaciente() {
        System.out.print("ID paciente: ");
        long id = leerLong();

        mostrarLista(controlador.listarPorPaciente(id));
    }

    private void listarPorOdontologo() {
        System.out.print("ID odontólogo: ");
        long id = leerLong();

        mostrarLista(controlador.listarPorOdontologo(id));
    }

    private void confirmarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        try {
            controlador.confirmarTurno(id);
            System.out.println("Turno confirmado.");
        } catch (ClinicaException e) {
            System.out.println(e);
        }
    }

    private void cancelarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        try {
            controlador.cancelarTurno(id);
            System.out.println("Turno cancelado.");
        } catch (ClinicaException e) {
            System.out.println(e);
        }
    }

    private void completarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        try {
            controlador.completarTurno(id);
            System.out.println("Turno completado.");
        } catch (ClinicaException e) {
            System.out.println(e);
        }
    }

    private void eliminarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        try {
            controlador.eliminarTurno(id);
            System.out.println("Turno eliminado correctamente.");
        } catch (ClinicaException e) {
            System.out.println(e);
        }
    }


    private void mostrarLista(List<Turno> turnos) {
        if (turnos.isEmpty()) {
            System.out.println("No hay turnos.");
        } else {
            for (Turno t : turnos) {
                System.out.println(t);
            }
        }
    }


    private void listarPorRangoFechas() {
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate inicio = leerFecha();

        if (inicio == null) return;

        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate fin = leerFecha();

        if (fin == null) return;

        try {
            mostrarLista(controlador.listarPorRangoFechas(inicio, fin));
        } catch (ClinicaException e) {
            System.out.println(e);
        }
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

    private long leerLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }

    private LocalDate leerFecha() {
        try {
            return LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("Fecha inválida.");
            return null;
        }
    }

    private LocalTime leerHora() {
        try {
            return LocalTime.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("Hora inválida.");
            return null;
        }
    }
}