package exception;

public class ClinicaException extends Exception {

    // padre de todos los errores del proyecto, les agrega un código numérico

    private final int codigoError;

    public ClinicaException(String mensaje, int codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public ClinicaException(String mensaje) {
        super(mensaje);
        this.codigoError = 0;
    }

    public int getCodigoError() {
        return codigoError;
    }

    @Override
    public String toString() {
        return "[Error " + codigoError + "] " + getMessage();
    }
}
