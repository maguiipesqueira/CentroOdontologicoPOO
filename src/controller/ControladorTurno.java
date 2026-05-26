package controller;

import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import exception.ClinicaException;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import exception.PacienteNoEncontradoException;
import exception.OdontologoNoEncontradoException;

public class ControladorTurno {

    private final ServicioTurno servicioTurno;
    private final ServicioPaciente servicioPaciente;
    private final ServicioOdontologo servicioOdontologo;

    // constructor guarda los tres servicios
    public ControladorTurno(ServicioTurno servicioTurno,
                            ServicioPaciente servicioPaciente,
                            ServicioOdontologo servicioOdontologo) {
        this.servicioTurno = servicioTurno;
        this.servicioPaciente = servicioPaciente;
        this.servicioOdontologo = servicioOdontologo;
    }

    // registra dejando pasar las excepciones si el paciente o el odontologo no existen
    public Turno registrarTurno(long pacienteId, long odontologoId, LocalDate fecha, LocalTime hora)
            throws ClinicaException {

        Paciente paciente = servicioPaciente.buscarPorId(pacienteId);
        Odontologo odontologo = servicioOdontologo.buscarPorId(odontologoId);

        return servicioTurno.registrarTurno(paciente, odontologo, fecha, hora);
    }

    // busca un turno por id
    public Turno buscarPorId(long id) throws ClinicaException {
        return servicioTurno.buscarPorId(id);
    }

    // lista todos los turnos registrados
    public List<Turno> listarTodos() {
        return servicioTurno.listarTodos();
    }

    // lista los turnos filtrados por paciente usando tu metodo stream
    public List<Turno> listarPorPaciente(long pacienteId) {
        return servicioTurno.listarPorPaciente(pacienteId);
    }

    // lista los turnos filtrados por odontologo
    public List<Turno> listarPorOdontologo(long odontologoId) {
        return servicioTurno.listarPorOdontologo(odontologoId);
    }

    //lista los turnos por rango de fechas
    public List<Turno> listarPorRangoFechas(LocalDate inicio, LocalDate fin) throws ClinicaException {
        return servicioTurno.listarPorRangoFechas(inicio, fin);
    }


    // confirma un turno por id
    public Turno confirmarTurno(long id) throws ClinicaException {
        return servicioTurno.confirmarTurno(id);
    }

    // cancela un turno por id
    public Turno cancelarTurno(long id)  throws ClinicaException{
        return servicioTurno.cancelarTurno(id);
    }

    // completa un turno por id
    public Turno completarTurno(long id) throws ClinicaException{
        return servicioTurno.completarTurno(id);
    }

    // elimina un turno por id
    public void eliminarTurno(long id) throws ClinicaException{
        servicioTurno.eliminarTurno(id);
    }
}