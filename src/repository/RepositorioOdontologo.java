package repository;

import entity.Odontologo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repositorio de odontologos con indice secundario por matricula. */
public class RepositorioOdontologo extends RepositorioEnMemoriaMutable<Odontologo> {

    private final Map<Integer, Long> idsPorMatricula = new HashMap<>();

    @Override
    protected long idDe(Odontologo odontologo) {
        return odontologo.getId();
    }

    @Override
    protected void alGuardar(Odontologo odontologo) {
        idsPorMatricula.put(odontologo.getMatricula(), odontologo.getId());
    }

    @Override
    protected void alEliminar(Odontologo odontologo) {
        Long idIndexado = idsPorMatricula.get(odontologo.getMatricula());
        if (idIndexado != null && idIndexado == odontologo.getId()) {
            idsPorMatricula.remove(odontologo.getMatricula());
        }
    }

    public Optional<Odontologo> buscarPorMatricula(int matricula) {
        Long id = idsPorMatricula.get(matricula);
        return id == null ? Optional.empty() : buscarPorId(id);
    }
}
