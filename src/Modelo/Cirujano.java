package Modelo;

public class Cirujano extends Odontologo{
    private String tipoCirugia;

    //HERENCIA DE LA CLASE ODONTOLOGO - Constructor con parametros
    public Cirujano(int id, String nombre, String apellido, int matricula, String tipoCirugia) {
        super(id, nombre, apellido, matricula);
        this.tipoCirugia = tipoCirugia; // esta por fuera de la herencia
    }
    //Constructor por defecto
    public Cirujano(){
        super();
    }

    //getter y setter
    public String getTipoCirugia() {
        return tipoCirugia;
    }
    public void setTipoCirugia(String tipoCirugia) {
        this.tipoCirugia = tipoCirugia;
    }


    //Metodo toString() heredado
    @Override
    public String toString() {
        return "Especialidad: Cirujano | " + super.toString();
    }
}