package pago;

import java.math.BigDecimal;

/** Política monetaria de descuento, aplicada sobre un importe expresado en pesos. */
@FunctionalInterface
public interface PoliticaDescuento {
    BigDecimal calcularDescuento(BigDecimal monto);
}
