package uiSwing;

import pago.MedioPago;

import java.util.Objects;
import java.util.Optional;

/** Opción explícita del selector de medios; evita usar null como elemento del combo. */
public final class OpcionMedioPago {

    private static final OpcionMedioPago SIN_SELECCION = new OpcionMedioPago(null, "Elegir medio de pago...");

    private final MedioPago medioPago;
    private final String etiqueta;

    private OpcionMedioPago(MedioPago medioPago, String etiqueta) {
        this.medioPago = medioPago;
        this.etiqueta = Objects.requireNonNull(etiqueta, "La etiqueta es obligatoria.");
    }

    public static OpcionMedioPago sinSeleccion() { return SIN_SELECCION; }

    public static OpcionMedioPago de(MedioPago medioPago) {
        Objects.requireNonNull(medioPago, "El medio de pago es obligatorio.");
        return new OpcionMedioPago(medioPago, medioPago.getNombre());
    }

    public Optional<MedioPago> medioPago() { return Optional.ofNullable(medioPago); }

    public boolean esSinSeleccion() { return medioPago == null; }

    @Override public String toString() { return etiqueta; }
}
