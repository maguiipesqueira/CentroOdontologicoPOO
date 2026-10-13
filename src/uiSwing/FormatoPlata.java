package uiSwing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

// da formato de pesos argentinos a un importe (ej "$ 15.000"), para que todas las pantallas muestren la plata igual
public class FormatoPlata {

    private static final DecimalFormat FORMATO =
            new DecimalFormat("$ #,##0.##", new DecimalFormatSymbols(Locale.forLanguageTag("es-AR")));

    // devuelve el importe formateado
    public static String formatear(double monto) {
        return FORMATO.format(monto);
    }

    // devuelve el importe de un descuento con signo menos (si es cero lo deja sin signo)
    public static String formatearDescuento(double monto) {
        return monto > 0 ? "-" + FORMATO.format(monto) : FORMATO.format(monto);
    }
}