package service;

import entity.DesgloseMonto;
import exception.DatoInvalidoException;
import pago.MedioPago;

// Calcula el desglose del cobro delegando el descuento propio al medio de pago.
public class CalculadorMonto {

    private static final double DESCUENTO_OBRA_SOCIAL = 0.30;
    private static final double SIN_DESCUENTO_MEDIO = 0.0;

    public DesgloseMonto calcular(double montoBase, boolean tieneObraSocial, MedioPago medioPago)
            throws DatoInvalidoException {
        if (medioPago == null) {
            throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago.");
        }
        validarMonto(montoBase);

        double descuentoObraSocial = tieneObraSocial
                ? redondear(montoBase * DESCUENTO_OBRA_SOCIAL)
                : SIN_DESCUENTO_MEDIO;
        double montoLuegoDeObraSocial = montoBase - descuentoObraSocial;
        double montoTotal = redondear(medioPago.aplicarDescuento(montoLuegoDeObraSocial));
        double descuentoMedioPago = redondear(montoLuegoDeObraSocial - montoTotal);

        return new DesgloseMonto(montoBase, descuentoObraSocial, descuentoMedioPago, montoTotal);
    }

    // Vista previa cuando todavía no se eligió un medio de pago.
    public DesgloseMonto calcular(double montoBase, boolean tieneObraSocial) throws DatoInvalidoException {
        validarMonto(montoBase);

        double descuentoObraSocial = tieneObraSocial
                ? redondear(montoBase * DESCUENTO_OBRA_SOCIAL)
                : SIN_DESCUENTO_MEDIO;
        double montoTotal = redondear(montoBase - descuentoObraSocial);

        return new DesgloseMonto(montoBase, descuentoObraSocial, SIN_DESCUENTO_MEDIO, montoTotal);
    }

    private void validarMonto(double montoBase) throws DatoInvalidoException {
        if (montoBase <= SIN_DESCUENTO_MEDIO) {
            throw new DatoInvalidoException("Monto", "El monto base debe ser mayor a cero.");
        }
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
