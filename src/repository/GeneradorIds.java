package repository;

import java.util.function.LongSupplier;

/**
 * Centraliza la politica actual de identificadores: el siguiente id es el maximo
 * que existe en el repositorio mas uno. La consulta se ejecuta al momento de
 * generar para contemplar entidades cargadas desde persistencia.
 */
public class GeneradorIds {

    public long siguienteId(LongSupplier maximoActual) {
        if (maximoActual == null) {
            throw new IllegalArgumentException("La consulta del maximo actual es obligatoria.");
        }
        return maximoActual.getAsLong() + 1;
    }
}
