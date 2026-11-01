package persistence;

/** Error aislado de un registro; no impide procesar las demás filas. */
public record ErrorRegistro(long numeroLinea, String motivo, String contenido) { }
