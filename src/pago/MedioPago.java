package pago;

/**
 * Contrato de un medio de pago.
 * Cada implementacion define su identificador estable, nombre visible y politica de descuento.
 */
public interface MedioPago {

    String getCodigo();

    String getNombre();

    /**
     * Calcula el descuento que aplica este medio sobre el importe recibido.
     */
    double calcularDescuento(double monto);

    /**
     * Devuelve el importe luego de aplicar el descuento propio del medio.
     */
    default double aplicarDescuento(double monto) {
        return monto - calcularDescuento(monto);
    }
}
