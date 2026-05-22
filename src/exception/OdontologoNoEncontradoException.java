package exception;

public class OdontologoNoEncontradoException extends ClinicaException {

    //codigo identificador unico
    private static final int CODIGO = 102;

    // Busca por ID
    public OdontologoNoEncontradoException(long id) {
        super("No se encontró ningún odontólogo con el ID: " + id, CODIGO);
    }

    // Busca  por matrícula
    public OdontologoNoEncontradoException(int matricula) {
        super("No se encontró ningún odontólogo con la matrícula: " + matricula, CODIGO);
    }

    // Mensaje
    public OdontologoNoEncontradoException(String mensaje) {
        super(mensaje, CODIGO);
    }
}