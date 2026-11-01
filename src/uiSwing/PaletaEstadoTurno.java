package uiSwing;

import entity.EstadoTurno;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Paleta única para representar visualmente cada estado de un turno. */
public final class PaletaEstadoTurno {

    public record Colores(Color fondo, Color texto, Color borde) { }

    private static final Map<EstadoTurno, Colores> PALETA;

    static {
        EnumMap<EstadoTurno, Colores> colores = new EnumMap<>(EstadoTurno.class);
        colores.put(EstadoTurno.PENDIENTE,
                new Colores(new Color(254, 243, 199), new Color(146, 64, 14), new Color(245, 158, 11)));
        colores.put(EstadoTurno.CONFIRMADO,
                new Colores(new Color(224, 242, 254), new Color(12, 74, 110), new Color(14, 165, 233)));
        colores.put(EstadoTurno.COMPLETADO,
                new Colores(new Color(209, 250, 229), new Color(6, 78, 59), new Color(5, 150, 105)));
        colores.put(EstadoTurno.CANCELADO,
                new Colores(new Color(255, 228, 230), new Color(136, 19, 55), new Color(190, 18, 60)));
        colores.put(EstadoTurno.AUSENTE,
                new Colores(new Color(237, 233, 254), new Color(91, 33, 182), new Color(124, 58, 237)));
        PALETA = Collections.unmodifiableMap(colores);
    }

    private PaletaEstadoTurno() { }

    public static Colores de(EstadoTurno estado) {
        if (estado == null) {
            return new Colores(Color.WHITE, new Color(15, 23, 42), new Color(148, 163, 184));
        }
        return PALETA.get(estado);
    }

    public static Color fondo(EstadoTurno estado) { return de(estado).fondo(); }
    public static Color texto(EstadoTurno estado) { return de(estado).texto(); }
    public static Color borde(EstadoTurno estado) { return de(estado).borde(); }
}
