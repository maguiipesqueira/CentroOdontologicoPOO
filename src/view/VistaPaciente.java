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

    // registra capturando si los datos no cumplen con los formatos requeridos
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

        // captura errores de formato o dni duplicado
        try {
            Paciente paciente = controlador.registrarPaciente(nombre, apellido, dni, email, domicilio);

            if (paciente != null) {
                System.out.println("Paciente registrado: " + paciente);
            } else {
                System.out.println("No se pudo registrar el paciente.");
            }
        } catch (exception.DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // busca capturando si el id ingresado no existe en el sistema
    private void buscarPorId() {
        System.out.print("ID: ");
        long id = leerLong();

        // captura error de id inexistente
        try {
            Paciente paciente = controlador.buscarPorId(id);
            System.out.println("Paciente encontrado: " + paciente);
        } catch (exception.PacienteNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // busca capturando si el dni no existe o es un numero negativo
    private void buscarPorDni() {
        System.out.print("DNI: ");
        int dni = leerEntero();

        // captura error de dni inexistente o invalido
        try {
            Paciente paciente = controlador.buscarPorDni(dni);
            System.out.println("Paciente encontrado: " + paciente);
        } catch (exception.PacienteNoEncontradoException | exception.DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
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

    // actualiza capturando si el id no existe o si los datos nuevos vienen mal
    private void actualizarPaciente() {
        System.out.print("ID: ");
        long id = leerLong();

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Nuevo apellido: ");
        String apellido = scanner.nextLine().trim();

        System.out.print("Nuevo email: ");
        String email = scanner.nextLine().trim();

        // captura errores de id inexistente o datos incorrectos
        try {
            Paciente paciente = controlador.actualizarPaciente(id, nombre, apellido, email, null);

            if (paciente != null) {
                System.out.println("Paciente actualizado: " + paciente);
            } else {
                System.out.println("No se pudo actualizar el paciente.");
            }
        } catch (exception.PacienteNoEncontradoException | exception.DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // elimina capturando si se ingresa un id que no existe
    private void eliminarPaciente() {
        System.out.print("ID: ");
        long id = leerLong();

        // captura error si el paciente a borrar no se encuentra
        try {
            controlador.eliminarPaciente(id);
            System.out.println("Paciente eliminado correctamente.");
        } catch (exception.PacienteNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
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