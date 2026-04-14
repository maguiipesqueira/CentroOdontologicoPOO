package Modelo;

public class General extends Odontologo {
    private String tipoConsulta;

    //HERENCIA DE LA CLASE ODONTOLOGO / Constructor con parametros
    public General(int id, String nombre, String apellido, int matricula, String tipoConsulta) {
        super(id, nombre, apellido, matricula);
        this.tipoConsulta = tipoConsulta; // atributo que lo distingue de la clase padre
    }

    //Constructor por defecto
    public General(){
        super();
    }

    //getter y setter
    public String getTipoConsulta() {
        return tipoConsulta;
    }
    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    //Metodo toString() heredado
    @Override
    public String toString() {
        return "Especialidad: General | " + super.toString();
    }
}