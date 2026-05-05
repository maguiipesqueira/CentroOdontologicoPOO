package view;

import controller.ControladorPaciente;
import entity.Domicilio;
import entity.Paciente;
import entity.TipoHogar;

import java.util.List;
import java.util.Scanner;

public class VistaPaciente {

    private final ControladorPaciente controlador;
    private final Scanner scanner;

    public VistaPaciente(ControladorPaciente controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("\n========= GESTIÓN DE PACIENTES =========");
            System.out.println("1. Registrar paciente");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Buscar por DNI");
            System.out.println("4. Listar pacientes");
            System.out.println("5. Actualizar paciente");
            System.out.println("6. Eliminar paciente");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarPaciente();
                case 2 -> buscarPorId();
                case 3 -> buscarPorDni();
                case 4 -> listarTodos();
                case 5 -> actualizarPaciente();
                case 6 -> eliminarPaciente();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private void registrarPaciente() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();

        System.out.print("DNI: ");
        int dni = leerEntero();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.println("-- Domicilio --");
        System.out.print("Calle: ");
        String calle = scanner.nextLine().trim();

        System.out.print("Número: ");
        int numero = leerEntero();

        System.out.print("Localidad: ");
        String localidad = scanner.nextLine().trim();

        System.out.print("Provincia: ");
        String provincia = scanner.nextLine().trim();

        TipoHogar tipoHogar = leerTipoHogar();

        Domicilio domicilio = new Domicilio(calle, numero, localidad, provincia, tipoHogar);

        Paciente paciente = controlador.registrarPaciente(nombre, apellido, dni, email, domicilio);

        if (paciente != null) {
            System.out.println("Paciente registrado: " + paciente);
        } else {
            System.out.println("No se pudo registrar el paciente.");
        }
    }

    private void buscarPorId() {
        System.out.print("ID: ");
        long id = leerLong();

        Paciente paciente = controlador.buscarPorId(id);

        if (paciente != null) {
            System.out.println("Paciente encontrado: " + paciente);
        } else {
            System.out.println("No existe paciente con ese ID.");
        }
    }

    private void buscarPorDni() {
        System.out.print("DNI: ");
        int dni = leerEntero();

        Paciente paciente = controlador.buscarPorDni(dni);

        if (paciente != null) {
            System.out.println("Paciente encontrado: " + paciente);
        } else {
            System.out.println("No existe paciente con ese DNI.");
        }
    }

    private void listarTodos() {
        List<Paciente> pacientes = controlador.listarTodos();

        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
        } else {
            for (Paciente p : pacientes) {
                System.out.println(p);
            }
        }
    }

    private void actualizarPaciente() {
        System.out.print("ID: ");
        long id = leerLong();

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Nuevo apellido: ");
        String apellido = scanner.nextLine().trim();

        System.out.print("Nuevo email: ");
        String email = scanner.nextLine().trim();

        Paciente paciente = controlador.actualizarPaciente(id, nombre, apellido, email, null);

        if (paciente != null) {
            System.out.println("Paciente actualizado: " + paciente);
        } else {
            System.out.println("No se pudo actualizar el paciente.");
        }
    }

    private void eliminarPaciente() {
        System.out.print("ID: ");
        long id = leerLong();

        controlador.eliminarPaciente(id);
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

    private TipoHogar leerTipoHogar() {
        while (true) {
            System.out.print("Tipo de hogar (CASA / DEPARTAMENTO / PH): ");
            String texto = scanner.nextLine().trim().toUpperCase();

            try {
                return TipoHogar.valueOf(texto);
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo inválido.");
            }
        }
    }
}
    