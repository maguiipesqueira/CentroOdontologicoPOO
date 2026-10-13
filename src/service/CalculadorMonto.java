package service;

import exception.DatoInvalidoException;
import pago.MedioPago;

// Sabe aplicar los descuentos a un monto: no conoce turnos, pacientes ni facturas (alta cohesion, bajo acoplamiento)
public class CalculadorMonto {

    // descuento por tener obra social
    private static final double DESCUENTO_OBRA_SOCIAL = 0.30;

    // calcula el desglose con el medio de pago ya elegido
    public DesgloseMonto calcular(double montoBase, boolean tieneObraSocial, MedioPago medioPago)
            throws DatoInvalidoException {

        if (medioPago == null) {
            throw new DatoInvalidoException("Método de pago", "Debe elegir un método de pago.");
        }

        return calcular(montoBase, tieneObraSocial, medioPago.getPorcentajeDescuento());
    }

    // calcula el desglose cuando todavia no se eligio medio de pago (solo aplica la obra social)
    public DesgloseMonto calcular(double montoBase, boolean tieneObraSocial) throws DatoInvalidoException {
        return calcular(montoBase, tieneObraSocial, 0.0);
    }

    // primero la obra social sobre el precio de lista y despues el descuento del medio de pago
    // sobre lo que queda (los descuentos son acumulables)
    private DesgloseMonto calcular(double montoBase, boolean tieneObraSocial, double porcentajeMedio)
            throws DatoInvalidoException {

        if (montoBase <= 0) {
            throw new DatoInvalidoException("Monto", "El monto base debe ser mayor a cero.");
        }

        double descuentoObraSocial = tieneObraSocial ? redondear(montoBase * DESCUENTO_OBRA_SOCIAL) : 0.0;
        double montoLuegoDeObraSocial = montoBase - descuentoObraSocial;

        double descuentoEfectivo = redondear(montoLuegoDeObraSocial * porcentajeMedio);
        double montoTotal = redondear(montoLuegoDeObraSocial - descuentoEfectivo);

        return new DesgloseMonto(montoBase, descuentoObraSocial, descuentoEfectivo, montoTotal);
    }

    // redondea a 2 decimales para evitar ruido de double (ej 135000.00000000001)
    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}