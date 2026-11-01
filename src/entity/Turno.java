package entity;

import exception.DatoInvalidoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Turno de un paciente con un odontologo. Su identidad es el id.
 * Los datos del turno no cambian una vez creado; lo unico que evoluciona es el estado,
 * y solo a traves de confirmar(), cancelar(), completar() y marcarAusente(), que validan la transicion.
 */
public class Turno {

    private final long id;
    private final Paciente paciente;
    private final Odontologo odontologo;
    private final LocalDate fecha;
    private final LocalTime hora;
    private final MotivoConsulta motivoConsulta; // puede ser null si el turno se registro sin motivo
    private EstadoTurno estado;

    // constructor unico. Para un turno nuevo el estado es PENDIENTE; la persistencia lo usa para reconstruir
    // un turno guardado en cualquier estado. El motivo es opcional (null = sin motivo)
    public Turno(long id, Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora,
                 EstadoTurno estado, MotivoConsulta motivoConsulta) {
        this.id = id;
        this.paciente = Objects.requireNonNull(paciente, "El paciente del turno no puede ser null");
        this.odontologo = Objects.requireNonNull(odontologo, "El odontologo del turno no puede ser null");
        this.fecha = Objects.requireNonNull(fecha, "La fecha del turno no puede ser null");
        this.hora = Objects.requireNonNull(hora, "La hora del turno no puede ser null");
        this.estado = Objects.requireNonNull(estado, "El estado del turno no puede ser null");
        this.motivoConsulta = motivoConsulta;
    }

    public long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Odontologo getOdontologo() { return odontologo; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public EstadoTurno getEstado() { return estado; }

    // motivo de consulta, vacio si el turno se registro sin motivo
    public Optional<MotivoConsulta> getMotivoConsulta() { return Optional.ofNullable(motivoConsulta); }

    // PENDIENTE -> CONFIRMADO
    public void confirmar() throws DatoInvalidoException {
        estado = estado.confirmar();
    }

    // PENDIENTE o CONFIRMADO -> CANCELADO
    public void cancelar() throws DatoInvalidoException {
        estado = estado.cancelar();
    }

    // PENDIENTE o CONFIRMADO -> COMPLETADO
    public void completar() throws DatoInvalidoException {
        estado = estado.completar();
    }

    // PENDIENTE o CONFIRMADO -> AUSENTE (el paciente no se presento)
    public void marcarAusente() throws DatoInvalidoException {
        estado = estado.marcarAusente();
    }

    // dos turnos son el mismo si tienen el mismo id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Turno)) return false;
        return id == ((Turno) o).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    // texto con los datos del turno
    @Override
    public String toString() {
        return "TURNO #" + id +
                " | Paciente: " + paciente.getNombreCompleto() +
                " | Odontólogo: " + odontologo.getNombreCompleto() +
                " | Fecha: " + fecha + " Hora: " + hora +
                " | Estado: " + estado;
    }
}