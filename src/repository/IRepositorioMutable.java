package repository;

/** Operaciones de modificacion disponibles solo para repositorios que las necesitan. */
public interface IRepositorioMutable<T> extends IRepositorio<T> {

    void actualizar(T entidad);

    void eliminar(long id);
}
