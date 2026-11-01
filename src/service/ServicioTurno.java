package service;

import entity.EstadoTurno;
import entity.MotivoConsulta;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import exception.DatoInvalidoException;
import exception.TurnoYaReservadoException;
import exception.TurnoNoEncontradoException;
import repository.GeneradorIds;
import repository.RepositorioTurno;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/** Casos de uso y reglas de negocio para la agenda de turnos. */
public class ServicioTurno {

    private static final HorarioAtencion HORARIO_PREDETERMINADO =
            new HorarioAtencion(LocalTime.of(8, 0), LocalTime.of(20, 0));

    private final RepositorioTurno repositorio;
    private final Clock clock;
    private final HorarioAtencion horarioAtencion;
    private final GeneradorIds generadorIds = new GeneradorIds();

    /** Configuración de producción: reloj del sistema y horario habitual de 08:00 a 20:00. */
    public ServicioTurno(RepositorioTurno repositorio) {
        this(repositorio, Clock.systemDefaultZone(), HORARIO_PREDETERMINADO);
    }

    /** Permite controlar el tiempo en pruebas sin modificar el horario habitual. */
    public ServicioTurno(RepositorioTurno repositorio, Clock clock) {
        this(repositorio, clock, HORARIO_PREDETERMINADO);
    }

    /** Permite configurar tanto el reloj como el horario de atención. */
    public ServicioTurno(RepositorioTurno repositorio, Clock clock, HorarioAtencion horarioAtencion) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio de turnos no puede ser null.");
        this.clock = Objects.requireNonNull(clock, "El reloj no puede ser null.");
        this.horarioAtencion = Objects.requireNonNull(horarioAtencion, "El horario de atención no puede ser null.");
    }

    public Turno registrarTurno(Paciente paciente, Odontologo odontologo,
                                LocalDate fecha, LocalTime hora)
            throws DatoInvalidoException, TurnoYaReservadoException {
        return registrarTurno(paciente, odontologo, fecha, hora, null);
    }

    public Turno registrarTurno(Paciente paciente, Odontologo odontologo,
                                LocalDate fecha, LocalTime hora, MotivoConsulta motivoConsulta)
            throws DatoInvalidoException, TurnoYaReservadoException {
        validarPaciente(paciente);
        validarOdontologo(odontologo);
        validarFecha(fecha);
        validarHora(fecha, hora);

        if (existeTurnoSolapado(odontologo.getId(), fecha, hora)) {
            throw new TurnoYaReservadoException(odontologo.getNombreCompleto(), fecha, hora);
        }

        long id = generadorIds.siguienteId(() -> repositorio.listarTodos().stream()
                .mapToLong(Turno::getId)
                .max()
                .orElse(0L));

        Turno turno = new Turno(id, paciente, odontologo, fecha, hora,
                EstadoTurno.PENDIENTE, motivoConsulta);
        repositorio.guardar(turno);
        return turno;
    }

    /** Un turno cancelado libera el horario; cualquier otro estado lo mantiene ocupado. */
    private boolean existeTurnoSolapado(long odontologoId, LocalDate fecha, LocalTime hora) {
        return repositorio.buscarPorOdontologoYFecha(odontologoId, fecha).stream()
                .anyMatch(turno -> turno.getHora().equals(hora)
                        && turno.getEstado() != EstadoTurno.CANCELADO);
    }

    public Turno buscarPorId(long id) throws TurnoNoEncontradoException {
        return repositorio.buscarPorId(id).orElseThrow(() -> new TurnoNoEncontradoException(id));
    }

    public List<Turno> listarTodos() {
        return repositorio.listarTodos();
    }

    public List<Turno> listarPorPaciente(long pacienteId) {
        return repositorio.buscarPorPaciente(pacienteId);
    }

    public List<Turno> listarPorOdontologo(long odontologoId) {
        return repositorio.buscarPorOdontologo(odontologoId);
    }

    public List<Turno> listarPorRangoFechas(LocalDate inicio, LocalDate fin) throws DatoInvalidoException {
        if (inicio == null || fin == null) {
            throw new DatoInvalidoException("Fechas", "Las fechas no pueden ser nulas.");
        }
        if (inicio.isAfter(fin)) {
            throw new DatoInvalidoException("Fechas", "La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        return repositorio.buscarPorRangoFechas(inicio, fin);
    }

    public Turno cancelarTurno(long id) throws DatoInvalidoException {
        Turno turno = buscarPorId(id);
        turno.cancelar();
        repositorio.actualizar(turno);
        return turno;
    }

    public Turno confirmarTurno(long id) throws DatoInvalidoException {
        Turno turno = buscarPorId(id);
        turno.confirmar();
        repositorio.actualizar(turno);
        return turno;
    }

    public Turno completarTurno(long id) throws DatoInvalidoException {
        Turno turno = buscarPorId(id);
        turno.completar();
        repositorio.actualizar(turno);
        return turno;
    }

    public void eliminarTurno(long id) throws DatoInvalidoException {
        Turno turno = buscarPorId(id);
        if (turno.getEstado() != EstadoTurno.CANCELADO) {
            throw new DatoInvalidoException("Estado", "Solo se pueden eliminar turnos cancelados, cancela el turno para poder eliminarlo.");
        }
        repositorio.eliminar(id);
    }

    private void validarPaciente(Paciente paciente) throws DatoInvalidoException {
        if (paciente == null) {
            throw new DatoInvalidoException("Paciente", "El paciente no puede estar vacío.");
        }
    }

    private void validarOdontologo(Odontologo odontologo) throws DatoInvalidoException {
        if (odontologo == null) {
            throw new DatoInvalidoException("Odontólogo", "El odontólogo no puede estar vacío.");
        }
    }

    private void validarFecha(LocalDate fecha) throws DatoInvalidoException {
        if (fecha == null) {
            throw new DatoInvalidoException("Fecha", "La fecha no puede ser nula.");
        }
        if (fecha.isBefore(LocalDate.now(clock))) {
            throw new DatoInvalidoException("Fecha", "No se pueden registrar turnos en fechas pasadas.");
        }
    }

    private void validarHora(LocalDate fecha, LocalTime hora) throws DatoInvalidoException {
        if (hora == null) {
            throw new DatoInvalidoException("Hora", "La hora no puede ser nula.");
        }
        if (!horarioAtencion.contiene(hora)) {
            throw new DatoInvalidoException("Hora", "Los turnos solo se pueden registrar entre las "
                    + horarioAtencion.apertura() + " y las " + horarioAtencion.cierre() + ".");
        }
        if (fecha.equals(LocalDate.now(clock)) && !hora.isAfter(LocalTime.now(clock))) {
            throw new DatoInvalidoException("Hora", "No se pueden registrar turnos en una hora que ya pasó.");
        }
    }

}
