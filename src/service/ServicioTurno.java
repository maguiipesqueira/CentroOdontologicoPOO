package service;

import entity.EstadoTurno;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import repository.RepositorioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de Turnos.
 * Aplica SRP: solo contiene lógica de negocio relacionada con Turno.
 * Aplica patrón GRASP Controller: orquesta entre paciente, odontólogo y turno.
 * Aplica patrón GRASP Creator: crea objetos Turno cuando tiene todos los datos.
 */
public class ServicioTurno {

    private final RepositorioTurno repositorio;

    public ServicioTurno(RepositorioTurno repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Registra un nuevo turno con validaciones de negocio.
     * Valida que no haya solapamiento de turno para el mismo odontólogo, fecha y hora.
     */
    public Turno registrarTurno(Paciente paciente, Odontologo odontologo,
                                 LocalDate fecha, LocalTime hora) {
        validarFecha(fecha);
        validarHora(hora);

        if (repositorio.existeTurnoSolapado(odontologo.getId(), fecha, hora)) {
            throw new IllegalArgumentException(
                    "ERROR: El odontólogo " + odontologo.getNombreCompleto() +
                    " ya tiene un turno asignado el " + fecha + " a las " + hora + ".");
        }

        long id = repositorio.siguienteId();
        // Patrón Creator: ServicioTurno tiene todos los datos para crear el Turno
        Turno turno = new Turno(id, paciente, odontologo, fecha, hora);
        repositorio.guardar(turno);
        return turno;
    }

    public Optional<Turno> buscarPorId(long id) {
        return repositorio.buscarPorId(id);
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

    /**
     * Cancela un turno por ID. Solo se puede cancelar si está PENDIENTE o CONFIRMADO.
     */
    public void cancelarTurno(long id) {
        Turno turno = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un turno con ID " + id + "."));

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new IllegalStateException("ERROR: El turno #" + id + " ya está cancelado.");
        }
        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new IllegalStateException("ERROR: No se puede cancelar un turno completado.");
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        repositorio.actualizar(turno);
    }

    /**
     * Confirma un turno PENDIENTE.
     */
    public void confirmarTurno(long id) {
        Turno turno = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un turno con ID " + id + "."));

        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new IllegalStateException(
                    "ERROR: Solo se pueden confirmar turnos en estado PENDIENTE.");
        }

        turno.setEstado(EstadoTurno.CONFIRMADO);
        repositorio.actualizar(turno);
    }

    /**
     * Marca un turno como completado.
     */
    public void completarTurno(long id) {
        Turno turno = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un turno con ID " + id + "."));

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new IllegalStateException("ERROR: No se puede completar un turno cancelado.");
        }

        turno.setEstado(EstadoTurno.COMPLETADO);
        repositorio.actualizar(turno);
    }

    /**
     * Elimina un turno por ID (solo si está cancelado).
     */
    public void eliminarTurno(long id) {
        Turno turno = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un turno con ID " + id + "."));

        if (turno.getEstado() != EstadoTurno.CANCELADO) {
            throw new IllegalStateException(
                    "ERROR: Solo se pueden eliminar turnos cancelados.");
        }

        repositorio.eliminar(id);
    }

    // --- Validaciones ---

    private void validarFecha(LocalDate fecha) {
        if (fecha == null)
            throw new IllegalArgumentException("ERROR: La fecha no puede ser nula.");
        if (fecha.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("ERROR: No se pueden registrar turnos en fechas pasadas.");
    }

    private void validarHora(LocalTime hora) {
        if (hora == null)
            throw new IllegalArgumentException("ERROR: La hora no puede ser nula.");
        LocalTime apertura = LocalTime.of(8, 0);
        LocalTime cierre = LocalTime.of(20, 0);
        if (hora.isBefore(apertura) || hora.isAfter(cierre)) {
            throw new IllegalArgumentException(
                    "ERROR: Los turnos solo se pueden registrar entre las 08:00 y las 20:00.");
        }
    }
}
