package service;

import entity.EstadoTurno;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import repository.RepositorioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ServicioTurno {

    private final RepositorioTurno repositorio;

    // guarda el repo de turnos
    public ServicioTurno(RepositorioTurno repositorio) {
        this.repositorio = repositorio;
    }

    // crea el turno si la fecha y hora están ok y no pisa otro turno del mismo odontólogo
    public Turno registrarTurno(Paciente paciente, Odontologo odontologo,
                                LocalDate fecha, LocalTime hora) {

        if (!validarFecha(fecha)) return null;
        if (!validarHora(hora)) return null;

        if (repositorio.existeTurnoSolapado(odontologo.getId(), fecha, hora)) {
            System.out.println("Error: el odontólogo ya tiene un turno en esa fecha y hora.");
            return null;
        }

        long id = repositorio.siguienteId();

        Turno turno = new Turno(id, paciente, odontologo, fecha, hora);
        repositorio.guardar(turno);

        return turno;
    }

    // busca por id
    public Turno buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    // todos los turnos
    public List<Turno> listarTodos() {
        return repositorio.listarTodos();
    }

    // turnos de un paciente
    public List<Turno> listarPorPaciente(long pacienteId) {
        return repositorio.buscarPorPaciente(pacienteId);
    }

    // turnos de un odontólogo
    public List<Turno> listarPorOdontologo(long odontologoId) {
        return repositorio.buscarPorOdontologo(odontologoId);
    }

    // pasa a cancelado si se puede
    public Turno cancelarTurno(long id) {
        Turno turno = buscarPorId(id);

        if (turno == null) {
            System.out.println("Error: no existe un turno con ese ID.");
            return null;
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            System.out.println("Error: el turno ya está cancelado.");
            return null;
        }

        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            System.out.println("Error: no se puede cancelar un turno completado.");
            return null;
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        repositorio.actualizar(turno);

        return turno;
    }

    // solo deja si estaba pendiente
    public Turno confirmarTurno(long id) {
        Turno turno = buscarPorId(id);

        if (turno == null) {
            System.out.println("Error: no existe un turno con ese ID.");
            return null;
        }

        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            System.out.println("Error: solo se pueden confirmar turnos pendientes.");
            return null;
        }

        turno.setEstado(EstadoTurno.CONFIRMADO);
        repositorio.actualizar(turno);

        return turno;
    }

    // marca como completado
    public Turno completarTurno(long id) {
        Turno turno = buscarPorId(id);

        if (turno == null) {
            System.out.println("Error: no existe un turno con ese ID.");
            return null;
        }

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            System.out.println("Error: no se puede completar un turno cancelado.");
            return null;
        }

        turno.setEstado(EstadoTurno.COMPLETADO);
        repositorio.actualizar(turno);

        return turno;
    }

    // borra de verdad solo si ya estaba cancelado
    public void eliminarTurno(long id) {
        Turno turno = buscarPorId(id);

        if (turno == null) {
            System.out.println("Error: no existe un turno con ese ID.");
            return;
        }

        if (turno.getEstado() != EstadoTurno.CANCELADO) {
            System.out.println("Error: solo se pueden eliminar turnos cancelados.");
            return;
        }

        repositorio.eliminar(id);
    }

    // fecha no null y no del pasado
    private boolean validarFecha(LocalDate fecha) {
        if (fecha == null) {
            System.out.println("Error: la fecha no puede ser nula.");
            return false;
        }

        if (fecha.isBefore(LocalDate.now())) {
            System.out.println("Error: no se pueden registrar turnos en fechas pasadas.");
            return false;
        }

        return true;
    }

    // hora entre 8 y 20
    private boolean validarHora(LocalTime hora) {
        if (hora == null) {
            System.out.println("Error: la hora no puede ser nula.");
            return false;
        }

        LocalTime apertura = LocalTime.of(8, 0);
        LocalTime cierre = LocalTime.of(20, 0);

        if (hora.isBefore(apertura) || hora.isAfter(cierre)) {
            System.out.println("Error: los turnos solo se pueden registrar entre las 08:00 y las 20:00.");
            return false;
        }

        return true;
    }
}
