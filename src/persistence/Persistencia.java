package persistence;

import java.io.IOException;
import java.util.List;

/** Contrato independiente de la interfaz gráfica para almacenar entidades. */
public interface Persistencia<T> {
    void guardar(List<T> entidades) throws IOException;
    ResultadoCarga<T> cargar() throws IOException;
}
