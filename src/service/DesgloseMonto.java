package service;

// Resultado de calcular un cobro: el precio de lista, cuanto se descuento por cada motivo y el total a pagar
public class DesgloseMonto {

    private final double montoBase;
    private final double descuentoObraSocial;
    private final double descuentoEfectivo;
    private final double montoTotal;

    // constructor con todos los importes
    public DesgloseMonto(double montoBase, double descuentoObraSocial, double descuentoEfectivo, double montoTotal) {
        this.montoBase = montoBase;
        this.descuentoObraSocial = descuentoObraSocial;
        this.descuentoEfectivo = descuentoEfectivo;
        this.montoTotal = montoTotal;
    }

    // devuelve el precio de lista
    public double getMontoBase() { return montoBase; }
    // devuelve lo descontado por obra social
    public double getDescuentoObraSocial() { return descuentoObraSocial; }
    // devuelve lo descontado por el medio de pago (efectivo)
    public double getDescuentoEfectivo() { return descuentoEfectivo; }
    // devuelve el total a pagar
    public double getMontoTotal() { return montoTotal; }

    // texto con el desglose
    @Override
    public String toString() {
        return "Base: $" + montoBase +
                " | Desc. obra social: $" + descuentoObraSocial +
                " | Desc. efectivo: $" + descuentoEfectivo +
                " | Total: $" + montoTotal;
    }
}