package controller;

import entity.Cirujano;
import entity.General;
import entity.Odontologo;
import entity.Ortodoncista;
import service.ServicioOdontologo;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controlador de Odontólogos.
 * Patrón GRASP Controller: recibe las peticiones de la vista y delega al servicio.
 * Demuestra polimorfismo: crea subtipos de Odontologo según la especialidad elegida.
 */
public class ControladorOdontologo {

    private final ServicioOdontologo servicio;
    private final Scanner scanner;
    private long contadorId = 1L;

    public ControladorOdontologo(ServicioOdontologo servicio, Scanner scanner) {
        this.servicio = servicio;
        this.scanner = scanner;
    }

    public void menuOdontologos() {
        int opcion;
        do {
            System.out.println("\n======== GESTIÓN DE ODONTÓLOGOS ========");
            System.out.println("  1. Registrar nuevo odontólogo");
            System.out.println("  2. Buscar odontólogo por ID");
            System.out.println("  3. Buscar odontólogo por matrícula");
            System.out.println("  4. Listar todos los odontólogos");
            System.out.println("  5. Actualizar odontólogo");
            System.out.println("  6. Eliminar odontólogo");
            System.out.println("  0. Volver al menú principal");
            System.out.println("=========================================");
            System.out.print("  Seleccione una opción: ");
            int op = leerEntero();
            opcion = op;

            switch (opcion) {
                case 1 -> registrarOdontologo();
                case 2 -> buscarPorId();
                case 3 -> buscarPorMatricula();
                case 4 -> listarTodos();
                case 5 -> actualizarOdontologo();
                case 6 -> eliminarOdontologo();
                case 0 -> System.out.println("  Volviendo al menú principal...");
                default -> System.out.println("  Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void registrarOdontologo() {
        System.out.println("\n-- Registrar nuevo odontólogo --");
        System.out.print("  Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("  Matrícula: ");
        int matricula = leerEntero();

        System.out.println("  Especialidad (1=General / 2=Cirujano / 3=Ortodoncista): ");
        System.out.print("  Opción: ");
        int especialidad = leerEntero();

        Odontologo odontologo;
        long id = contadorId++;

        // Polimorfismo: se instancia el subtipo correspondiente según la especialidad
        switch (especialidad) {
            case 2 -> {
                System.out.print("  Tipo de cirugía: ");
                String tipoCirugia = scanner.nextLine().trim();
                odontologo = new Cirujano(id, nombre, apellido, matricula, tipoCirugia);
            }
            case 3 -> {
                System.out.print("  Tipo de aparato: ");
                String tipoAparato = scanner.nextLine().trim();
                odontologo = new Ortodoncista(id, nombre, apellido, matricula, tipoAparato);
            }
            default -> {
                System.out.print("  Tipo de consulta: ");
                String tipoConsulta = scanner.nextLine().trim();
                odontologo = new General(id, nombre, apellido, matricula, tipoConsulta);
            }
        }

        try {
            servicio.registrarOdontologo(odontologo);
            // Polimorfismo: toString() se resuelve en tiempo de ejecución según el tipo real
            System.out.println("  ✔ Odontólogo registrado exitosamente: " + odontologo);
        } catch (IllegalArgumentException e) {
            contadorId--; // revertir ID si falló
            System.out.println("  " + e.getMessage());
        }
    }

    private void buscarPorId() {
        System.out.print("  Ingrese el ID del odontólogo: ");
        long id = leerLong();
        Optional<Odontologo> odon = servicio.buscarPorId(id);
        if (odon.isPresent()) {
            System.out.println("  Odontólogo encontrado: " + odon.get());
        } else {
            System.out.println("  No se encontró un odontólogo con ID " + id + ".");
        }
    }

    private void buscarPorMatricula() {
        System.out.print("  Ingrese la matrícula: ");
        int matricula = leerEntero();
        Optional<Odontologo> odon = servicio.buscarPorMatricula(matricula);
        if (odon.isPresent()) {
            System.out.println("  Odontólogo encontrado: " + odon.get());
        } else {
            System.out.println("  No se encontró un odontólogo con matrícula " + matricula + ".");
        }
    }

    private void listarTodos() {
        List<Odontologo> lista = servicio.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  No hay odontólogos registrados.");
        } else {
            System.out.println("\n  --- Lista de Odontólogos ---");
            // Polimorfismo: toString() varía según Cirujano, Ortodoncista o General
            lista.forEach(o -> System.out.println("  " + o));
        }
    }

    private void actualizarOdontologo() {
        System.out.print("  ID del odontólogo a actualizar: ");
        long id = leerLong();
        System.out.print("  Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Nuevo apellido: ");
        String apellido = scanner.nextLine().trim();
        try {
            servicio.actualizarOdontologo(id, nombre, apellido);
            System.out.println("  ✔ Odontólogo actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  " + e.getMessage());
        }
    }

    private void eliminarOdontologo() {
        System.out.print("  ID del odontólogo a eliminar: ");
        long id = leerLong();
        try {
            servicio.eliminarOdontologo(id);
            System.out.println("  ✔ Odontólogo eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  " + e.getMessage());
        }
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
}
