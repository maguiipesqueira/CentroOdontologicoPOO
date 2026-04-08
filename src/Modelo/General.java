package Modelo;

public class General extends Odontologo {

    //HERENCIA DE LA CLASE ODONTOLOGO / Constructor con parametros
    public General(int id, String nombre, String apellido, int matricula) {
        super(id, nombre, apellido, matricula);
    }

    //Constructor por defecto
    public General(){
        super();
    }

    //Metodo toString() heredado
    @Override
    public String toString() {
        return "General{}" + super.toString();
    }
}
