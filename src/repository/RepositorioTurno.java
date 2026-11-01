package repository;

import entity.Turno;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** Consultas específicas de turnos; las reglas de agenda pertenecen al servicio. */
public class RepositorioTurno extends RepositorioEnMemoriaMutable<Turno> {

    @Override
    protected long idDe(Turno turno) {
        return turno.getId();
    }

    public List<Turno> buscarPorPaciente(long pacienteId) {
        return listarTodos().stream()
                .filter(turno -> turno.getPaciente().getId() == pacienteId)
                .collect(Collectors.toList());
    }

    public List<Turno> buscarPorOdontologo(long odontologoId) {
        return listarTodos().stream()
                .filter(turno -> turno.getOdontologo().getId() == odontologoId)
                .collect(Collectors.toList());
    }

    public List<Turno> buscarPorOdontologoYFecha(long odontologoId, LocalDate fecha) {
        return listarTodos().stream()
                .filter(turno -> turno.getOdontologo().getId() == odontologoId
                        && turno.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Turno> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return listarTodos().stream()
                .filter(turno -> !turno.getFecha().isBefore(inicio) && !turno.getFecha().isAfter(fin))
                .collect(Collectors.toList());
    }
}
