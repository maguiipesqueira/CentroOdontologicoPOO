package uiSwing;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formateador centralizado de importes en pesos argentinos. */
public final class FormatoPlata {

    private static final ThreadLocal<DecimalFormat> FORMATO = ThreadLocal.withInitial(() ->
            new DecimalFormat("$ #,##0.##", new DecimalFormatSymbols(Locale.forLanguageTag("es-AR"))));

    private FormatoPlata() { }

    public static String formatear(double monto) {
        return formatear(BigDecimal.valueOf(monto));
    }

    public static String formatear(BigDecimal monto) {
        if (monto == null) {
            throw new IllegalArgumentException("El importe no puede ser null.");
        }
        return FORMATO.get().format(monto);
    }

    public static String formatearDescuento(double monto) {
        return formatearDescuento(BigDecimal.valueOf(monto));
    }

    public static String formatearDescuento(BigDecimal monto) {
        if (monto == null) {
            throw new IllegalArgumentException("El descuento no puede ser null.");
        }
        return monto.signum() > 0 ? "-" + formatear(monto) : formatear(monto);
    }
}
