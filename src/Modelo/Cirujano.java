package Modelo;

public class Cirujano extends Odontologo{

    //HERENCIA DE LA CLASE ODONTOLOGO - Constructor con parametros
    public Cirujano(int id, String nombre, String apellido, int matricula) {
        super(id, nombre, apellido, matricula);
    }

    //Constructor por defecto
    public Cirujano(){
        super();
    }

    //Metodo toString() heredado
    @Override
    public String toString() {
        return "Cirujano{}" + super.toString();
    }
}
