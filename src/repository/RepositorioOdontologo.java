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
    public Odontologo buscarPorId(Long id) {
        return almacenamiento.get(id);
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
     * Devuelve el odontólogo si existe, o null si no existe.
     */
    public Odontologo buscarPorMatricula(int matricula) {
        for (Odontologo o : almacenamiento.values()) {
            if (o.getMatricula() == matricula) {
                return o;
            }
        }
        return null;
    }

    /**
     * Verifica si ya existe un odontólogo con esa matrícula.
     */
    public boolean existeMatricula(int matricula) {
        return buscarPorMatricula(matricula) != null;
    }
}
