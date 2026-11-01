package repository;

import entity.Turno;

import java.util.List;
import java.util.stream.Collectors;

/** Repositorio de turnos; las reglas de solapamiento pertenecen al servicio. */
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
}
