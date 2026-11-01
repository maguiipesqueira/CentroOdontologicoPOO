package uiSwing;

import entity.MotivoConsulta;

/** Opción tipada del selector de motivo, incluida la indicación inicial. */
public record OpcionMotivo(String etiqueta, MotivoConsulta motivo) {
    public static OpcionMotivo inicial() { return new OpcionMotivo("Seleccione un tipo", null); }
    public static OpcionMotivo de(MotivoConsulta motivo) {
        if (motivo == null) throw new IllegalArgumentException("El motivo es obligatorio.");
        return new OpcionMotivo(motivo.getNombreLegible(), motivo);
    }
    public boolean esSeleccionable() { return motivo != null; }
    @Override public String toString() { return etiqueta; }
}
