package service;

import entity.DesgloseMonto;
import exception.DatoInvalidoException;
import pago.MedioPago;
import pago.PoliticaDescuento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Calcula importes con aritmética decimal y una única regla de redondeo.
 * El descuento de obra social es una política inyectable (30 % por defecto), igual que el del medio
 * de pago: cambiar o agregar un descuento no obliga a editar esta clase.
 */
public final class CalculadorMonto {

    private static final BigDecimal PORCENTAJE_OBRA_SOCIAL_PREDETERMINADO = new BigDecimal("0.30");
    private static final BigDecimal CERO = new BigDecimal("0.00");
    private static final PoliticaDescuento SIN_DESCUENTO_DE_MEDIO = monto -> CERO;

    private final PoliticaDescuento politicaObraSocial;

    /** Configuración de producción: 30 % de descuento para pacientes con obra social. */
    public CalculadorMonto() {
        this(PoliticaDescuento.porcentaje(PORCENTAJE_OBRA_SOCIAL_PREDETERMINADO));
    }

    public CalculadorMonto(PoliticaDescuento politicaObraSocial) {
        this.politicaObraSocial = Objects.requireNonNull(politicaObraSocial, "La política de obra social es obligatoria.");
    }

    public DesgloseMonto calcular(BigDecimal base, boolean obraSocial, MedioPago medio)
            throws DatoInvalidoException {
        if (medio == null) throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago.");
        return calcularCon(base, obraSocial, medio);
    }

    /** Vista previa sin medio de pago elegido todavía: solo se aplica la obra social. */
    public DesgloseMonto calcular(BigDecimal base, boolean obraSocial) throws DatoInvalidoException {
        return calcularCon(base, obraSocial, SIN_DESCUENTO_DE_MEDIO);
    }

    public DesgloseMonto calcular(double base, boolean obraSocial, MedioPago medio) throws DatoInvalidoException {
        return calcular(BigDecimal.valueOf(base), obraSocial, medio);
    }

    public DesgloseMonto calcular(double base, boolean obraSocial) throws DatoInvalidoException {
        return calcular(BigDecimal.valueOf(base), obraSocial);
    }

    // único lugar donde se hace la cuenta
    private DesgloseMonto calcularCon(BigDecimal base, boolean obraSocial, PoliticaDescuento politicaMedio)
            throws DatoInvalidoException {
        validar(base);
        BigDecimal descuentoOS = obraSocial ? redondear(politicaObraSocial.calcularDescuento(base)) : CERO;
        BigDecimal despuesOS = redondear(base.subtract(descuentoOS));
        BigDecimal descuentoMedio = redondear(politicaMedio.calcularDescuento(despuesOS));
        if (descuentoMedio.signum() < 0 || descuentoMedio.compareTo(despuesOS) > 0) {
            throw new DatoInvalidoException("Descuento", "El descuento del medio de pago no es válido.");
        }
        BigDecimal total = redondear(despuesOS.subtract(descuentoMedio));
        return new DesgloseMonto(base, descuentoOS, descuentoMedio, total);
    }

    private void validar(BigDecimal base) throws DatoInvalidoException {
        if (base == null || base.signum() <= 0)
            throw new DatoInvalidoException("Monto", "El monto base debe ser mayor a cero.");
    }

    public static BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}