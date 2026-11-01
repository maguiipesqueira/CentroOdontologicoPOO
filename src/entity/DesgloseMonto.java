package entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Valores monetarios de un cobro, almacenados con precisión decimal de dos posiciones. */
public final class DesgloseMonto {
    private final BigDecimal montoBase;
    private final BigDecimal descuentoObraSocial;
    private final BigDecimal descuentoMedioPago;
    private final BigDecimal montoTotal;

    public DesgloseMonto(BigDecimal montoBase, BigDecimal descuentoObraSocial,
                         BigDecimal descuentoMedioPago, BigDecimal montoTotal) {
        this.montoBase = dinero(montoBase);
        this.descuentoObraSocial = dinero(descuentoObraSocial);
        this.descuentoMedioPago = dinero(descuentoMedioPago);
        this.montoTotal = dinero(montoTotal);
        if (this.montoBase.signum() < 0 || this.descuentoObraSocial.signum() < 0
                || this.descuentoMedioPago.signum() < 0 || this.montoTotal.signum() < 0) {
            throw new IllegalArgumentException("Los importes no pueden ser negativos.");
        }
    }

    /** Compatibilidad con constructores existentes; los cálculos nuevos usan BigDecimal. */
    public DesgloseMonto(double base, double descObraSocial, double descMedioPago, double total) {
        this(BigDecimal.valueOf(base), BigDecimal.valueOf(descObraSocial),
                BigDecimal.valueOf(descMedioPago), BigDecimal.valueOf(total));
    }

    private static BigDecimal dinero(BigDecimal valor) {
        return Objects.requireNonNull(valor, "El importe no puede ser nulo.").setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMontoBaseDecimal() { return montoBase; }
    public BigDecimal getDescuentoObraSocialDecimal() { return descuentoObraSocial; }
    public BigDecimal getDescuentoMedioPagoDecimal() { return descuentoMedioPago; }
    public BigDecimal getMontoTotalDecimal() { return montoTotal; }

    // Getters double conservados para las vistas y clientes existentes.
    public double getMontoBase() { return montoBase.doubleValue(); }
    public double getDescuentoObraSocial() { return descuentoObraSocial.doubleValue(); }
    public double getDescuentoMedioPago() { return descuentoMedioPago.doubleValue(); }
    @Deprecated public double getDescuentoEfectivo() { return getDescuentoMedioPago(); }
    public double getMontoTotal() { return montoTotal.doubleValue(); }

    @Override public String toString() {
        return "Base: $" + montoBase + " | Desc. obra social: $" + descuentoObraSocial
                + " | Desc. medio de pago: $" + descuentoMedioPago + " | Total: $" + montoTotal;
    }
}
