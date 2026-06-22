package main;

import entity.*;
import controller.ControladorOdontologo;
import controller.ControladorPaciente;
import controller.ControladorTurno;
import repository.RepositorioOdontologo;
import repository.RepositorioPaciente;
import repository.RepositorioTurno;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;
import ui.VistaOdontologo;
import ui.VistaPaciente;
import ui.VistaPrincipal;
import ui.VistaTurno;
import uiSwing.VentanaPrincipal;
import uiSwing.PersistenciaPaciente;
import uiSwing.PersistenciaTurno;
import uiSwing.PersistenciaOdontologo;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        RepositorioPaciente repoPaciente = new RepositorioPaciente();
        RepositorioOdontologo repoOdontologo = new RepositorioOdontologo();
        RepositorioTurno repoTurno = new RepositorioTurno();

        ServicioPaciente servPaciente = new ServicioPaciente(repoPaciente);
        ServicioOdontologo servOdontologo = new ServicioOdontologo(repoOdontologo);
        ServicioTurno servTurno = new ServicioTurno(repoTurno);

        ControladorPaciente ctrlPaciente = new ControladorPaciente(servPaciente);
        ControladorOdontologo ctrlOdontologo = new ControladorOdontologo(servOdontologo);
        ControladorTurno ctrlTurno = new ControladorTurno(servTurno, servPaciente, servOdontologo);

        // si ya existe el archivo carga desde ahi, sino carga los datos de prueba
        if (new java.io.File("pacientes.txt").exists() || new java.io.File("odontologos.txt").exists()) {
            PersistenciaPaciente.cargar(servPaciente);
            PersistenciaOdontologo.cargar(servOdontologo); // Carga la persistencia en txt
            PersistenciaTurno.cargar(servTurno, servPaciente, servOdontologo);
        } else {
            try {
                Domicilio domicilioFati = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
                servPaciente.registrarPaciente("Fatima", "Persi", 46113226, "fatimap@gmail.com", domicilioFati);

                Domicilio domicilioTomi = new Domicilio("Gaboto", 849, "La Tablada, La Matanza", "Buenos Aires", TipoHogar.CASA);
                servPaciente.registrarPaciente("Tomas", "Barea", 46443494, "tomybareadiaz@gmail.com", domicilioTomi);

                Domicilio domicilioMaria = new Domicilio("San Martin", 1200, "Moron", "Buenos Aires", TipoHogar.DEPARTAMENTO);
                servPaciente.registrarPaciente("Maria", "Garcia", 38221456, "mariagarcia@gmail.com", domicilioMaria);

                Domicilio domicilioLucas = new Domicilio("Belgrano", 430, "San Justo", "Buenos Aires", TipoHogar.CASA);
                servPaciente.registrarPaciente("Lucas", "Fernandez", 41789632, "lucasfernandez@gmail.com", domicilioLucas);

                Odontologo o1 = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
                servOdontologo.registrarOdontologo(o1);

                Odontologo o2 = new Ortodoncista(2L, "Nahuel", "Torres", 2245, TipoOrtodoncia.BRACKETS_METALICOS);
                servOdontologo.registrarOdontologo(o2);

                Odontologo o3 = new Cirujano(3L, "Blas", "Pesce", 3367, TipoCirugia.EXTRACCION_MUELA);
                servOdontologo.registrarOdontologo(o3);

                // turnos de prueba solo la primera vez
                java.time.LocalDate hoy = java.time.LocalDate.now();
                java.time.LocalDate lunesEstaSemana = hoy.with(java.time.DayOfWeek.MONDAY);
                if (hoy.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || hoy.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                    lunesEstaSemana = lunesEstaSemana.plusWeeks(1);
                }
                java.time.LocalDate lunesSemanaSiguiente = lunesEstaSemana.plusWeeks(1);
                java.time.LocalDate lunesSemanaSubsiguiente = lunesEstaSemana.plusWeeks(2);

                Turno t1 = ctrlTurno.registrarTurno(1L, 1L, lunesEstaSemana, java.time.LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
                Turno t2 = ctrlTurno.registrarTurno(2L, 1L, lunesEstaSemana.plusDays(1), java.time.LocalTime.of(11, 0), TipoConsulta.CONTROL);
                ctrlTurno.confirmarTurno(t2.getId());
                Turno t3 = ctrlTurno.registrarTurno(1L, 2L, lunesEstaSemana.plusDays(2), java.time.LocalTime.of(10, 0), TipoOrtodoncia.BRACKETS_METALICOS);
                Turno t4 = ctrlTurno.registrarTurno(3L, 3L, lunesEstaSemana.plusDays(2), java.time.LocalTime.of(13, 0), TipoCirugia.EXTRACCION_MUELA);
                ctrlTurno.confirmarTurno(t4.getId());
                ctrlTurno.completarTurno(t4.getId());
                Turno t5 = ctrlTurno.registrarTurno(4L, 1L, lunesEstaSemana.plusDays(3), java.time.LocalTime.of(8, 0), TipoConsulta.URGENCIA);
                ctrlTurno.cancelarTurno(t5.getId());
                Turno t6 = ctrlTurno.registrarTurno(2L, 2L, lunesEstaSemana.plusDays(4), java.time.LocalTime.of(14, 0), TipoOrtodoncia.BRACKETS_CERAMICOS);
                ctrlTurno.confirmarTurno(t6.getId());
                ctrlTurno.registrarTurno(3L, 1L, lunesSemanaSiguiente, java.time.LocalTime.of(9, 0), TipoConsulta.BLANQUEAMIENTO);
                ctrlTurno.registrarTurno(4L, 2L, lunesSemanaSiguiente.plusDays(1), java.time.LocalTime.of(10, 0), TipoOrtodoncia.BRACKETS_METALICOS);
                ctrlTurno.registrarTurno(1L, 3L, lunesSemanaSiguiente.plusDays(2), java.time.LocalTime.of(11, 0), TipoCirugia.IMPLANTE);
                ctrlTurno.registrarTurno(2L, 1L, lunesSemanaSiguiente.plusDays(3), java.time.LocalTime.of(12, 0), TipoConsulta.CONTROL);
                ctrlTurno.registrarTurno(4L, 3L, lunesSemanaSubsiguiente, java.time.LocalTime.of(9, 0), TipoCirugia.CIRUGIA_ENCIA);
                ctrlTurno.registrarTurno(3L, 2L, lunesSemanaSubsiguiente.plusDays(1), java.time.LocalTime.of(10, 0), TipoOrtodoncia.BRACKETS_CERAMICOS);
                ctrlTurno.registrarTurno(1L, 1L, lunesSemanaSubsiguiente.plusDays(4), java.time.LocalTime.of(15, 0), TipoConsulta.LIMPIEZA);

            } catch (exception.ClinicaException e) {
                System.out.println("Error en carga inicial: " + e.getMessage());
            }
        }

        VistaPaciente vistaPaciente = new VistaPaciente(ctrlPaciente, scanner);
        VistaOdontologo vistaOdontologo = new VistaOdontologo(ctrlOdontologo, scanner);
        VistaTurno vistaTurno = new VistaTurno(ctrlTurno, scanner);

        VistaPrincipal vistaPrincipal = new VistaPrincipal(
                vistaPaciente,
                vistaOdontologo,
                vistaTurno,
                scanner
        );

        SwingUtilities.invokeLater(() -> {
            // Se le envían los tres controladores para que no tire error de null
            VentanaPrincipal ventana = new VentanaPrincipal(ctrlTurno, ctrlPaciente, ctrlOdontologo);
            ventana.iniciar();
        });

        // scanner.close(); <- No cierro el scanner si van a usar consola luego
    }
}