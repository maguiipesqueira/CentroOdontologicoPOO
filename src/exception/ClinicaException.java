package exception;

/** Excepción base comprobada para errores esperables de los casos de uso de la clínica. */
public class ClinicaException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int codigoError;

    public ClinicaException(String mensaje, int codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public ClinicaException(String mensaje) {
        this(mensaje, 0);
    }

    public ClinicaException(String mensaje, int codigoError, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = codigoError;
    }

    public int getCodigoError() {
        return codigoError;
    }

    @Override
    public String toString() {
        return "[Error " + codigoError + "] " + getMessage();
    }
}
