package repository;

import entity.Paciente;

import java.util.*;

public class RepositorioPaciente implements IRepositorio<Paciente> {

    private final Map<Long, Paciente> almacenamiento = new HashMap<>();

    // guarda el paciente con su id como clave
    @Override
    public void guardar(Paciente paciente) {
        almacenamiento.put(paciente.getId(), paciente);
    }

    // trae el paciente o null
    @Override
    public Paciente buscarPorId(Long id) {
        return almacenamiento.get(id);
    }

    // copia de todos en una lista
    @Override
    public List<Paciente> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    // pisa solo si el id ya estaba
    @Override
    public void actualizar(Paciente paciente) {
        if (almacenamiento.containsKey(paciente.getId())) {
            almacenamiento.put(paciente.getId(), paciente);
        }
    }

    // borra por id
    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    // busca el dni a mano porque el mapa es por id
    public Paciente buscarPorDni(int dni) {
        for (Paciente p : almacenamiento.values()) {
            if (p.getDni() == dni) {
                return p;
            }
        }
        return null;
    }

    // true si ya hay alguien con ese dni
    public boolean existeDni(int dni) {
        return buscarPorDni(dni) != null;
    }
}
