package repository;

/** Template Method para repositorios que permiten actualizar y eliminar. */
public abstract class RepositorioEnMemoriaMutable<T> extends RepositorioEnMemoria<T>
        implements IRepositorioMutable<T> {

    @Override
    public final void actualizar(T entidad) {
        actualizarSiExiste(entidad);
    }

    @Override
    public final void eliminar(long id) {
        eliminarPorId(id);
    }
}
