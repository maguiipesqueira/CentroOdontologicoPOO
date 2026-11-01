package service;

import entity.DesgloseMonto;
import exception.DatoInvalidoException;
import pago.MedioPago;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calcula importes con aritmética decimal y una única regla de redondeo. */
public final class CalculadorMonto {
    private static final BigDecimal PORCENTAJE_OBRA_SOCIAL = new BigDecimal("0.30");
    private static final BigDecimal CERO = new BigDecimal("0.00");

    public DesgloseMonto calcular(BigDecimal base, boolean obraSocial, MedioPago medio)
            throws DatoInvalidoException {
        if (medio == null) throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago.");
        validar(base);
        BigDecimal descuentoOS = obraSocial ? redondear(base.multiply(PORCENTAJE_OBRA_SOCIAL)) : CERO;
        BigDecimal despuesOS = redondear(base.subtract(descuentoOS));
        BigDecimal descuentoMedio = redondear(medio.calcularDescuento(despuesOS));
        if (descuentoMedio.signum() < 0 || descuentoMedio.compareTo(despuesOS) > 0) {
            throw new DatoInvalidoException("Descuento", "El descuento del medio de pago no es válido.");
        }
        BigDecimal total = redondear(despuesOS.subtract(descuentoMedio));
        return new DesgloseMonto(base, descuentoOS, descuentoMedio, total);
    }

    public DesgloseMonto calcular(BigDecimal base, boolean obraSocial) throws DatoInvalidoException {
        validar(base);
        BigDecimal descuentoOS = obraSocial ? redondear(base.multiply(PORCENTAJE_OBRA_SOCIAL)) : CERO;
        return new DesgloseMonto(base, descuentoOS, CERO, redondear(base.subtract(descuentoOS)));
    }

    public DesgloseMonto calcular(double base, boolean obraSocial, MedioPago medio) throws DatoInvalidoException {
        return calcular(BigDecimal.valueOf(base), obraSocial, medio);
    }
    public DesgloseMonto calcular(double base, boolean obraSocial) throws DatoInvalidoException {
        return calcular(BigDecimal.valueOf(base), obraSocial);
    }

    private void validar(BigDecimal base) throws DatoInvalidoException {
        if (base == null || base.signum() <= 0)
            throw new DatoInvalidoException("Monto", "El monto base debe ser mayor a cero.");
    }

    public static BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
