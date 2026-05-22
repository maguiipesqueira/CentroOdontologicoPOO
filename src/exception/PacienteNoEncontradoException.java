package exception;

public class PacienteNoEncontradoException extends ClinicaException {

    // numero unico que detecta el error, en este caso de pacientes
    private static final int CODIGO = 101;

    // Busca por ID
    public PacienteNoEncontradoException(long id) {
        super("No se encontró ningún paciente con el ID: " + id, CODIGO);
    }

    // Busca por DNI
    public PacienteNoEncontradoException(int dni) {
        super("No se encontró ningún paciente con el DNI: " + dni, CODIGO);
    }

    // Mensaje
    public PacienteNoEncontradoException(String mensaje) {
        super(mensaje, CODIGO);
    }
}