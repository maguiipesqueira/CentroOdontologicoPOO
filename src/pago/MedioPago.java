package pago;

import java.math.BigDecimal;

/** Medio de pago con identificador estable y política de descuento propia. */
public interface MedioPago extends PoliticaDescuento {
    String getCodigo();
    String getNombre();

    /** API de compatibilidad con las pantallas y pruebas previas. */
    double calcularDescuento(double monto);

    @Override
    default BigDecimal calcularDescuento(BigDecimal monto) {
        return BigDecimal.valueOf(calcularDescuento(monto.doubleValue())).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    default double aplicarDescuento(double monto) { return monto - calcularDescuento(monto); }
}
