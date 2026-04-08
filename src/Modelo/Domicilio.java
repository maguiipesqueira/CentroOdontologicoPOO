package Modelo;

public class Domicilio {
    //calle, número, localidad, provincia.
    private String calle;
    private int numero;
    private String localidad;
    private String provincia;

    //constructor con parametros
    public Domicilio(String calle, int numero, String localidad, String provincia) {
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
    }

    //constructor por defecto
    public Domicilio(){}

    //getters
    public String getCalle() {
        return calle;
    }
    public int getNumero() {
        return numero;
    }
    public String getLocalidad() {
        return localidad;
    }
    public String getProvincia() {
        return provincia;
    }

    //setters
    public void setCalle(String calle) {
        this.calle = calle;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    //Metodo toString()
    @Override
    public String toString() {
        return "Domicilio{" +
                "calle='" + calle + '\'' +
                ", numero=" + numero +
                ", localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                '}';
    }
}
