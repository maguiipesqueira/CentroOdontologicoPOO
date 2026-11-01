package repository;

import java.util.List;
import java.util.Optional;

/**
 * Contrato minimo de acceso a entidades almacenadas.
 * No obliga a los repositorios de solo lectura a implementar modificacion o borrado.
 */
public interface IRepositorio<T> {

    void guardar(T entidad);

    Optional<T> buscarPorId(long id);

    List<T> listarTodos();
}
