package repository;

import entity.EstadoTurno;
import entity.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class RepositorioTurno implements IRepositorio<Turno> {

    private final Map<Long, Turno> almacenamiento = new HashMap<>();

    // guarda el turno
    @Override
    public void guardar(Turno turno) {
        almacenamiento.put(turno.getId(), turno);
    }

    // busca o null
    @Override
    public Turno buscarPorId(Long id) {
        return almacenamiento.get(id);
    }

    // lista con todos
    @Override
    public List<Turno> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    // pisa si había id
    @Override
    public void actualizar(Turno turno) {
        if (almacenamiento.containsKey(turno.getId())) {
            almacenamiento.put(turno.getId(), turno);
        }
    }

    // borra
    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    // true si ese odontólogo ya tiene turno ahí (los cancelados no cuentan)
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

    // filtra por paciente
    public List<Turno> buscarPorPaciente(long pacienteId) {
        List<Turno> resultado = new ArrayList<>();

        for (Turno t : almacenamiento.values()) {
            if (t.getPaciente().getId() == pacienteId) {
                resultado.add(t);
            }
        }

        return resultado;
    }

    // filtra por odontólogo
    public List<Turno> buscarPorOdontologo(long odontologoId) {
        List<Turno> resultado = new ArrayList<>();

        for (Turno t : almacenamiento.values()) {
            if (t.getOdontologo().getId() == odontologoId) {
                resultado.add(t);
            }
        }

        return resultado;
    }

    // el próximo id es el máximo + 1
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
