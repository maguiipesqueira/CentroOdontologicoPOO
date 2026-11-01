package persistence;

import java.util.List;
import java.util.Map;

/** Convierte una entidad a/desde columnas CSV identificadas por nombre. */
public interface CsvMapper<T> {
    List<String> columnas();
    Map<String, String> aColumnas(T entidad);
    T desdeColumnas(Map<String, String> columnas);

    /** Traduce los formatos históricos sin encabezado al esquema actual. */
    Map<String, String> desdeFormatoLegado(List<String> valores);
}
