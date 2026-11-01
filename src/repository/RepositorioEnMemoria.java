package repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion comun del almacenamiento en memoria.
 * Las subclases definen como obtener el id y mantienen sus indices secundarios
 * mediante los hooks alGuardar/alEliminar.
 */
public abstract class RepositorioEnMemoria<T> implements IRepositorio<T> {

    private final Map<Long, T> almacenamiento = new LinkedHashMap<>();

    protected abstract long idDe(T entidad);

    protected void alGuardar(T entidad) {
        // Hook opcional para indices secundarios.
    }

    protected void alEliminar(T entidad) {
        // Hook opcional para indices secundarios.
    }

    @Override
    public final void guardar(T entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula.");
        }

        long id = idDe(entidad);
        T anterior = almacenamiento.get(id);
        if (anterior != null) {
            alEliminar(anterior);
        }
        almacenamiento.put(id, entidad);
        alGuardar(entidad);
    }

    @Override
    public final Optional<T> buscarPorId(long id) {
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public final List<T> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    protected final void actualizarSiExiste(T entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser nula.");
        }

        long id = idDe(entidad);
        T anterior = almacenamiento.get(id);
        if (anterior == null) {
            return;
        }

        alEliminar(anterior);
        almacenamiento.put(id, entidad);
        alGuardar(entidad);
    }

    protected final void eliminarPorId(long id) {
        T eliminada = almacenamiento.remove(id);
        if (eliminada != null) {
            alEliminar(eliminada);
        }
    }
}
