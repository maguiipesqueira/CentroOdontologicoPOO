package view;

import controller.ControladorTurno;
import entity.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

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
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

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

        Turno turno = controlador.registrarTurno(pacienteId, odontologoId, fecha, hora);

        if (turno != null) {
            System.out.println("Turno registrado: " + turno);
        } else {
            System.out.println("No se pudo registrar el turno.");
        }
    }

    private void buscarPorId() {
        System.out.print("ID turno: ");
        long id = leerLong();

        Turno turno = controlador.buscarPorId(id);

        if (turno != null) {
            System.out.println("Turno encontrado: " + turno);
        } else {
            System.out.println("No existe turno con ese ID.");
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

        Turno turno = controlador.confirmarTurno(id);

        if (turno != null) {
            System.out.println("Turno confirmado.");
        } else {
            System.out.println("No se pudo confirmar.");
        }
    }

    private void cancelarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        Turno turno = controlador.cancelarTurno(id);

        if (turno != null) {
            System.out.println("Turno cancelado.");
        } else {
            System.out.println("No se pudo cancelar.");
        }
    }

    private void completarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        Turno turno = controlador.completarTurno(id);

        if (turno != null) {
            System.out.println("Turno completado.");
        } else {
            System.out.println("No se pudo completar.");
        }
    }

    private void eliminarTurno() {
        System.out.print("ID turno: ");
        long id = leerLong();

        controlador.eliminarTurno(id);
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
    