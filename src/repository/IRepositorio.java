package repository;

import java.util.List;

/**
 * Interfaz genérica IRepositorio<T> con operaciones CRUD.
 * Aplica principio OCP: extensible sin modificar la interfaz.
 * Aplica patrón GRASP Expert: cada repositorio conoce su propia colección.
 *
 * @param <T> Tipo de entidad gestionada
 */
public interface IRepositorio<T> {

    /**
     * Guarda una nueva entidad o actualiza una existente.
     */
    void guardar(T entidad);

    /**
     * Busca una entidad por su ID.
     * devuelve la entidad si existe, o null si no existe.
     */
    T buscarPorId(Long id);

    /**
     * Devuelve la lista completa de entidades.
     */
    List<T> listarTodos();

    /**
     * Actualiza una entidad existente.
     */
    void actualizar(T entidad);

    /**
     * Elimina la entidad con el ID dado.
     */
    void eliminar(Long id);
}
