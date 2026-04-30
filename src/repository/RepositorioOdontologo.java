package repository;

import entity.Odontologo;

import java.util.*;

/**
 * Repositorio en memoria para Odontologo.
 * Usa HashMap<Long, Odontologo> como almacenamiento.
 */
public class RepositorioOdontologo implements IRepositorio<Odontologo> {

    private final Map<Long, Odontologo> almacenamiento = new HashMap<>();

    @Override
    public void guardar(Odontologo odontologo) {
        almacenamiento.put(odontologo.getId(), odontologo);
    }

    @Override
    public Optional<Odontologo> buscarPorId(Long id) {
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public List<Odontologo> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public void actualizar(Odontologo odontologo) {
        if (almacenamiento.containsKey(odontologo.getId())) {
            almacenamiento.put(odontologo.getId(), odontologo);
        }
    }

    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    /**
     * Busca un odontólogo por matrícula.
     * Patrón GRASP Expert: el repositorio es quien conoce la colección.
     */
    public Optional<Odontologo> buscarPorMatricula(int matricula) {
        return almacenamiento.values().stream()
                .filter(o -> o.getMatricula() == matricula)
                .findFirst();
    }

    /**
     * Verifica si ya existe un odontólogo con esa matrícula.
     */
    public boolean existeMatricula(int matricula) {
        return almacenamiento.values().stream()
                .anyMatch(o -> o.getMatricula() == matricula);
    }
}
