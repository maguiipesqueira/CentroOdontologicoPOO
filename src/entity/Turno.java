package entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {

    private long id;
    private Paciente paciente;
    private Odontologo odontologo;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoTurno estado;

    public Turno(long id, Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = EstadoTurno.PENDIENTE;
    }

    public Turno() {}

    public long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Odontologo getOdontologo() { return odontologo; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public EstadoTurno getEstado() { return estado; }

    public void setId(long id) { this.id = id; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "TURNO #" + id +
               " | Paciente: " + paciente.getNombreCompleto() +
               " | Odontólogo: " + odontologo.getNombreCompleto() +
               " | Fecha: " + fecha + " Hora: " + hora +
               " | Estado: " + estado;
    }
}
