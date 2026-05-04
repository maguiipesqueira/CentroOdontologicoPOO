package controller;

import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Controlador de Turnos.
 * Patrón GRASP Controller: coordina entre múltiples servicios sin contener lógica de negocio.
 */
public class ControladorTurno {

    private final ServicioTurno servicioTurno;
    private final ServicioPaciente servicioPaciente;
    private final ServicioOdontologo servicioOdontologo;
    private final Scanner scanner;

    public ControladorTurno(ServicioTurno servicioTurno,
                             ServicioPaciente servicioPaciente,
                             ServicioOdontologo servicioOdontologo,
                             Scanner scanner) {
        this.servicioTurno = servicioTurno;
        this.servicioPaciente = servicioPaciente;
        this.servicioOdontologo = servicioOdontologo;
        this.scanner = scanner;
    }

    public void menuTurnos() {
        int opcion;
        do {
            System.out.println("\n=========== GESTIÓN DE TURNOS ===========");
            System.out.println("  1. Registrar nuevo turno");
            System.out.println("  2. Buscar turno por ID");
            System.out.println("  3. Listar todos los turnos");
            System.out.println("  4. Listar turnos por paciente");
            System.out.println("  5. Listar turnos por odontólogo");
            System.out.println("  6. Confirmar turno");
            System.out.println("  7. Cancelar turno");
            System.out.println("  8. Completar turno");
            System.out.println("  9. Eliminar turno (solo cancelados)");
            System.out.println("  0. Volver al menú principal");
            System.out.println("==========================================");
            System.out.print("  Seleccione una opción: ");
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
                case 0 -> System.out.println("  Volviendo al menú principal...");
                default -> System.out.println("  Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void registrarTurno() {
        System.out.println("\n-- Registrar nuevo turno --");

        System.out.print("  ID del paciente: ");
        long pacienteId = leerLong();

        Paciente paciente = servicioPaciente.buscarPorId(pacienteId);

        if (paciente == null) {
            System.out.println("  ERROR: No existe un paciente con ID " + pacienteId + ".");
            return;
        }

        System.out.print("  ID del odontólogo: ");
        long odontologoId = leerLong();

        Odontologo odontologo = servicioOdontologo.buscarPorId(odontologoId);

        if (odontologo == null) {
            System.out.println("  ERROR: No existe un odontólogo con ID " + odontologoId + ".");
            return;
        }

        System.out.print("  Fecha (YYYY-MM-DD): ");
        LocalDate fecha = leerFecha();
        if (fecha == null) return;

        System.out.print("  Hora (HH:MM): ");
        LocalTime hora = leerHora();
        if (hora == null) return;

        Turno turno = servicioTurno.registrarTurno(paciente, odontologo, fecha, hora);

        if (turno != null) {
            System.out.println("  ✔ Turno registrado exitosamente: " + turno);
        } else {
            System.out.println("  No se pudo registrar el turno.");
        }
    }

    private void buscarPorId() {
        System.out.print("  Ingrese el ID del turno: ");
        long id = leerLong();

        Turno turno = servicioTurno.buscarPorId(id);

        if (turno != null) {
            System.out.println("  Turno encontrado: " + turno);
        } else {
            System.out.println("  No se encontró un turno con ID " + id + ".");
        }
    }

    private void listarTodos() {
        List<Turno> lista = servicioTurno.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  No hay turnos registrados.");
        } else {
            System.out.println("\n  --- Lista de Turnos ---");
            lista.forEach(t -> System.out.println("  " + t));
        }
    }

    private void listarPorPaciente() {
        System.out.print("  ID del paciente: ");
        long id = leerLong();
        List<Turno> lista = servicioTurno.listarPorPaciente(id);
        if (lista.isEmpty()) {
            System.out.println("  No hay turnos para el paciente con ID " + id + ".");
        } else {
            lista.forEach(t -> System.out.println("  " + t));
        }
    }

    private void listarPorOdontologo() {
        System.out.print("  ID del odontólogo: ");
        long id = leerLong();
        List<Turno> lista = servicioTurno.listarPorOdontologo(id);
        if (lista.isEmpty()) {
            System.out.println("  No hay turnos para el odontólogo con ID " + id + ".");
        } else {
            lista.forEach(t -> System.out.println("  " + t));
        }
    }

    private void confirmarTurno() {
        System.out.print("  ID del turno a confirmar: ");
        long id = leerLong();

        Turno turno = servicioTurno.confirmarTurno(id);

        if (turno != null) {
            System.out.println("  ✔ Turno #" + id + " confirmado.");
        } else {
            System.out.println("  No se pudo confirmar el turno.");
        }
    }

    private void cancelarTurno() {
        System.out.print("  ID del turno a cancelar: ");
        long id = leerLong();

        Turno turno = servicioTurno.cancelarTurno(id);

        if (turno != null) {
            System.out.println("  ✔ Turno #" + id + " cancelado.");
        } else {
            System.out.println("  No se pudo cancelar el turno.");
        }
    }

    private void completarTurno() {
        System.out.print("  ID del turno a completar: ");
        long id = leerLong();

        Turno turno = servicioTurno.completarTurno(id);

        if (turno != null) {
            System.out.println("  ✔ Turno #" + id + " marcado como completado.");
        } else {
            System.out.println("  No se pudo completar el turno.");
        }
    }

    private void eliminarTurno() {
        System.out.print("  ID del turno a eliminar: ");
        long id = leerLong();

        servicioTurno.eliminarTurno(id);
    }

    // --- Utilidades ---

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Valor inválido. Ingrese un número: ");
            }
        }
    }

    private long leerLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Valor inválido. Ingrese un número: ");
            }
        }
    }

    private LocalDate leerFecha() {
        try {
            return LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("  Formato de fecha inválido. Use YYYY-MM-DD.");
            return null;
        }
    }

    private LocalTime leerHora() {
        try {
            return LocalTime.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("  Formato de hora inválido. Use HH:MM.");
            return null;
        }
    }
}
