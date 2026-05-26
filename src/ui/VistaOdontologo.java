package ui;

import controller.ControladorOdontologo;
import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;

import java.util.List;
import java.util.Scanner;

public class VistaOdontologo {

    private final ControladorOdontologo controlador;
    private final Scanner scanner;

    public VistaOdontologo(ControladorOdontologo controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("\n======== GESTIÓN DE ODONTÓLOGOS ========");
            System.out.println("1. Registrar odontólogo");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Buscar por matrícula");
            System.out.println("4. Listar odontólogos");
            System.out.println("5. Actualizar odontólogo");
            System.out.println("6. Eliminar odontólogo");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarOdontologo();
                case 2 -> buscarPorId();
                case 3 -> buscarPorMatricula();
                case 4 -> listarTodos();
                case 5 -> actualizarOdontologo();
                case 6 -> eliminarOdontologo();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    public void registrarOdontologo() {
        System.out.println("--- Registrar Odontólogo ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Matrícula: ");
        int matricula = leerEntero();

        Odontologo odontologo = new Odontologo(nombre, apellido, matricula);

        // Ahora rodeamos con try-catch la llamada al controlador para capturar la excepción
        try {
            controlador.registrarOdontologo(odontologo);
            System.out.println("Odontólogo registrado correctamente.");
        } catch (DatoInvalidoException e) {
            System.out.println("Error al registrar: " + e.getMessage());
        }
    }

    public void buscarPorId() {
        System.out.print("Ingrese ID a buscar: ");
        long id = leerLong();

        // Try-catch para capturar el error de "no encontrado" si el servicio falla
        try {
            Odontologo odontologo = controlador.buscarPorId(id);
            System.out.println("Encontrado: " + odontologo);
        } catch (OdontologoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void buscarPorMatricula() {
        System.out.print("Ingrese Matrícula: ");
        int matricula = leerEntero();

        // Try-catch para atrapar ambos posibles errores de negocio
        try {
            Odontologo odontologo = controlador.buscarPorMatricula(matricula);
            System.out.println("Encontrado: " + odontologo);
        } catch (OdontologoNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    // muestra todos los odontólogos registrados
    private void listarTodos() {

        List<Odontologo> odontologos = controlador.listarTodos();

        if (odontologos.isEmpty()) {
            System.out.println("No hay odontólogos registrados.");
            return;
        }

        System.out.println("\n--- LISTA DE ODONTÓLOGOS ---");

        for (Odontologo odontologo : odontologos) {
            System.out.println(odontologo);
        }
    }



    public void actualizarOdontologo() {
        System.out.print("ID del odontólogo a actualizar: ");
        long id = leerLong();
        System.out.print("Nuevo Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo Apellido: ");
        String apellido = scanner.nextLine();

        // Try-catch para errores de validación o si no se encuentra el objeto
        try {
            controlador.actualizarOdontologo(id, nombre, apellido);
            System.out.println("Actualización exitosa.");
        } catch (OdontologoNoEncontradoException | DatoInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void eliminarOdontologo() {
        System.out.print("ID del odontólogo a eliminar: ");
        long id = leerLong();

        // Try-catch para manejar el caso donde el ID no existe en el sistema
        try {
            controlador.eliminarOdontologo(id);
            System.out.println("Eliminado correctamente.");
        } catch (OdontologoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES DE LECTURA ---
    private long leerLong() {
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un ID numérico válido.");
            return 0;
        }
    }

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un valor numérico válido.");
            return 0;
        }
    }
}