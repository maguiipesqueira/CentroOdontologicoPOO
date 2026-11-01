package pago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Política monetaria de descuento, aplicada sobre un importe expresado en pesos. */
@FunctionalInterface
public interface PoliticaDescuento {
    BigDecimal calcularDescuento(BigDecimal monto);

    /** Política que descuenta un porcentaje fijo (0.30 = 30 %), redondeado a dos decimales. */
    static PoliticaDescuento porcentaje(BigDecimal porcentaje) {
        Objects.requireNonNull(porcentaje, "El porcentaje es obligatorio.");
        if (porcentaje.signum() < 0 || porcentaje.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1.");
        }
        return monto -> monto.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }
}