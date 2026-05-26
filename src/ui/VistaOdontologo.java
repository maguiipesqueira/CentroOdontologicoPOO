package ui;

import controller.ControladorOdontologo;
import entity.*;

import java.util.List;
import java.util.Scanner;

public class VistaOdontologo {

    private final ControladorOdontologo controlador;
    private final Scanner scanner;
    private long contadorId = 1L;

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

    private void registrarOdontologo() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();

        System.out.print("Matrícula: ");
        int matricula = leerEntero();

        System.out.println("Especialidad: 1-General / 2-Cirujano / 3-Ortodoncista");
        int especialidad = leerEntero();

        long id = contadorId++;
        Odontologo odontologo;

        switch (especialidad) {
            case 2 -> odontologo = new Cirujano(id, nombre, apellido, matricula, leerTipoCirugia());
            case 3 -> odontologo = new Ortodoncista(id, nombre, apellido, matricula, leerTipoOrtodoncia());
            default -> odontologo = new General(id, nombre, apellido, matricula, leerTipoConsulta());
        }

        Odontologo registrado = controlador.registrarOdontologo(odontologo);

        if (registrado != null) {
            System.out.println("Odontólogo registrado: " + registrado);
        } else {
            contadorId--;
            System.out.println("No se pudo registrar el odontólogo.");
        }
    }

    private void buscarPorId() {
        System.out.print("ID: ");
        long id = leerLong();

        Odontologo odontologo = controlador.buscarPorId(id);

        if (odontologo != null) {
            System.out.println("Odontólogo encontrado: " + odontologo);
        } else {
            System.out.println("No existe odontólogo con ese ID.");
        }
    }

    private void buscarPorMatricula() {
        System.out.print("Matrícula: ");
        int matricula = leerEntero();

        Odontologo odontologo = controlador.buscarPorMatricula(matricula);

        if (odontologo != null) {
            System.out.println("Odontólogo encontrado: " + odontologo);
        } else {
            System.out.println("No existe odontólogo con esa matrícula.");
        }
    }

    private void listarTodos() {
        List<Odontologo> lista = controlador.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("No hay odontólogos registrados.");
        } else {
            for (Odontologo o : lista) {
                System.out.println(o);
            }
        }
    }

    private void actualizarOdontologo() {
        System.out.print("ID: ");
        long id = leerLong();

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Nuevo apellido: ");
        String apellido = scanner.nextLine().trim();

        Odontologo odontologo = controlador.actualizarOdontologo(id, nombre, apellido);

        if (odontologo != null) {
            System.out.println("Odontólogo actualizado: " + odontologo);
        } else {
            System.out.println("No se pudo actualizar el odontólogo.");
        }
    }

    private void eliminarOdontologo() {
        System.out.print("ID: ");
        long id = leerLong();

        controlador.eliminarOdontologo(id);
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

    private TipoCirugia leerTipoCirugia() {
        System.out.println("1. Extracción de muela");
        System.out.println("2. Implante");
        System.out.println("3. Cirugía de encía");
        System.out.println("4. Cirugía maxilofacial");

        return switch (leerEntero()) {
            case 2 -> TipoCirugia.IMPLANTE;
            case 3 -> TipoCirugia.CIRUGIA_ENCIA;
            case 4 -> TipoCirugia.CIRUGIA_MAXILOFACIAL;
            default -> TipoCirugia.EXTRACCION_MUELA;
        };
    }

    private TipoOrtodoncia leerTipoOrtodoncia() {
        System.out.println("1. Brackets metálicos");
        System.out.println("2. Brackets cerámicos");
        System.out.println("3. Invisalign");
        System.out.println("4. Lingual");

        return switch (leerEntero()) {
            case 2 -> TipoOrtodoncia.BRACKETS_CERAMICOS;
            default -> TipoOrtodoncia.BRACKETS_METALICOS;
        };
    }

    private TipoConsulta leerTipoConsulta() {
        System.out.println("1. Limpieza");
        System.out.println("2. Control");
        System.out.println("3. Urgencia");
        System.out.println("4. Blanqueamiento");

        return switch (leerEntero()) {
            case 2 -> TipoConsulta.CONTROL;
            case 3 -> TipoConsulta.URGENCIA;
            case 4 -> TipoConsulta.BLANQUEAMIENTO;
            default -> TipoConsulta.LIMPIEZA;
        };
    }
}
    