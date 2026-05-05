package repository;

import entity.Odontologo;

import java.util.*;

public class RepositorioOdontologo implements IRepositorio<Odontologo> {

    private final Map<Long, Odontologo> almacenamiento = new HashMap<>();

    // guarda por id (cualquier subclase)
    @Override
    public void guardar(Odontologo odontologo) {
        almacenamiento.put(odontologo.getId(), odontologo);
    }

    // trae o null
    @Override
    public Odontologo buscarPorId(Long id) {
        return almacenamiento.get(id);
    }

    // todos en lista
    @Override
    public List<Odontologo> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    // actualiza si existía
    @Override
    public void actualizar(Odontologo odontologo) {
        if (almacenamiento.containsKey(odontologo.getId())) {
            almacenamiento.put(odontologo.getId(), odontologo);
        }
    }

    // borra
    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    // busca matrícula recorriendo porque la clave es el id
    public Odontologo buscarPorMatricula(int matricula) {
        for (Odontologo o : almacenamiento.values()) {
            if (o.getMatricula() == matricula) {
                return o;
            }
        }
        return null;
    }

    // true si esa matrícula ya está
    public boolean existeMatricula(int matricula) {
        return buscarPorMatricula(matricula) != null;
    }
}
