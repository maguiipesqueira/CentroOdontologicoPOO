package controller;

import entity.*;
import service.ServicioOdontologo;

import java.util.List;
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
                TipoCirugia tipoCirugia = leerTipoCirugia();
                odontologo = new Cirujano(id, nombre, apellido, matricula, tipoCirugia);
            }
            case 3 -> {
                TipoOrtodoncia tipoOrtodoncia = leerTipoOrtodoncia();
                odontologo = new Ortodoncista(id, nombre, apellido, matricula, tipoOrtodoncia);
            }
            default -> {
                TipoConsulta tipoConsulta = leerTipoConsulta();
                odontologo = new General(id, nombre, apellido, matricula, tipoConsulta);
            }
        }

        Odontologo resultado = servicio.registrarOdontologo(odontologo);

        if (resultado != null) {
            // Polimorfismo: toString() se resuelve en tiempo de ejecución según el tipo real
            System.out.println("  ✔ Odontólogo registrado exitosamente: " + resultado);
        } else {
            contadorId--; // revertir ID si falló
            System.out.println("  No se pudo registrar el odontólogo.");
        }
    }

    private void buscarPorId() {
        System.out.print("  Ingrese el ID del odontólogo: ");
        long id = leerLong();

        Odontologo odon = servicio.buscarPorId(id);

        if (odon != null) {
            System.out.println("  Odontólogo encontrado: " + odon);
        } else {
            System.out.println("  No se encontró un odontólogo con ID " + id + ".");
        }
    }

    private void buscarPorMatricula() {
        System.out.print("  Ingrese la matrícula: ");
        int matricula = leerEntero();

        Odontologo odon = servicio.buscarPorMatricula(matricula);

        if (odon != null) {
            System.out.println("  Odontólogo encontrado: " + odon);
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

        Odontologo actualizado = servicio.actualizarOdontologo(id, nombre, apellido);

        if (actualizado != null) {
            System.out.println("  ✔ Odontólogo actualizado correctamente.");
        } else {
            System.out.println("  No se pudo actualizar el odontólogo.");
        }
    }

    private void eliminarOdontologo() {
        System.out.print("  ID del odontólogo a eliminar: ");
        long id = leerLong();

        servicio.eliminarOdontologo(id);
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


    private TipoCirugia leerTipoCirugia() {
        while (true) {
            System.out.println("  Tipos de cirugía:");
            System.out.println("  1. Extracción de muela");
            System.out.println("  2. Implante");
            System.out.println("  3. Cirugía de encía");
            System.out.println("  4. Cirugía maxilofacial");
            System.out.print("  Opción: ");

            int opcion = leerEntero();

            switch (opcion) {
                case 1:
                    return TipoCirugia.EXTRACCION_MUELA;
                case 2:
                    return TipoCirugia.IMPLANTE;
                case 3:
                    return TipoCirugia.CIRUGIA_ENCIA;
                case 4:
                    return TipoCirugia.CIRUGIA_MAXILOFACIAL;
                default:
                    System.out.println("  Opción inválida.");
            }
        }

    }

    private TipoOrtodoncia leerTipoOrtodoncia() {
        while (true) {
            System.out.println("  Tipos de ortodoncia:");
            System.out.println("  1. Brackets metálicos");
            System.out.println("  2. Brackets cerámicos");
            System.out.println("  3. Invisalign");
            System.out.println("  4. Lingual");
            System.out.print("  Opción: ");

            int opcion = leerEntero();

            switch (opcion) {
                case 1: return TipoOrtodoncia.BRACKETS_METALICOS;
                case 2: return TipoOrtodoncia.BRACKETS_CERAMICOS;
                default: System.out.println("  Opción inválida.");
            }
        }
    }



    private TipoConsulta leerTipoConsulta() {
        while (true) {
            System.out.println("  Tipos de consulta:");
            System.out.println("  1. Limpieza");
            System.out.println("  2. Control");
            System.out.println("  3. Urgencia");
            System.out.println("  4. Blanqueamiento");
            System.out.print("  Opción: ");

            int opcion = leerEntero();

            switch (opcion) {
                case 1: return TipoConsulta.LIMPIEZA;
                case 2: return TipoConsulta.CONTROL;
                case 3: return TipoConsulta.URGENCIA;
                case 4: return TipoConsulta.BLANQUEAMIENTO;
                default: System.out.println("  Opción inválida.");
            }
        }
    }



}


