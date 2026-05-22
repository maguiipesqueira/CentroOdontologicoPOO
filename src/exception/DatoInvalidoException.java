package exception;

public class DatoInvalidoException extends ClinicaException {

    private static final int CODIGO = 104;
    private final String campo;
    private final String motivo;

    public DatoInvalidoException(String campo, String motivo) {
        super("Dato inválido en '" + campo + "': " + motivo, CODIGO);
        this.campo = campo;
        this.motivo = motivo;
    }

    public DatoInvalidoException(String mensaje) {
        super(mensaje, CODIGO);
        this.campo = "desconocido";
        this.motivo = "desconocido";
    }

    public String getCampo() {
        return campo;
    }

    public String getMotivo() {
        return motivo;
    }
}