package pago;

/** Pago con tarjeta, sin descuento propio. */
public class Tarjeta implements MedioPago {

    private static final double SIN_DESCUENTO = 0.0;

    @Override
    public String getCodigo() {
        return "TARJETA";
    }

    @Override
    public String getNombre() {
        return "Tarjeta";
    }

    @Override
    public double calcularDescuento(double monto) {
        return SIN_DESCUENTO;
    }
}
