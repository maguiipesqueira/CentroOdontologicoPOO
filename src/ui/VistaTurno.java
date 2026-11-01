package ui;

import controller.ControladorTurno;
import controller.SolicitudTurno;
import entity.Turno;
import exception.ClinicaException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class VistaTurno {
    private final ControladorTurno controlador;
    private final LectorConsola lector;
    private final MenuConsola menu;

    public VistaTurno(ControladorTurno controlador, LectorConsola lector) {
        this.controlador = controlador;
        this.lector = lector;
        this.menu = new MenuConsola(lector);
    }

    public VistaTurno(ControladorTurno controlador, Scanner scanner) {
        this(controlador, new LectorConsola(scanner));
    }

    public void mostrarMenu() {
        menu.ejecutar("=========== GESTIÓN DE TURNOS ===========", List.of(
                new OpcionMenu(1, "Registrar turno", this::registrarTurno),
                new OpcionMenu(2, "Buscar turno por ID", this::buscarPorId),
                new OpcionMenu(3, "Listar turnos", this::listarTodos),
                new OpcionMenu(4, "Listar por paciente", this::listarPorPaciente),
                new OpcionMenu(5, "Listar por odontólogo", this::listarPorOdontologo),
                new OpcionMenu(6, "Confirmar turno", this::confirmarTurno),
                new OpcionMenu(7, "Cancelar turno", this::cancelarTurno),
                new OpcionMenu(8, "Completar turno", this::completarTurno),
                new OpcionMenu(9, "Eliminar turno", this::eliminarTurno),
                new OpcionMenu(10, "Listar por rango de fechas", this::listarPorRangoFechas)
        ));
    }

    private void registrarTurno() {
        long pacienteId = lector.leerLong("ID paciente: ");
        long odontologoId = lector.leerLong("ID odontólogo: ");
        LocalDate fecha = lector.leerFecha("Fecha (YYYY-MM-DD): ");
        LocalTime hora = lector.leerHora("Hora (HH:MM): ");
        try {
            Turno turno = controlador.registrarTurno(
                    new SolicitudTurno(pacienteId, odontologoId, fecha, hora, null));
            System.out.println("Turno registrado: " + turno);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorId() {
        long id = lector.leerLong("ID turno: ");
        try {
            System.out.println("Turno encontrado: " + controlador.buscarPorId(id));
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarTodos() { mostrarLista(controlador.listarTodos()); }

    private void listarPorPaciente() {
        mostrarLista(controlador.listarPorPaciente(lector.leerLong("ID paciente: ")));
    }

    private void listarPorOdontologo() {
        mostrarLista(controlador.listarPorOdontologo(lector.leerLong("ID odontólogo: ")));
    }

    private void confirmarTurno() { ejecutarAccionSobreTurno("ID turno: ", controlador::confirmarTurno, "Turno confirmado."); }
    private void cancelarTurno() { ejecutarAccionSobreTurno("ID turno: ", controlador::cancelarTurno, "Turno cancelado."); }
    private void completarTurno() { ejecutarAccionSobreTurno("ID turno: ", controlador::completarTurno, "Turno completado."); }

    private void eliminarTurno() {
        long id = lector.leerLong("ID turno: ");
        try {
            controlador.eliminarTurno(id);
            System.out.println("Turno eliminado correctamente.");
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarPorRangoFechas() {
        LocalDate inicio = lector.leerFecha("Fecha inicio (YYYY-MM-DD): ");
        LocalDate fin = lector.leerFecha("Fecha fin (YYYY-MM-DD): ");
        try {
            mostrarLista(controlador.listarPorRangoFechas(inicio, fin));
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void ejecutarAccionSobreTurno(String mensaje, AccionTurno accion, String confirmacion) {
        long id = lector.leerLong(mensaje);
        try {
            accion.ejecutar(id);
            System.out.println(confirmacion);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void mostrarLista(List<Turno> turnos) {
        if (turnos.isEmpty()) System.out.println("No hay turnos.");
        else turnos.forEach(System.out::println);
    }

    @FunctionalInterface
    private interface AccionTurno { Turno ejecutar(long id) throws ClinicaException; }
}
