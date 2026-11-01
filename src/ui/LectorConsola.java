package ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/** Centraliza la lectura y validación de entradas de la interfaz de consola. */
public final class LectorConsola {
    private final Scanner scanner;

    public LectorConsola(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "El scanner es obligatorio.");
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return siguienteLinea().trim();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    public long leerLong(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Long.parseLong(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Use el formato YYYY-MM-DD.");
            }
        }
    }

    public LocalTime leerHora(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalTime.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Hora inválida. Use el formato HH:MM.");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje).toUpperCase(Locale.ROOT);
            if (respuesta.equals("S")) return true;
            if (respuesta.equals("N")) return false;
            System.out.println("Responda S o N.");
        }
    }

    private String siguienteLinea() {
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("No quedan datos para leer desde la consola.");
        }
        return scanner.nextLine();
    }
}
