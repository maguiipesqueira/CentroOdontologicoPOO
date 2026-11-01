package exception;

/** Se produce cuando una búsqueda o modificación referencia un paciente inexistente. */
public class PacienteNoEncontradoException extends ClinicaException {

    private static final long serialVersionUID = 1L;
    private static final int CODIGO = 101;

    public PacienteNoEncontradoException(long id) {
        super("No se encontró ningún paciente con el ID: " + id, CODIGO);
    }

    public PacienteNoEncontradoException(int dni) {
        super("No se encontró ningún paciente con el DNI: " + dni, CODIGO);
    }

    public PacienteNoEncontradoException(String mensaje) {
        super(mensaje, CODIGO);
    }
}
