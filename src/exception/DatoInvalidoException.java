package exception;

/** Error de validación de datos de entrada o de una operación no permitida. */
public class DatoInvalidoException extends ClinicaException {

    private static final long serialVersionUID = 1L;
    private static final int CODIGO = 104;

    private final String campo;
    private final String motivo;

    public DatoInvalidoException(String campo, String motivo) {
        this(campo, motivo, CODIGO);
    }

    protected DatoInvalidoException(String campo, String motivo, int codigo) {
        super("Dato inválido en '" + normalizar(campo, "desconocido") + "': "
                + normalizar(motivo, "El dato no es válido."), codigo);
        this.campo = normalizar(campo, "desconocido");
        this.motivo = normalizar(motivo, "El dato no es válido.");
    }

    public DatoInvalidoException(String mensaje) {
        super(normalizar(mensaje, "Los datos ingresados no son válidos."), CODIGO);
        this.campo = "desconocido";
        this.motivo = normalizar(mensaje, "Los datos ingresados no son válidos.");
    }

    private static String normalizar(String valor, String alternativo) {
        return valor == null || valor.isBlank() ? alternativo : valor.trim();
    }

    public String getCampo() { return campo; }
    public String getMotivo() { return motivo; }
}
