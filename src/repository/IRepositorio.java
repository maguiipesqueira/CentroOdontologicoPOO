package repository;

import java.util.List;

public interface IRepositorio<T> {

    // guarda o pisa en el mapa
    void guardar(T entidad);

    // busca por id, si no hay devuelve null
    T buscarPorId(Long id);

    // devuelve todo en una lista
    List<T> listarTodos();

    // actualiza si ya existía el id
    void actualizar(T entidad);

    // saca del mapa por id
    void eliminar(Long id);
}
