package entity;

public class Domicilio {
    private String calle;
    private int numero;
    private String localidad;
    private String provincia;
    private TipoHogar hogar;

    // constructor con todo
    public Domicilio(String calle, int numero, String localidad, String provincia, TipoHogar hogar) {
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
        this.hogar = hogar;
    }

    // constructor vacío
    public Domicilio() {}

    // devuelve calle
    public String getCalle() { return calle; }
    // devuelve número
    public int getNumero() { return numero; }
    // devuelve localidad
    public String getLocalidad() { return localidad; }
    // devuelve provincia
    public String getProvincia() { return provincia; }
    // devuelve tipo de hogar
    public TipoHogar getHogar() { return hogar; }

    // cambia calle
    public void setCalle(String calle) { this.calle = calle; }
    // cambia número
    public void setNumero(int numero) { this.numero = numero; }
    // cambia localidad
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    // cambia provincia
    public void setProvincia(String provincia) { this.provincia = provincia; }
    // cambia tipo de hogar
    public void setHogar(TipoHogar hogar) { this.hogar = hogar; }

    // arma la dire en un solo string
    public String obtenerDireccionCompleta() {
        return calle + " " + numero + ", " + localidad + ", " + provincia;
    }

    // true si la provincia coincide (mayúscula da igual)
    public boolean esDeProvincia(String provinciaBusqueda) {
        return this.provincia.equalsIgnoreCase(provinciaBusqueda);
    }

    // texto corto
    @Override
    public String toString() {
        return calle + " " + numero + " (" + localidad + ", " + provincia + ")";
    }
}
