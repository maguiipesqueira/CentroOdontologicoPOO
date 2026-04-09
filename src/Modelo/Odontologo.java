package Modelo;

public class Odontologo {
    private int id;
    private String nombre;
    private String apellido;
    private int matricula;

//constructor con parametros
    public Odontologo(int id, String nombre, String apellido, int matricula) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }

//Constructor por defecto
    public Odontologo(){}

//getter
    public int getId(){
        return id;
    }
    public String getNombre(){
        return nombre;
    }
    public String getApellido(){
        return apellido;
    }
    public int getMatricula(){
        return matricula;
    }

//setter
    public void setID(int id){
        this.id = id;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setApellido(String apellido){
        this.apellido = apellido;
    }
    public void setMatricula(int matricula){
        this.matricula = matricula;
    }

    public void mostrarDatosOdontologo(){
        System.out.println("Datos del odontologo cargado: "+getId());
        System.out.println("Nombre: "+getNombre());
        System.out.println("Apellido: "+getApellido());
        System.out.println("Numero de matricula: " +getMatricula());
    }
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
    @Override
    public String toString() {
        return "ID: " + id + " | " + getNombreCompleto() + " | Matrícula: " + matricula;
    }
}
