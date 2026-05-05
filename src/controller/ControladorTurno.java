
package controller;

import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ControladorTurno {

    private final ServicioTurno servicioTurno;
    private final ServicioPaciente servicioPaciente;
    private final ServicioOdontologo servicioOdontologo;

    public ControladorTurno(ServicioTurno servicioTurno,
                            ServicioPaciente servicioPaciente,
                            ServicioOdontologo servicioOdontologo) {
        this.servicioTurno = servicioTurno;
        this.servicioPaciente = servicioPaciente;
        this.servicioOdontologo = servicioOdontologo;
    }

    public Turno registrarTurno(long pacienteId, long odontologoId, LocalDate fecha, LocalTime hora) {
        Paciente paciente = servicioPaciente.buscarPorId(pacienteId);
        Odontologo odontologo = servicioOdontologo.buscarPorId(odontologoId);

        if (paciente == null || odontologo == null) {
            return null;
        }

        return servicioTurno.registrarTurno(paciente, odontologo, fecha, hora);
    }

    public Turno buscarPorId(long id) {
        return servicioTurno.buscarPorId(id);
    }

    public List<Turno> listarTodos() {
        return servicioTurno.listarTodos();
    }

    public List<Turno> listarPorPaciente(long pacienteId) {
        return servicioTurno.listarPorPaciente(pacienteId);
    }

    public List<Turno> listarPorOdontologo(long odontologoId) {
        return servicioTurno.listarPorOdontologo(odontologoId);
    }

    public Turno confirmarTurno(long id) {
        return servicioTurno.confirmarTurno(id);
    }

    public Turno cancelarTurno(long id) {
        return servicioTurno.cancelarTurno(id);
    }

    public Turno completarTurno(long id) {
        return servicioTurno.completarTurno(id);
    }

    public void eliminarTurno(long id) {
        servicioTurno.eliminarTurno(id);
    }
}