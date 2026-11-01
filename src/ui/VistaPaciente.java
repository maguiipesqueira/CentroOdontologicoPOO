package ui;

import controller.ControladorPaciente;
import controller.DatosDomicilio;
import controller.DatosPaciente;
import controller.DatosPacienteActualizacion;
import entity.Paciente;
import entity.TipoHogar;
import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;

import java.util.List;
import java.util.Scanner;

public class VistaPaciente {
    private final ControladorPaciente controlador;
    private final LectorConsola lector;
    private final MenuConsola menu;

    public VistaPaciente(ControladorPaciente controlador, LectorConsola lector) {
        this.controlador = controlador;
        this.lector = lector;
        this.menu = new MenuConsola(lector);
    }

    public VistaPaciente(ControladorPaciente controlador, Scanner scanner) {
        this(controlador, new LectorConsola(scanner));
    }

    public void mostrarMenu() {
        menu.ejecutar("========= GESTIÓN DE PACIENTES =========", List.of(
                new OpcionMenu(1, "Registrar paciente", this::registrarPaciente),
                new OpcionMenu(2, "Buscar por ID", this::buscarPorId),
                new OpcionMenu(3, "Buscar por DNI", this::buscarPorDni),
                new OpcionMenu(4, "Listar pacientes", this::listarTodos),
                new OpcionMenu(5, "Actualizar paciente", this::actualizarPaciente),
                new OpcionMenu(6, "Eliminar paciente", this::eliminarPaciente)
        ));
    }

    private void registrarPaciente() {
        String nombre = lector.leerTexto("Nombre: ");
        String apellido = lector.leerTexto("Apellido: ");
        int dni = lector.leerEntero("DNI: ");
        String email = lector.leerTexto("Email: ");
        System.out.println("-- Domicilio --");
        String calle = lector.leerTexto("Calle: ");
        int numero = lector.leerEntero("Número: ");
        String localidad = lector.leerTexto("Localidad: ");
        String provincia = lector.leerTexto("Provincia: ");
        TipoHogar tipoHogar = leerTipoHogar();
        DatosDomicilio domicilio = new DatosDomicilio(calle, numero, localidad, provincia, tipoHogar);
        boolean obraSocial = lector.leerSiNo("¿Tiene obra social? (S/N): ");
        try {
            Paciente paciente = controlador.registrarPaciente(
                    new DatosPaciente(nombre, apellido, dni, email, domicilio, obraSocial));
            System.out.println("Paciente registrado: " + paciente);
        } catch (DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorId() {
        long id = lector.leerLong("ID: ");
        try {
            System.out.println("Paciente encontrado: " + controlador.buscarPorId(id));
        } catch (PacienteNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorDni() {
        int dni = lector.leerEntero("DNI: ");
        try {
            System.out.println("Paciente encontrado: " + controlador.buscarPorDni(dni));
        } catch (PacienteNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarTodos() {
        List<Paciente> pacientes = controlador.listarTodos();
        if (pacientes.isEmpty()) System.out.println("No hay pacientes registrados.");
        else pacientes.forEach(System.out::println);
    }

    private void actualizarPaciente() {
        long id = lector.leerLong("ID: ");
        String nombre = lector.leerTexto("Nuevo nombre: ");
        String apellido = lector.leerTexto("Nuevo apellido: ");
        String email = lector.leerTexto("Nuevo email: ");
        boolean obraSocial = lector.leerSiNo("¿Tiene obra social? (S/N): ");
        try {
            Paciente paciente = controlador.actualizarPaciente(id,
                    new DatosPacienteActualizacion(nombre, apellido, email, null, obraSocial));
            System.out.println("Paciente actualizado: " + paciente);
        } catch (PacienteNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarPaciente() {
        long id = lector.leerLong("ID: ");
        try {
            controlador.eliminarPaciente(id);
            System.out.println("Paciente eliminado correctamente.");
        } catch (PacienteNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private TipoHogar leerTipoHogar() {
        TipoHogar[] opciones = TipoHogar.values();
        while (true) {
            System.out.println("Tipo de hogar:");
            for (int i = 0; i < opciones.length; i++) {
                System.out.println((i + 1) + ". " + opciones[i].getNombreLegible());
            }
            int seleccion = lector.leerEntero("Opción: ");
            if (seleccion >= 1 && seleccion <= opciones.length) return opciones[seleccion - 1];
            System.out.println("Opción inválida.");
        }
    }
}
