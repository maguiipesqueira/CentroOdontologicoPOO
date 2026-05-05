package view;

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

        Domicilio domicilioFati = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
        Paciente p1 = servPaciente.registrarPaciente("Fatima", "Persi", 46113226, "fatimap@gmail.com", domicilioFati);

        Domicilio domicilioTomi = new Domicilio("Gaboto", 849, "La Tablada, La Matanza", "Buenos Aires", TipoHogar.CASA);
        Paciente p2 = servPaciente.registrarPaciente("Tomas", "Barea", 46443494, "tomybareadiaz@gmail.com", domicilioTomi);


        Odontologo o1 = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        servOdontologo.registrarOdontologo(o1);

        ControladorPaciente ctrlPaciente = new ControladorPaciente(servPaciente);
        ControladorOdontologo ctrlOdontologo = new ControladorOdontologo(servOdontologo);
        ControladorTurno ctrlTurno = new ControladorTurno(servTurno, servPaciente, servOdontologo);

        VistaPaciente vistaPaciente = new VistaPaciente(ctrlPaciente, scanner);
        VistaOdontologo vistaOdontologo = new VistaOdontologo(ctrlOdontologo, scanner);
        VistaTurno vistaTurno = new VistaTurno(ctrlTurno, scanner);

        VistaPrincipal vistaPrincipal = new VistaPrincipal(
                vistaPaciente,
                vistaOdontologo,
                vistaTurno,
                scanner
        );

        vistaPrincipal.iniciar();

        scanner.close();
    }
}
