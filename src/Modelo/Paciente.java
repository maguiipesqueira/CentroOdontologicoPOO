package Modelo;
import java.time.LocalDate;
import java.util.Objects;

public class Paciente {

//Atributos: son las caracteristicas del objeto // Encapsulamiento - los atributos se ponen privados
    private int id;
    private String nombre;
    private String apellido;
    private int dni;
    private String email;
    private LocalDate fechaAlta;
    private Domicilio domicilio;

//Metodos - el primero siempre tiene que ser el constructor (permite la creacion de los objetos)
//Unico metodo que se llama como la clase misma
    //Constructores con parametros

    public Paciente (int id, String nombre, String apellido, int dni, String email, LocalDate fechaAlta, Domicilio domicilio){

        this.id =id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.domicilio = domicilio;

    } // parentesis del constructor

    public Paciente (int id, String nombre, String apellido, int dni, String email){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
    }

    //Constructor por defecto
    public Paciente(){}
    //Encapsulamiento -> Salida - nombre - parametros - exception (opcional)
    //GETTER
    public String getNombre (){
        return this.nombre;
    }

    public String getApellido(){
        return this.apellido;
    }

    public int getId(){
        return this.id;
    }

    public int getDni(){
        return this.dni;
    }

    public String getEmail(){
        return this.email;
    }

    public LocalDate getFechaAlta(){
        return this.fechaAlta;
    }

    public Domicilio getDomicilio(){
        return this.domicilio;
    }

    //SETTER
    public void setId(int id){
        this.id = id;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    public boolean tieneDomicilio() {
        return this.domicilio != null;
    }

//metodo
    public void mostrarDatosPaciente(){
        System.out.println("Datos del paciente cargado: "+getId());
        System.out.println("Nombre: "+getNombre());
        System.out.println("Apellido: "+getApellido());
        System.out.println("Cedula: "+getDni());
        System.out.println("Email: "+getEmail());
        System.out.println("Fecha de alta: "+getFechaAlta());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return id == paciente.id && dni == paciente.dni && Objects.equals(nombre, paciente.nombre) && Objects.equals(apellido, paciente.apellido) && Objects.equals(email, paciente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellido, dni, email);
    }

    @Override
    public String toString() {
        return "Paciente ID: " + id + " | " + getNombreCompleto() + " | DNI: " + dni + " | Domicilio: " + domicilio.toString();
    }

}
