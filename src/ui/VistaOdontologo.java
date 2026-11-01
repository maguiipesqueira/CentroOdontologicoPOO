package ui;

import controller.ControladorOdontologo;
import controller.DatosOdontologo;
import entity.Especialidad;
import entity.MotivoConsulta;
import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;

import java.util.List;
import java.util.Scanner;

public class VistaOdontologo {
    private final ControladorOdontologo controlador;
    private final LectorConsola lector;
    private final MenuConsola menu;

    public VistaOdontologo(ControladorOdontologo controlador, LectorConsola lector) {
        this.controlador = controlador;
        this.lector = lector;
        this.menu = new MenuConsola(lector);
    }

    public VistaOdontologo(ControladorOdontologo controlador, Scanner scanner) {
        this(controlador, new LectorConsola(scanner));
    }

    public void mostrarMenu() {
        menu.ejecutar("======== GESTIÓN DE ODONTÓLOGOS ========", List.of(
                new OpcionMenu(1, "Registrar odontólogo", this::registrarOdontologo),
                new OpcionMenu(2, "Buscar por ID", this::buscarPorId),
                new OpcionMenu(3, "Buscar por matrícula", this::buscarPorMatricula),
                new OpcionMenu(4, "Listar odontólogos", this::listarTodos),
                new OpcionMenu(5, "Actualizar odontólogo", this::actualizarOdontologo),
                new OpcionMenu(6, "Eliminar odontólogo", this::eliminarOdontologo)
        ));
    }

    private void registrarOdontologo() {
        System.out.println("--- Registrar odontólogo ---");
        String nombre = lector.leerTexto("Nombre: ");
        String apellido = lector.leerTexto("Apellido: ");
        int matricula = lector.leerEntero("Matrícula: ");
        Especialidad especialidad = leerEspecialidad();
        MotivoConsulta subespecialidad = leerSubespecialidad(especialidad);
        try {
            Odontologo odontologo = controlador.registrarOdontologo(
                    new DatosOdontologo(nombre, apellido, matricula, especialidad, subespecialidad));
            System.out.println("Odontólogo registrado: " + odontologo);
        } catch (DatoInvalidoException e) {
            System.out.println("Error al registrar: " + e.getMessage());
        }
    }

    private void buscarPorId() {
        long id = lector.leerLong("Ingrese ID a buscar: ");
        try {
            System.out.println("Encontrado: " + controlador.buscarPorId(id));
        } catch (OdontologoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorMatricula() {
        int matricula = lector.leerEntero("Ingrese matrícula: ");
        try {
            System.out.println("Encontrado: " + controlador.buscarPorMatricula(matricula));
        } catch (OdontologoNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarTodos() {
        List<Odontologo> odontologos = controlador.listarTodos();
        if (odontologos.isEmpty()) {
            System.out.println("No hay odontólogos registrados.");
            return;
        }
        System.out.println("--- LISTA DE ODONTÓLOGOS ---");
        odontologos.forEach(System.out::println);
    }

    private void actualizarOdontologo() {
        long id = lector.leerLong("ID del odontólogo a actualizar: ");
        String nombre = lector.leerTexto("Nuevo nombre: ");
        String apellido = lector.leerTexto("Nuevo apellido: ");
        int matricula = lector.leerEntero("Nueva matrícula: ");
        Especialidad especialidad = leerEspecialidad();
        MotivoConsulta subespecialidad = leerSubespecialidad(especialidad);
        try {
            Odontologo actualizado = controlador.actualizarOdontologo(id,
                    new DatosOdontologo(nombre, apellido, matricula, especialidad, subespecialidad));
            System.out.println("Odontólogo actualizado: " + actualizado);
        } catch (OdontologoNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarOdontologo() {
        long id = lector.leerLong("ID del odontólogo a eliminar: ");
        try {
            controlador.eliminarOdontologo(id);
            System.out.println("Eliminado correctamente.");
        } catch (OdontologoNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Especialidad leerEspecialidad() {
        Especialidad[] opciones = Especialidad.values();
        while (true) {
            System.out.println("Especialidades:");
            for (int i = 0; i < opciones.length; i++) {
                System.out.println((i + 1) + ". " + opciones[i].getNombre());
            }
            int seleccion = lector.leerEntero("Especialidad: ");
            if (seleccion >= 1 && seleccion <= opciones.length) return opciones[seleccion - 1];
            System.out.println("Opción inválida.");
        }
    }

    private MotivoConsulta leerSubespecialidad(Especialidad especialidad) {
        List<MotivoConsulta> opciones = especialidad.getMotivosPosibles();
        while (true) {
            System.out.println("Tratamientos / subespecialidades de " + especialidad.getNombre() + ":");
            for (int i = 0; i < opciones.size(); i++) {
                System.out.println((i + 1) + ". " + opciones.get(i).getNombreLegible());
            }
            int seleccion = lector.leerEntero("Seleccione una opción: ");
            if (seleccion >= 1 && seleccion <= opciones.size()) return opciones.get(seleccion - 1);
            System.out.println("Opción inválida.");
        }
    }
}
