package main;

import controller.ControladorFactura;
import controller.ControladorOdontologo;
import controller.ControladorPaciente;
import controller.ControladorTurno;
import pago.Efectivo;
import pago.MedioPago;
import pago.MercadoPago;
import pago.Tarjeta;
import persistence.PersistenciaClinica;
import repository.RepositorioFactura;
import repository.RepositorioOdontologo;
import repository.RepositorioPaciente;
import repository.RepositorioTurno;
import service.CalculadorMonto;
import service.ServicioFactura;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;
import service.Tarifario;
import ui.LectorConsola;
import ui.VistaFactura;
import ui.VistaOdontologo;
import ui.VistaPaciente;
import ui.VistaPrincipal;
import ui.VistaTurno;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Composition root: construye servicios, controladores, persistencia e interfaces. */
public final class ConfiguradorAplicacion {

    private ConfiguradorAplicacion() {
    }

    public static ContextoAplicacion crear(Path directorioDatos) {
        Path directorio = directorioDatos.toAbsolutePath().normalize();

        RepositorioPaciente repositorioPaciente = new RepositorioPaciente();
        RepositorioOdontologo repositorioOdontologo = new RepositorioOdontologo();
        RepositorioTurno repositorioTurno = new RepositorioTurno();
        RepositorioFactura repositorioFactura = new RepositorioFactura();

        ServicioPaciente servicioPaciente = new ServicioPaciente(repositorioPaciente, repositorioTurno);
        ServicioOdontologo servicioOdontologo = new ServicioOdontologo(repositorioOdontologo, repositorioTurno);
        ServicioTurno servicioTurno = new ServicioTurno(repositorioTurno);

        List<MedioPago> mediosPago = List.of(new MercadoPago(), new Tarjeta(), new Efectivo());
        ServicioFactura servicioFactura = new ServicioFactura(repositorioFactura, servicioTurno,
                new Tarifario(), new CalculadorMonto(), mediosPago);

        ControladorPaciente controladorPaciente = new ControladorPaciente(servicioPaciente);
        ControladorOdontologo controladorOdontologo = new ControladorOdontologo(servicioOdontologo);
        ControladorTurno controladorTurno = new ControladorTurno(
                servicioTurno, servicioPaciente, servicioOdontologo);
        ControladorFactura controladorFactura = new ControladorFactura(servicioFactura);

        PersistenciaClinica persistencia = new PersistenciaClinica(directorio, repositorioPaciente,
                repositorioOdontologo, repositorioTurno, repositorioFactura, servicioFactura::codigoMedioPago);

        cargarDatos(persistencia, directorio, servicioPaciente, servicioOdontologo, controladorTurno);

        LectorConsola lector = new LectorConsola(new Scanner(System.in));
        VistaPrincipal vistaPrincipal = new VistaPrincipal(
                new VistaPaciente(controladorPaciente, lector),
                new VistaOdontologo(controladorOdontologo, lector),
                new VistaTurno(controladorTurno, lector),
                new VistaFactura(controladorFactura, lector),
                lector);

        return new ContextoAplicacion(controladorPaciente, controladorOdontologo,
                controladorTurno, controladorFactura, persistencia, vistaPrincipal);
    }

    private static void cargarDatos(PersistenciaClinica persistencia, Path directorio,
                                    ServicioPaciente servicioPaciente,
                                    ServicioOdontologo servicioOdontologo,
                                    ControladorTurno controladorTurno) {
        if (hayDatosGuardados(directorio)) {
            try {
                persistencia.cargar();
            } catch (IOException e) {
                System.err.println("No se pudieron cargar los datos: " + e.getMessage());
            }
            return;
        }

        try {
            DatosDemostracion.cargar(servicioPaciente, servicioOdontologo, controladorTurno);
        } catch (exception.ClinicaException e) {
            System.err.println("Error en carga inicial: " + e.getMessage());
        }
    }

    private static boolean hayDatosGuardados(Path directorio) {
        return Files.exists(directorio.resolve("pacientes.txt"))
                || Files.exists(directorio.resolve("odontologos.txt"))
                || Files.exists(directorio.resolve("turnos.txt"))
                || Files.exists(directorio.resolve("facturas.txt"));
    }
}
