package uiSwing;

import entity.Turno;

// Texto del motivo de un turno para mostrar en pantalla. Elegir que mostrar cuando el turno no tiene motivo
// es una decision de presentacion, por eso vive aca y no en la entidad Turno.
public final class TextoMotivo {

    private static final String SIN_MOTIVO = "Consulta";

    private TextoMotivo() {}

    // nombre tecnico del motivo (el mismo texto que se mostraba hasta ahora) o "Consulta" si no tiene
    public static String de(Turno turno) {
        return turno.getMotivoConsulta().map(m -> m.name()).orElse(SIN_MOTIVO);
    }
}