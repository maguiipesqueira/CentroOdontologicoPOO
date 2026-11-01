package repository;

import entity.Paciente;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repositorio de pacientes con indice secundario por DNI. */
public class RepositorioPaciente extends RepositorioEnMemoriaMutable<Paciente> {

    private final Map<Integer, Long> idsPorDni = new HashMap<>();

    @Override
    protected long idDe(Paciente paciente) {
        return paciente.getId();
    }

    @Override
    protected void alGuardar(Paciente paciente) {
        idsPorDni.put(paciente.getDni(), paciente.getId());
    }

    @Override
    protected void alEliminar(Paciente paciente) {
        Long idIndexado = idsPorDni.get(paciente.getDni());
        if (idIndexado != null && idIndexado == paciente.getId()) {
            idsPorDni.remove(paciente.getDni());
        }
    }

    public Optional<Paciente> buscarPorDni(int dni) {
        Long id = idsPorDni.get(dni);
        return id == null ? Optional.empty() : buscarPorId(id);
    }
}
