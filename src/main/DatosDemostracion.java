package main;

import controller.ControladorTurno;
import entity.Cirujano;
import entity.Domicilio;
import entity.General;
import entity.Odontologo;
import entity.Ortodoncista;
import entity.TipoCirugia;
import entity.TipoConsulta;
import entity.TipoHogar;
import entity.TipoOrtodoncia;
import service.ServicioOdontologo;
import service.ServicioPaciente;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/** Carga los datos de ejemplo usados al iniciar una instalación sin archivos previos. */
final class DatosDemostracion {

    private DatosDemostracion() {
    }

    static void cargar(ServicioPaciente servicioPaciente,
                       ServicioOdontologo servicioOdontologo,
                       ControladorTurno controladorTurno) throws exception.ClinicaException {
        registrarPacientes(servicioPaciente);
        registrarOdontologos(servicioOdontologo);
        registrarTurnos(controladorTurno);
    }

    private static void registrarPacientes(ServicioPaciente servicio) throws exception.ClinicaException {
        servicio.registrarPaciente("Fatima", "Persi", 46113226, "fatimap@gmail.com",
                new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA), true);
        servicio.registrarPaciente("Tomas", "Barea", 46443494, "tomybareadiaz@gmail.com",
                new Domicilio("Gaboto", 849, "La Tablada, La Matanza", "Buenos Aires", TipoHogar.CASA), false);
        servicio.registrarPaciente("Maria", "Garcia", 38221456, "mariagarcia@gmail.com",
                new Domicilio("San Martin", 1200, "Moron", "Buenos Aires", TipoHogar.DEPARTAMENTO), true);
        servicio.registrarPaciente("Lucas", "Fernandez", 41789632, "lucasfernandez@gmail.com",
                new Domicilio("Belgrano", 430, "San Justo", "Buenos Aires", TipoHogar.CASA), false);
    }

    private static void registrarOdontologos(ServicioOdontologo servicio) throws exception.ClinicaException {
        Odontologo general = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        Odontologo ortodoncista = new Ortodoncista(2L, "Nahuel", "Torres", 2245,
                TipoOrtodoncia.BRACKETS_METALICOS);
        Odontologo cirujano = new Cirujano(3L, "Blas", "Pesce", 3367, TipoCirugia.EXTRACCION_MUELA);
        servicio.registrarOdontologo(general);
        servicio.registrarOdontologo(ortodoncista);
        servicio.registrarOdontologo(cirujano);
    }

    private static void registrarTurnos(ControladorTurno controlador) throws exception.ClinicaException {
        LocalDate hoy = LocalDate.now();
        LocalDate lunesEstaSemana = hoy.with(DayOfWeek.MONDAY);
        if (hoy.getDayOfWeek() == DayOfWeek.SATURDAY || hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lunesEstaSemana = lunesEstaSemana.plusWeeks(1);
        }
        LocalDate lunesSemanaSiguiente = lunesEstaSemana.plusWeeks(1);
        LocalDate lunesSemanaSubsiguiente = lunesEstaSemana.plusWeeks(2);

        controlador.registrarTurno(1L, 1L, lunesEstaSemana, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        var turno2 = controlador.registrarTurno(2L, 1L, lunesEstaSemana.plusDays(1),
                LocalTime.of(11, 0), TipoConsulta.CONTROL);
        controlador.confirmarTurno(turno2.getId());
        controlador.registrarTurno(1L, 2L, lunesEstaSemana.plusDays(2),
                LocalTime.of(10, 0), TipoOrtodoncia.BRACKETS_METALICOS);
        var turno4 = controlador.registrarTurno(3L, 3L, lunesEstaSemana.plusDays(2),
                LocalTime.of(13, 0), TipoCirugia.EXTRACCION_MUELA);
        controlador.confirmarTurno(turno4.getId());
        controlador.completarTurno(turno4.getId());
        var turno5 = controlador.registrarTurno(4L, 1L, lunesEstaSemana.plusDays(3),
                LocalTime.of(8, 0), TipoConsulta.URGENCIA);
        controlador.cancelarTurno(turno5.getId());
        var turno6 = controlador.registrarTurno(2L, 2L, lunesEstaSemana.plusDays(4),
                LocalTime.of(14, 0), TipoOrtodoncia.BRACKETS_CERAMICOS);
        controlador.confirmarTurno(turno6.getId());

        controlador.registrarTurno(3L, 1L, lunesSemanaSiguiente, LocalTime.of(9, 0), TipoConsulta.BLANQUEAMIENTO);
        controlador.registrarTurno(4L, 2L, lunesSemanaSiguiente.plusDays(1),
                LocalTime.of(10, 0), TipoOrtodoncia.BRACKETS_METALICOS);
        controlador.registrarTurno(1L, 3L, lunesSemanaSiguiente.plusDays(2),
                LocalTime.of(11, 0), TipoCirugia.IMPLANTE);
        controlador.registrarTurno(2L, 1L, lunesSemanaSiguiente.plusDays(3),
                LocalTime.of(12, 0), TipoConsulta.CONTROL);
        controlador.registrarTurno(4L, 3L, lunesSemanaSubsiguiente, LocalTime.of(9, 0), TipoCirugia.CIRUGIA_ENCIA);
        controlador.registrarTurno(3L, 2L, lunesSemanaSubsiguiente.plusDays(1),
                LocalTime.of(10, 0), TipoOrtodoncia.BRACKETS_CERAMICOS);
        controlador.registrarTurno(1L, 1L, lunesSemanaSubsiguiente.plusDays(4),
                LocalTime.of(15, 0), TipoConsulta.LIMPIEZA);
    }
}
