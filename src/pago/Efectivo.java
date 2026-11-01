package pago;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Pago en efectivo, con un descuento configurable (10 % por defecto). */
public class Efectivo implements MedioPago {

    private static final double DESCUENTO_PREDETERMINADO = 0.10;
    private static final double PORCENTAJE_MINIMO = 0.0;
    private static final double PORCENTAJE_MAXIMO = 1.0;

    private final double porcentajeDescuento;

    public Efectivo() {
        this(DESCUENTO_PREDETERMINADO);
    }

    public Efectivo(double porcentajeDescuento) {
        if (porcentajeDescuento < PORCENTAJE_MINIMO || porcentajeDescuento > PORCENTAJE_MAXIMO
                || Double.isNaN(porcentajeDescuento)) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 1.");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public String getCodigo() {
        return "EFECTIVO";
    }

    @Override
    public String getNombre() {
        return "Efectivo";
    }

    @Override
    public double calcularDescuento(double monto) {
        return redondear(monto * porcentajeDescuento);
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal monto) {
        return monto.multiply(BigDecimal.valueOf(porcentajeDescuento))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}