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
    public Optional<Paciente> buscarPorId(Long id) {
        return Optional.ofNullable(almacenamiento.get(id));
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
     * Patrón GRASP Expert: el repositorio conoce la colección y puede buscar en ella.
     */
    public Optional<Paciente> buscarPorDni(int dni) {
        return almacenamiento.values().stream()
                .filter(p -> p.getDni() == dni)
                .findFirst();
    }

    /**
     * Verifica si ya existe un paciente con ese DNI (para validaciones del service).
     */
    public boolean existeDni(int dni) {
        return almacenamiento.values().stream()
                .anyMatch(p -> p.getDni() == dni);
    }
}
