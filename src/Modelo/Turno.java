package Modelo;
import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
    //id, paciente, odontólogo, fecha, hora.
    private int id;
    private Paciente paciente;
    private Odontologo odontologo;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoTurno estado;

    //constructor por defecto
    public Turno(){}
    //Contructor con parametros
    public Turno(int id, Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.fecha = fecha;
        this.hora = hora;
    }

    //getter y setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Odontologo getOdontologo() {
        return odontologo;
    }

    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    public EstadoTurno getEstado(){
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    // cambia el estado a confirmado
    public void confirmarTurno() {
        this.estado = EstadoTurno.CONFIRMADO;
    }
    //cambia el estado a cancelado
    public void cancelarTurno() {
        this.estado = EstadoTurno.CANCELADO;
    }

    //Metodo toString()
    @Override
    public String toString() {
        return "TURNO #" + id + " | Paciente: " + paciente.getNombreCompleto() +
                " | Odontólogo: " + odontologo.getNombreCompleto() +
                " | Fecha: " + fecha + " Hora: " + hora + " | Estado: " + estado;
    }

}
