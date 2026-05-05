package controller;

import entity.Domicilio;
import entity.Paciente;
import entity.TipoHogar;
import service.ServicioPaciente;

import java.util.List;
import java.util.Scanner;

public class ControladorPaciente {

    private final ServicioPaciente servicio;
    private final Scanner scanner;

    // guarda el servicio y el scanner
    public ControladorPaciente(ServicioPaciente servicio, Scanner scanner) {
        this.servicio = servicio;
        this.scanner = scanner;
    }

    // menú de pacientes hasta que pongan 0
    public void menuPacientes() {
        int opcion;
        do {
            System.out.println("\n========= GESTIÓN DE PACIENTES =========");
            System.out.println("  1. Registrar nuevo paciente");
            System.out.println("  2. Buscar paciente por ID");
            System.out.println("  3. Buscar paciente por DNI");
            System.out.println("  4. Listar todos los pacientes");
            System.out.println("  5. Actualizar paciente");
            System.out.println("  6. Eliminar paciente");
            System.out.println("  0. Volver al menú principal");
            System.out.println("=========================================");
            System.out.print("  Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarPaciente();
                case 2 -> buscarPorId();
                case 3 -> buscarPorDni();
                case 4 -> listarTodos();
                case 5 -> actualizarPaciente();
                case 6 -> eliminarPaciente();
                case 0 -> System.out.println("  Volviendo al menú principal...");
                default -> System.out.println("  Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // pide todo y manda a registrar al servicio
    private void registrarPaciente() {
        System.out.println("\n-- Registrar nuevo paciente --");
        System.out.print("  Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("  DNI: ");
        int dni = leerEntero();
        System.out.print("  Email: ");
        String email = scanner.nextLine().trim();

        System.out.println("  -- Domicilio --");
        System.out.print("  Calle: ");
        String calle = scanner.nextLine().trim();
        System.out.print("  Número: ");
        int numero = leerEntero();
        System.out.print("  Localidad: ");
        String localidad = scanner.nextLine().trim();
        System.out.print("  Provincia: ");
        String provincia = scanner.nextLine().trim();
        System.out.println("  Tipo de hogar (CASA / DEPARTAMENTO / PH): ");
        TipoHogar tipoHogar = leerTipoHogar();

        Domicilio domicilio = new Domicilio(calle, numero, localidad, provincia, tipoHogar);

        Paciente nuevo = servicio.registrarPaciente(nombre, apellido, dni, email, domicilio);

        if (nuevo != null) {
            System.out.println("  ✔ Paciente registrado exitosamente: " + nuevo);
        } else {
            System.out.println("  No se pudo registrar el paciente.");
        }
    }

    // busca por id y lo muestra
    private void buscarPorId() {
        System.out.print("  Ingrese el ID del paciente: ");
        long id = leerLong();

        Paciente paciente = servicio.buscarPorId(id);

        if (paciente != null) {
            System.out.println("  Paciente encontrado: " + paciente);
        } else {
            System.out.println("  No se encontró un paciente con ID " + id + ".");
        }
    }

    // busca por dni y lo muestra
    private void buscarPorDni() {
        System.out.print("  Ingrese el DNI del paciente: ");
        int dni = leerEntero();

        Paciente paciente = servicio.buscarPorDni(dni);

        if (paciente != null) {
            System.out.println("  Paciente encontrado: " + paciente);
        } else {
            System.out.println("  No se encontró un paciente con DNI " + dni + ".");
        }
    }

    // imprime la lista de pacientes
    private void listarTodos() {
        List<Paciente> lista = servicio.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  No hay pacientes registrados.");
        } else {
            System.out.println("\n  --- Lista de Pacientes ---");
            lista.forEach(p -> System.out.println("  " + p));
        }
    }

    // cambia nombre apellido y mail (domicilio no lo toca acá, va null)
    private void actualizarPaciente() {
        System.out.print("  ID del paciente a actualizar: ");
        long id = leerLong();
        System.out.print("  Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Nuevo apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("  Nuevo email: ");
        String email = scanner.nextLine().trim();

        Paciente actualizado = servicio.actualizarPaciente(id, nombre, apellido, email, null);

        if (actualizado != null) {
            System.out.println("  ✔ Paciente actualizado correctamente.");
        } else {
            System.out.println("  No se pudo actualizar el paciente.");
        }
    }

    // borra el paciente con ese id
    private void eliminarPaciente() {
        System.out.print("  ID del paciente a eliminar: ");
        long id = leerLong();

        servicio.eliminarPaciente(id);
    }

    // lee int de la consola, si falla repregunta
    private int leerEntero() {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("  Valor inválido. Ingrese un número: ");
            }
        }
    }

    // lee long de la consola
    private long leerLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Valor inválido. Ingrese un número: ");
            }
        }
    }

    // lee casa / departamento / ph y lo pasa a enum
    private TipoHogar leerTipoHogar() {
        while (true) {
            System.out.print("  Tipo: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return TipoHogar.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.print("  Opción inválida. Ingrese CASA, DEPARTAMENTO o PH: ");
            }
        }
    }
}
