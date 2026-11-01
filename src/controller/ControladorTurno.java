package controller;

import entity.MotivoConsulta;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import exception.ClinicaException;
import exception.DatoInvalidoException;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Casos de uso de agenda preparados para las necesidades de consola y Swing. */
public class ControladorTurno {

    private static final Comparator<Turno> ORDEN_AGENDA =
            Comparator.comparing(Turno::getFecha).thenComparing(Turno::getHora);

    private final ServicioTurno servicioTurno;
    private final ServicioPaciente servicioPaciente;
    private final ServicioOdontologo servicioOdontologo;
    private final Clock clock;

    public ControladorTurno(ServicioTurno servicioTurno,
                            ServicioPaciente servicioPaciente,
                            ServicioOdontologo servicioOdontologo) {
        this(servicioTurno, servicioPaciente, servicioOdontologo, Clock.systemDefaultZone());
    }

    public ControladorTurno(ServicioTurno servicioTurno, ServicioPaciente servicioPaciente,
                            ServicioOdontologo servicioOdontologo, Clock clock) {
        this.servicioTurno = Objects.requireNonNull(servicioTurno);
        this.servicioPaciente = Objects.requireNonNull(servicioPaciente);
        this.servicioOdontologo = Objects.requireNonNull(servicioOdontologo);
        this.clock = Objects.requireNonNull(clock);
    }

    public Turno registrarTurno(SolicitudTurno solicitud) throws ClinicaException {
        if (solicitud == null) throw new DatoInvalidoException("Turno", "La solicitud de turno es obligatoria.");
        Paciente paciente = servicioPaciente.buscarPorId(solicitud.pacienteId());
        Odontologo odontologo = servicioOdontologo.buscarPorId(solicitud.odontologoId());
        return servicioTurno.registrarTurno(paciente, odontologo, solicitud.fecha(), solicitud.hora(), solicitud.motivoConsulta());
    }

    public Turno registrarTurno(long pacienteId, long odontologoId, LocalDate fecha, LocalTime hora)
            throws ClinicaException {
        return registrarTurno(new SolicitudTurno(pacienteId, odontologoId, fecha, hora, null));
    }

    public Turno registrarTurno(long pacienteId, long odontologoId, LocalDate fecha, LocalTime hora,
                                MotivoConsulta motivoConsulta) throws ClinicaException {
        return registrarTurno(new SolicitudTurno(pacienteId, odontologoId, fecha, hora, motivoConsulta));
    }

    public Turno buscarPorId(long id) throws ClinicaException { return servicioTurno.buscarPorId(id); }
    public List<Turno> listarTodos() { return ordenar(servicioTurno.listarTodos()); }
    public List<Turno> listarPorPaciente(long pacienteId) { return ordenar(servicioTurno.listarPorPaciente(pacienteId)); }
    public List<Turno> listarPorOdontologo(long odontologoId) { return ordenar(servicioTurno.listarPorOdontologo(odontologoId)); }
    public List<Turno> listarPorRangoFechas(LocalDate inicio, LocalDate fin) throws ClinicaException {
        return ordenar(servicioTurno.listarPorRangoFechas(inicio, fin));
    }

    /** Turnos de la fecha actual según el reloj inyectado. */
    public List<Turno> listarTurnosDeHoy() {
        LocalDate hoy = LocalDate.now(clock);
        try {
            return ordenar(servicioTurno.listarPorRangoFechas(hoy, hoy));
        } catch (DatoInvalidoException imposible) {
            throw new IllegalStateException("No se pudo consultar la agenda del día.", imposible);
        }
    }

    /** Turnos de la semana calendario de la fecha indicada (lunes a domingo). */
    public List<Turno> listarTurnosDeLaSemana(LocalDate fechaReferencia) throws DatoInvalidoException {
        if (fechaReferencia == null) throw new DatoInvalidoException("Fecha", "La fecha de referencia es obligatoria.");
        LocalDate desde = fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate hasta = desde.plusDays(6);
        return ordenar(servicioTurno.listarPorRangoFechas(desde, hasta));
    }

    public ResumenSemanalTurnos obtenerResumenSemanal(LocalDate fechaReferencia) throws DatoInvalidoException {
        if (fechaReferencia == null) throw new DatoInvalidoException("Fecha", "La fecha de referencia es obligatoria.");
        LocalDate desde = fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate hasta = desde.plusDays(6);
        List<Turno> turnos = servicioTurno.listarPorRangoFechas(desde, hasta);
        return ResumenSemanalTurnos.calcular(desde, hasta, turnos);
    }

    /** Aplica todos los criterios informados; los criterios nulos se ignoran. */
    public List<Turno> filtrar(FiltroTurnos filtro) throws DatoInvalidoException {
        FiltroTurnos criterios = filtro == null ? FiltroTurnos.todos() : filtro;
        List<Turno> base;
        if (criterios.desde() != null && criterios.hasta() != null) {
            base = servicioTurno.listarPorRangoFechas(criterios.desde(), criterios.hasta());
        } else if (criterios.pacienteId() != null) {
            base = servicioTurno.listarPorPaciente(criterios.pacienteId());
        } else if (criterios.odontologoId() != null) {
            base = servicioTurno.listarPorOdontologo(criterios.odontologoId());
        } else {
            base = servicioTurno.listarTodos();
        }
        return ordenar(base.stream()
                .filter(t -> criterios.pacienteId() == null || t.getPaciente().getId() == criterios.pacienteId())
                .filter(t -> criterios.odontologoId() == null || t.getOdontologo().getId() == criterios.odontologoId())
                .filter(t -> criterios.estado() == null || t.getEstado() == criterios.estado())
                .filter(t -> criterios.desde() == null || !t.getFecha().isBefore(criterios.desde()))
                .filter(t -> criterios.hasta() == null || !t.getFecha().isAfter(criterios.hasta()))
                .toList());
    }

    public Turno confirmarTurno(long id) throws ClinicaException { return servicioTurno.confirmarTurno(id); }
    public Turno cancelarTurno(long id) throws ClinicaException { return servicioTurno.cancelarTurno(id); }
    public Turno completarTurno(long id) throws ClinicaException { return servicioTurno.completarTurno(id); }
    public Turno marcarAusente(long id) throws ClinicaException { return servicioTurno.marcarAusente(id); }
    public void eliminarTurno(long id) throws ClinicaException { servicioTurno.eliminarTurno(id); }
    public List<Paciente> listarPacientes() { return servicioPaciente.listarTodos(); }
    public List<Odontologo> listarOdontologos() { return servicioOdontologo.listarTodos(); }

    private List<Turno> ordenar(List<Turno> turnos) {
        return turnos.stream().sorted(ORDEN_AGENDA).toList();
    }
}
