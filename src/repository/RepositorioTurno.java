package repository;

import entity.EstadoTurno;
import entity.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para Turno.
 * Usa HashMap<Long, Turno> como almacenamiento.
 */
public class RepositorioTurno implements IRepositorio<Turno> {

    private final Map<Long, Turno> almacenamiento = new HashMap<>();

    @Override
    public void guardar(Turno turno) {
        almacenamiento.put(turno.getId(), turno);
    }

    @Override
    public Optional<Turno> buscarPorId(Long id) {
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public List<Turno> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public void actualizar(Turno turno) {
        if (almacenamiento.containsKey(turno.getId())) {
            almacenamiento.put(turno.getId(), turno);
        }
    }

    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    /**
     * Verifica si ya existe un turno para el mismo odontólogo, fecha y hora.
     * Patrón GRASP Expert: el repositorio conoce la colección completa de turnos.
     */
    public boolean existeTurnoSolapado(long odontologoId, LocalDate fecha, LocalTime hora) {
        return almacenamiento.values().stream()
                .filter(t -> t.getEstado() != EstadoTurno.CANCELADO)
                .anyMatch(t -> t.getOdontologo().getId() == odontologoId
                        && t.getFecha().equals(fecha)
                        && t.getHora().equals(hora));
    }

    /**
     * Lista los turnos de un paciente específico.
     */
    public List<Turno> buscarPorPaciente(long pacienteId) {
        return almacenamiento.values().stream()
                .filter(t -> t.getPaciente().getId() == pacienteId)
                .collect(Collectors.toList());
    }

    /**
     * Lista los turnos de un odontólogo específico.
     */
    public List<Turno> buscarPorOdontologo(long odontologoId) {
        return almacenamiento.values().stream()
                .filter(t -> t.getOdontologo().getId() == odontologoId)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve el próximo ID disponible.
     */
    public long siguienteId() {
        return almacenamiento.isEmpty() ? 1L :
                almacenamiento.keySet().stream().mapToLong(Long::longValue).max().getAsLong() + 1;
    }
}
