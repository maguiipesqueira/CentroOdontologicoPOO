package repository;

import entity.Paciente;

import java.util.*;

/**
 * Repositorio en memoria para Paciente.
 * Usa HashMap<Long, Paciente> como almacenamiento.
 * Responsabilidad única (SRP): solo persiste y recupera Pacientes.
 */
public class RepositorioPaciente implements IRepositorio<Paciente> {

    private final Map<Long, Paciente> almacenamiento = new HashMap<>();

    @Override
    public void guardar(Paciente paciente) {
        almacenamiento.put(paciente.getId(), paciente);
    }

    @Override
    public Paciente buscarPorId(Long id) {
        return almacenamiento.get(id);
    }

    @Override
    public List<Paciente> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public void actualizar(Paciente paciente) {
        if (almacenamiento.containsKey(paciente.getId())) {
            almacenamiento.put(paciente.getId(), paciente);
        }
    }

    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    /**
     * Busca un paciente por DNI.
     * Devuelve el paciente si existe, o null si no existe.
     */
    public Paciente buscarPorDni(int dni) {
        for (Paciente p : almacenamiento.values()) {
            if (p.getDni() == dni) {
                return p;
            }
        }
        return null;
    }

    /**
     * Verifica si ya existe un paciente con ese DNI.
     */
    public boolean existeDni(int dni) {
        return buscarPorDni(dni) != null;
    }
}
