package view;

import controller.ControladorOdontologo;
import controller.ControladorPaciente;
import controller.ControladorTurno;
import entity.*;
import repository.RepositorioOdontologo;
import repository.RepositorioPaciente;
import repository.RepositorioTurno;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Punto de entrada de la aplicación.
 * Solo instancia dependencias, carga datos de prueba y lanza el menú principal.
 * Aplica SRP: no contiene lógica de negocio ni de persistencia.
 */
public class Main {

    public static void main(String[] args) {

        RepositorioPaciente repoPaciente     = new RepositorioPaciente();
        RepositorioOdontologo repoOdontologo = new RepositorioOdontologo();
        RepositorioTurno repoTurno           = new RepositorioTurno();

        ServicioPaciente servPaciente       = new ServicioPaciente(repoPaciente);
        ServicioOdontologo servOdontologo   = new ServicioOdontologo(repoOdontologo);
        ServicioTurno servTurno             = new ServicioTurno(repoTurno);

        Scanner scanner = new Scanner(System.in);

        ControladorPaciente ctrlPaciente     = new ControladorPaciente(servPaciente, scanner);
        ControladorOdontologo ctrlOdontologo = new ControladorOdontologo(servOdontologo, scanner);
        ControladorTurno ctrlTurno           = new ControladorTurno(servTurno, servPaciente, servOdontologo, scanner);

        cargarDatosDePrueba(servPaciente, servOdontologo, servTurno);

        int opcion;
        do {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE GESTIÓN ODONTOLÓGICA v2.0   ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Gestión de Pacientes                 ║");
            System.out.println("║  2. Gestión de Odontólogos               ║");
            System.out.println("║  3. Gestión de Turnos                    ║");
            System.out.println("║  0. Salir                                ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("  Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> ctrlPaciente.menuPacientes();
                case 2 -> ctrlOdontologo.menuOdontologos();
                case 3 -> ctrlTurno.menuTurnos();
                case 0 -> System.out.println("\n  Sistema cerrado. ¡Hasta pronto!");
                default -> System.out.println("  Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void cargarDatosDePrueba(ServicioPaciente servPaciente,
                                            ServicioOdontologo servOdontologo,
                                            ServicioTurno servTurno) {

        System.out.println("\n  [Cargando datos de prueba...]");

        Odontologo od1 = new Cirujano(1L, "Guillermina", "Cotrone", 1234, TipoCirugia.EXTRACCION_MUELA);
        Odontologo od2 = new Ortodoncista(2L, "Tomas", "Barea", 5678, TipoOrtodoncia.BRACKETS_CERAMICOS);
        Odontologo od3 = new General(3L, "Camilo", "Hunter", 9012, TipoConsulta.LIMPIEZA);

        servOdontologo.registrarOdontologo(od1);
        servOdontologo.registrarOdontologo(od2);
        servOdontologo.registrarOdontologo(od3);

        Domicilio dom1 = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
        Domicilio dom2 = new Domicilio("Rosas", 120, "Temperley", "Buenos Aires", TipoHogar.PH);
        Domicilio dom3 = new Domicilio("Avenida Mayo", 450, "Bariloche", "Río Negro", TipoHogar.DEPARTAMENTO);

        servPaciente.registrarPaciente("Fátima", "Persi", 46113226, "fatimap@gmail.com", dom1);
        servPaciente.registrarPaciente("Magali", "Pesqueira", 354473, "maga@mail.com", dom2);
        servPaciente.registrarPaciente("Jesús", "Cambronero", 4448544, "jesus@mail.com", dom3);

        Paciente p1 = servPaciente.buscarPorDni(46113226);
        Paciente p2 = servPaciente.buscarPorDni(354473);
        Paciente p3 = servPaciente.buscarPorDni(4448544);

        if (p1 != null) {
            servTurno.registrarTurno(p1, od1, LocalDate.of(2026, 5, 20), LocalTime.of(15, 30));
        }

        if (p2 != null) {
            servTurno.registrarTurno(p2, od2, LocalDate.of(2026, 5, 21), LocalTime.of(10, 0));
        }

        if (p3 != null) {
            servTurno.registrarTurno(p3, od3, LocalDate.of(2026, 5, 22), LocalTime.of(18, 15));
        }

        System.out.println("  [Datos de prueba cargados correctamente]\n");
    }
}
