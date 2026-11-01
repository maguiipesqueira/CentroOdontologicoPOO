package exception;

/** Se produce cuando una búsqueda o modificación referencia un odontólogo inexistente. */
public class OdontologoNoEncontradoException extends ClinicaException {

    private static final long serialVersionUID = 1L;
    private static final int CODIGO = 102;

    public OdontologoNoEncontradoException(long id) {
        super("No se encontró ningún odontólogo con el ID: " + id, CODIGO);
    }

    public OdontologoNoEncontradoException(int matricula) {
        super("No se encontró ningún odontólogo con la matrícula: " + matricula, CODIGO);
    }

    public OdontologoNoEncontradoException(String mensaje) {
        super(mensaje, CODIGO);
    }
}
