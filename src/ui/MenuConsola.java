package ui;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Renderiza y despacha menús a partir de una lista de comandos (patrón Command). */
public final class MenuConsola {
    private final LectorConsola lector;

    public MenuConsola(LectorConsola lector) {
        this.lector = Objects.requireNonNull(lector, "El lector es obligatorio.");
    }

    /** Ejecuta el menú hasta que se elige la opción 0 (volver). */
    public void ejecutar(String titulo, List<OpcionMenu> opciones) {
        Objects.requireNonNull(opciones, "Las opciones son obligatorias.");
        validarOpciones(opciones);
        while (true) {
            System.out.println("\n" + titulo);
            opciones.forEach(opcion -> System.out.println(opcion.numero() + ". " + opcion.etiqueta()));
            System.out.println("0. Volver");
            int seleccion = lector.leerEntero("Opción: ");
            if (seleccion == 0) return;
            opciones.stream().filter(o -> o.numero() == seleccion).findFirst()
                    .ifPresentOrElse(OpcionMenu::ejecutar,
                            () -> System.out.println("Opción inválida. Intente de nuevo."));
        }
    }

    private void validarOpciones(List<OpcionMenu> opciones) {
        Set<Integer> numeros = new HashSet<>();
        for (OpcionMenu opcion : opciones) {
            if (opcion == null) throw new IllegalArgumentException("El menú no admite opciones nulas.");
            if (!numeros.add(opcion.numero())) {
                throw new IllegalArgumentException("Número de opción duplicado: " + opcion.numero());
            }
        }
    }
}
