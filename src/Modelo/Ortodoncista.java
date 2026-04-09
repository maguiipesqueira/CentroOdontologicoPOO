package Modelo;

public class Ortodoncista extends Odontologo{

    //HERENCIA DE LA CLASE ODONTOLOGO - constructor con parametros
    public Ortodoncista(int id, String nombre, String apellido, int matricula) {
        super(id, nombre, apellido, matricula);
    }

    //Constructor por defecto
    public Ortodoncista(){
        super();
    }

    //Metodo toString() heredado
    @Override
    public String toString() {
        return "Especialidad: Ortodoncista | " + super.toString();
    }
}
