package persistence;

import java.util.ArrayList;
import java.util.List;

/** Utilidades CSV con comillas dobles y escape de comillas según RFC 4180. */
final class Csv {
    private Csv() { }

    static String escribir(List<String> valores) {
        return valores.stream().map(Csv::escapar).collect(java.util.stream.Collectors.joining(","));
    }

    private static String escapar(String valor) {
        if (valor == null) return "";
        String escapado = valor.replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n");
        if (escapado.indexOf(',') >= 0 || escapado.indexOf('"') >= 0) {
            return "\"" + escapado.replace("\"", "\"\"") + "\"";
        }
        return escapado;
    }

    static List<String> leer(String linea) {
        List<String> valores = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (c == ',' && !entreComillas) {
                valores.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (entreComillas) throw new IllegalArgumentException("campo CSV con comillas sin cerrar");
        valores.add(actual.toString());
        return valores;
    }

    static String desescapar(String valor) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (c == '\\' && i + 1 < valor.length()) {
                char siguiente = valor.charAt(++i);
                resultado.append(siguiente == 'n' ? '\n' : siguiente == 'r' ? '\r' : siguiente);
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }
}
