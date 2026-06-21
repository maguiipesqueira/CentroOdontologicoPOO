package service;

import entity.EstadoTurno;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import exception.ClinicaException;
import exception.DatoInvalidoException;
import exception.TurnoYaReservadoException;
import repository.RepositorioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioTurno {

    private final RepositorioTurno repositorio;

    // guarda el repo de turnos
    public ServicioTurno(RepositorioTurno repositorio) {

        this.repositorio = repositorio;
    }

    // crea el turno si la fecha y hora están ok y no pisa otro turno del mismo odontólogo
    public Turno registrarTurno(Paciente paciente, Odontologo odontologo,
                                LocalDate fecha, LocalTime hora)
            throws DatoInvalidoException, TurnoYaReservadoException {

        return registrarTurno(paciente, odontologo, fecha, hora, null);
    }

    // misma logica que registrarTurno, pero permite indicar el motivo de consulta elegido
    public Turno registrarTurno(Paciente paciente, Odontologo odontologo,
                                LocalDate fecha, LocalTime hora, Object motivoConsulta)
            throws DatoInvalidoException, TurnoYaReservadoException {


        validarPaciente(paciente);
        validarOdontologo(odontologo);
        validarFecha(fecha);
        validarHora(hora);



        boolean existeTurnoSolapado = repositorio.listarTodos().stream()
                .anyMatch(t -> t.getOdontologo().getId() == odontologo.getId()
                        && t.getFecha().equals(fecha) && t.getHora().equals(hora));

        if (existeTurnoSolapado) {
            throw new TurnoYaReservadoException(odontologo.getNombreCompleto(),fecha,hora);
        }

        long id = repositorio.siguienteId();

        Turno turno = new Turno(id, paciente, odontologo, fecha, hora, motivoConsulta);
        repositorio.guardar(turno);

        return turno;
    }

    // busca por id
    public Turno buscarPorId(long id) throws DatoInvalidoException {
        Turno turno = repositorio.buscarPorId(id);

        if (turno == null) {
            throw new DatoInvalidoException("Turno", "No existe un turno con el id " + id);
        }
        return turno;
    }

    // todos los turnos
    public List<Turno> listarTodos() {

        return repositorio.listarTodos();
    }

    // turnos de un paciente
    public List<Turno> listarPorPaciente(long pacienteId) {

        return repositorio.listarTodos().stream()
                .filter(t -> t.getPaciente().getId() == pacienteId).collect(Collectors.toList());
    }

    // turnos de un odontólogo
    public List<Turno> listarPorOdontologo(long odontologoId) {
        return repositorio.listarTodos().stream()
                .filter(t -> t.getOdontologo().getId() == odontologoId)
                .collect(Collectors.toList());
    }

    //filtra los turnos dentro de un rango de fechas
    public List<Turno> listarPorRangoFechas(LocalDate inicio, LocalDate fin) throws DatoInvalidoException {
        if (inicio == null || fin == null) {
            throw new DatoInvalidoException("Fechas", "Las fechas no pueden ser nulas.");
        }

        if (inicio.isAfter(fin)) {
            throw new DatoInvalidoException("Fechas", "La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        return repositorio.listarTodos().stream()
                .filter(t -> !t.getFecha().isBefore(inicio) && !t.getFecha().isAfter(fin))
                .collect(Collectors.toList());
    }



    // pasa a cancelado solo si se puede
    public Turno cancelarTurno(long id) throws DatoInvalidoException {
        Turno turno = buscarPorId(id);

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new DatoInvalidoException("Estado", "Este turno ya fue cancelado.");
        }

        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new DatoInvalidoException("Estado", "No se puede cancelar un turno completado.");
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        repositorio.actualizar(turno);

        return turno;
    }

    // solo deja si estaba pendiente
    public Turno confirmarTurno(long id) throws DatoInvalidoException {
        Turno turno = buscarPorId(id);

        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new DatoInvalidoException("Estado", "Solo se pueden confirmar turnos pendientes.");
        }

        turno.setEstado(EstadoTurno.CONFIRMADO);
        repositorio.actualizar(turno);

        return turno;
    }

    // marca como completado
    public Turno completarTurno(long id) throws DatoInvalidoException{
        Turno turno = buscarPorId(id);


        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new DatoInvalidoException("Estado", "No se puede completar un turno que fue cancelado.");
        }

        turno.setEstado(EstadoTurno.COMPLETADO);
        repositorio.actualizar(turno);

        return turno;
    }

    // borra de verdad solo si ya estaba cancelado
    public void eliminarTurno(long id) throws DatoInvalidoException{
        Turno turno = buscarPorId(id);

        if (turno.getEstado() != EstadoTurno.CANCELADO) {
            throw new DatoInvalidoException("Estado", "Solo se pueden eliminar turnos cancelados" +
                    ", cancela el turno para poder eliminarlo.");
        }

        repositorio.eliminar(id);
    }

    // valida que el paciente no sea nulo
    private void validarPaciente(Paciente paciente) throws DatoInvalidoException {
        if (paciente == null) {
            throw new DatoInvalidoException("Paciente", "El paciente no puede estar vacia.");
        }
    }

    // valida que el odontologo no sea nulo
    private void validarOdontologo(Odontologo odontologo) throws DatoInvalidoException {
        if (odontologo == null) {
            throw new DatoInvalidoException("Odontólogo", "El odontólogo no puede estar vacio");
        }
    }


    // fecha no null y no del pasado
    private void validarFecha(LocalDate fecha) throws DatoInvalidoException {
        if (fecha == null) {
            throw new DatoInvalidoException("Fecha", "La fecha no puede ser nula.");
        }

        if (fecha.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("Fecha", "No se pueden registrar turnos en fechas pasadas.");
        }
    }

    // hora entre 8 y 20
    private void validarHora(LocalTime hora) throws DatoInvalidoException {
        if (hora == null) {
            throw new DatoInvalidoException("Hora", "La hora no puede ser nula.");
        }

        LocalTime apertura = LocalTime.of(8, 0);
        LocalTime cierre = LocalTime.of(20, 0);

        if (hora.isBefore(apertura) || hora.isAfter(cierre)) {
            throw new DatoInvalidoException("Hora", "Los turnos solo se pueden registrar entre las 08:00 y las 20:00.");
        }
    }
}
