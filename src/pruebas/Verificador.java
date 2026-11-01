package pruebas;

import java.util.ArrayList;
import java.util.List;

/**
 * Verificador minimo para las pruebas (el proyecto no usa JUnit).
 * Cuenta aciertos y fallos, imprime cada fallo y un resumen al final.
 */
public class Verificador {

    /** Accion de prueba que puede lanzar cualquier excepcion. */
    @FunctionalInterface
    public interface Ejecutable {
        void ejecutar() throws Exception;
    }

    private int aciertos = 0;
    private final List<String> fallos = new ArrayList<>();
    private String seccionActual = "";

    // empieza una seccion de pruebas; si algo lanza una excepcion inesperada, se cuenta como fallo y se sigue
    public void seccion(String nombre, Ejecutable cuerpo) {
        seccionActual = nombre;
        System.out.println();
        System.out.println("== " + nombre);
        try {
            cuerpo.ejecutar();
        } catch (Throwable t) {
            registrarFallo("excepcion inesperada: " + t);
            t.printStackTrace(System.out);
        }
    }

    public void verdadero(String descripcion, boolean condicion) {
        if (condicion) {
            aciertos++;
            System.out.println("  OK    " + descripcion);
        } else {
            registrarFallo(descripcion);
        }
    }

    public void igual(String descripcion, Object esperado, Object actual) {
        boolean ok = (esperado == null) ? actual == null : esperado.equals(actual);
        if (ok) {
            aciertos++;
            System.out.println("  OK    " + descripcion);
        } else {
            registrarFallo(descripcion + " -> esperado: " + esperado + " | actual: " + actual);
        }
    }

    // compara importes con una tolerancia de medio centavo (por el ruido de double)
    public void casi(String descripcion, double esperado, double actual) {
        if (Math.abs(esperado - actual) < 0.005) {
            aciertos++;
            System.out.println("  OK    " + descripcion);
        } else {
            registrarFallo(descripcion + " -> esperado: " + esperado + " | actual: " + actual);
        }
    }

    // verifica que la accion lance exactamente esa clase de excepcion (o una subclase)
    public void lanza(String descripcion, Class<? extends Throwable> tipo, Ejecutable accion) {
        try {
            accion.ejecutar();
            registrarFallo(descripcion + " -> no lanzo " + tipo.getSimpleName());
        } catch (Throwable t) {
            if (tipo.isInstance(t)) {
                aciertos++;
                System.out.println("  OK    " + descripcion);
            } else {
                registrarFallo(descripcion + " -> lanzo " + t.getClass().getSimpleName()
                        + " en vez de " + tipo.getSimpleName());
            }
        }
    }

    private void registrarFallo(String detalle) {
        String linea = "[" + seccionActual + "] " + detalle;
        fallos.add(linea);
        System.out.println("  FALLO " + detalle);
    }

    // imprime el resumen y devuelve true si no hubo fallos
    public boolean resumen() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("Aciertos: " + aciertos + " | Fallos: " + fallos.size());
        if (fallos.isEmpty()) {
            System.out.println("RESULTADO: TODO OK");
        } else {
            System.out.println("RESULTADO: HAY FALLOS");
            fallos.forEach(f -> System.out.println("  - " + f));
        }
        System.out.println("==========================================");
        return fallos.isEmpty();
    }
}