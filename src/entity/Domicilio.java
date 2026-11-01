package entity;

import java.util.Objects;

/**
 * Domicilio de un paciente. Es un objeto de valor inmutable: para cambiarlo se crea uno nuevo
 * (por eso no tiene setters ni constructor vacio, no puede quedar a medio armar).
 */
public class Domicilio {
    private final String calle;
    private final int numero;
    private final String localidad;
    private final String provincia;
    private final TipoHogar hogar;

    public Domicilio(String calle, int numero, String localidad, String provincia, TipoHogar hogar) {
        this.calle = Objects.requireNonNull(calle, "La calle no puede ser nula");
        this.numero = numero;
        this.localidad = Objects.requireNonNull(localidad, "La localidad no puede ser nula");
        this.provincia = Objects.requireNonNull(provincia, "La provincia no puede ser nula");
        this.hogar = Objects.requireNonNull(hogar, "El tipo de hogar no puede ser nulo");
    }

    public String getCalle() { return calle; }

    public int getNumero() { return numero; }

    public String getLocalidad() { return localidad; }

    public String getProvincia() { return provincia; }

    public TipoHogar getHogar() { return hogar; }

    // "calle numero, localidad, provincia" (no incluye el tipo de hogar)
    public String obtenerDireccionCompleta() {
        return calleYNumero() + ", " + localidad + ", " + provincia;
    }

    // true si la provincia coincide (mayuscula da igual)
    public boolean esDeProvincia(String provinciaBusqueda) {
        return provincia.equalsIgnoreCase(provinciaBusqueda);
    }

    private String calleYNumero() {
        return calle + " " + numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domicilio otro = (Domicilio) o;
        return numero == otro.numero
                && calle.equals(otro.calle)
                && localidad.equals(otro.localidad)
                && provincia.equals(otro.provincia)
                && hogar == otro.hogar;
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, numero, localidad, provincia, hogar);
    }

    // texto corto: "calle numero (localidad, provincia)"
    @Override
    public String toString() {
        return calleYNumero() + " (" + localidad + ", " + provincia + ")";
    }
}