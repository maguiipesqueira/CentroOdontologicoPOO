package Modelo;

public class Ortodoncista extends Odontologo {
    private String tipoAparato;

    //HERENCIA DE LA CLASE ODONTOLOGO - constructor con parametros
    public Ortodoncista(int id, String nombre, String apellido, int matricula, String tipoAparato) {
        super(id, nombre, apellido, matricula);
        this.tipoAparato = tipoAparato; // atributo que lo distingue de la clase padre
    }

    //Constructor por defecto
    public Ortodoncista() {
        super();
    }

    //getter y setter
    public String getTipoAparato() {
        return tipoAparato;
    }

    public void setTipoAparato(String tipoAparato) {
        this.tipoAparato = tipoAparato;
    }

    //Metodo toString() heredado
    @Override
    public String toString() {
        return "Especialidad: Ortodoncista | " + super.toString();
    }
}