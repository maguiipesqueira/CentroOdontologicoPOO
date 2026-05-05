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

    // crea el turno y lo deja pendiente
    public Turno(long id, Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = EstadoTurno.PENDIENTE;
    }

    // constructor vacío
    public Turno() {}

    // devuelve id del turno
    public long getId() { return id; }
    // devuelve paciente
    public Paciente getPaciente() { return paciente; }
    // devuelve odontólogo
    public Odontologo getOdontologo() { return odontologo; }
    // devuelve fecha
    public LocalDate getFecha() { return fecha; }
    // devuelve hora
    public LocalTime getHora() { return hora; }
    // devuelve estado
    public EstadoTurno getEstado() { return estado; }

    // cambia id
    public void setId(long id) { this.id = id; }
    // cambia paciente
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    // cambia odontólogo
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }
    // cambia fecha
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    // cambia hora
    public void setHora(LocalTime hora) { this.hora = hora; }
    // cambia estado
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

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
