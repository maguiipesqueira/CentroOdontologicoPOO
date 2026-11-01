package exception;

/** Error específico para búsquedas de turnos inexistentes. */
public class TurnoNoEncontradoException extends DatoInvalidoException {

    private static final long serialVersionUID = 1L;
    private static final int CODIGO = 105;

    public TurnoNoEncontradoException(long id) {
        super("Turno", "No existe un turno con el ID " + id + ".", CODIGO);
    }
}
