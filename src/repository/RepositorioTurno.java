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
    public Turno buscarPorId(Long id) {
        return almacenamiento.get(id);
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
     */
    public boolean existeTurnoSolapado(long odontologoId, LocalDate fecha, LocalTime hora) {
        for (Turno t : almacenamiento.values()) {
            if (t.getEstado() != EstadoTurno.CANCELADO
                    && t.getOdontologo().getId() == odontologoId
                    && t.getFecha().equals(fecha)
                    && t.getHora().equals(hora)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lista los turnos de un paciente específico.
     */
    public List<Turno> buscarPorPaciente(long pacienteId) {
        List<Turno> resultado = new ArrayList<>();

        for (Turno t : almacenamiento.values()) {
            if (t.getPaciente().getId() == pacienteId) {
                resultado.add(t);
            }
        }

        return resultado;
    }

    /**
     * Lista los turnos de un odontólogo específico.
     */
    public List<Turno> buscarPorOdontologo(long odontologoId) {
        List<Turno> resultado = new ArrayList<>();

        for (Turno t : almacenamiento.values()) {
            if (t.getOdontologo().getId() == odontologoId) {
                resultado.add(t);
            }
        }

        return resultado;
    }

    /**
     * Devuelve el próximo ID disponible.
     */
    public long siguienteId() {
        if (almacenamiento.isEmpty()) {
            return 1L;
        }

        long max = 0L;

        for (Long id : almacenamiento.keySet()) {
            if (id > max) {
                max = id;
            }
        }

        return max + 1;
    }
}
