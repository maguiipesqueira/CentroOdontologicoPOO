package pago;

/** Pago mediante Mercado Pago, sin descuento propio. */
public class MercadoPago implements MedioPago {

    private static final double SIN_DESCUENTO = 0.0;

    @Override
    public String getCodigo() {
        return "MERCADO_PAGO";
    }

    @Override
    public String getNombre() {
        return "Mercado Pago";
    }

    @Override
    public double calcularDescuento(double monto) {
        return SIN_DESCUENTO;
    }
}
