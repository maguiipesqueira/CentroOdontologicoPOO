package ui;

import java.util.Objects;

/** Una opción de menú representa un comando ejecutable. */
public record OpcionMenu(int numero, String etiqueta, Runnable comando) {
    public OpcionMenu {
        if (numero < 1) throw new IllegalArgumentException("El número de opción debe ser positivo.");
        if (etiqueta == null || etiqueta.isBlank()) throw new IllegalArgumentException("La etiqueta es obligatoria.");
        Objects.requireNonNull(comando, "El comando es obligatorio.");
    }

    public void ejecutar() { comando.run(); }
}
