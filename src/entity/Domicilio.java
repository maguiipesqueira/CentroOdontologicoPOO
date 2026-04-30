package entity;

public class Domicilio {
    private String calle;
    private int numero;
    private String localidad;
    private String provincia;
    private TipoHogar hogar;

    public Domicilio(String calle, int numero, String localidad, String provincia, TipoHogar hogar) {
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
        this.hogar = hogar;
    }

    public Domicilio() {}

    public String getCalle() { return calle; }
    public int getNumero() { return numero; }
    public String getLocalidad() { return localidad; }
    public String getProvincia() { return provincia; }
    public TipoHogar getHogar() { return hogar; }

    public void setCalle(String calle) { this.calle = calle; }
    public void setNumero(int numero) { this.numero = numero; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public void setHogar(TipoHogar hogar) { this.hogar = hogar; }

    public String obtenerDireccionCompleta() {
        return calle + " " + numero + ", " + localidad + ", " + provincia;
    }

    public boolean esDeProvincia(String provinciaBusqueda) {
        return this.provincia.equalsIgnoreCase(provinciaBusqueda);
    }

    @Override
    public String toString() {
        return calle + " " + numero + " (" + localidad + ", " + provincia + ")";
    }
}
