package entity;

// Resultado de calcular un cobro: el precio de lista, cuanto se descuento por obra social y por el medio de pago y el total a pagar
public class DesgloseMonto {

    private final double montoBase;
    private final double descuentoObraSocial;
    private final double descuentoMedioPago;
    private final double montoTotal;

    // constructor con todos los importes
    public DesgloseMonto(double montoBase, double descuentoObraSocial, double descuentoMedioPago, double montoTotal) {
        this.montoBase = montoBase;
        this.descuentoObraSocial = descuentoObraSocial;
        this.descuentoMedioPago = descuentoMedioPago;
        this.montoTotal = montoTotal;
    }

    // devuelve el precio de lista
    public double getMontoBase() { return montoBase; }
    // devuelve lo descontado por obra social
    public double getDescuentoObraSocial() { return descuentoObraSocial; }
    // devuelve lo descontado por el medio de pago elegido
    public double getDescuentoMedioPago() { return descuentoMedioPago; }

    /** @deprecated usar getDescuentoMedioPago(). */
    @Deprecated
    public double getDescuentoEfectivo() { return getDescuentoMedioPago(); }
    // devuelve el total a pagar
    public double getMontoTotal() { return montoTotal; }

    // texto con el desglose
    @Override
    public String toString() {
        return "Base: $" + montoBase +
                " | Desc. obra social: $" + descuentoObraSocial +
                " | Desc. medio de pago: $" + descuentoMedioPago +
                " | Total: $" + montoTotal;
    }
}