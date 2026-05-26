package entity;

public class Odontologo {

    private long id;
    private String nombre;
    private String apellido;
    private int matricula;

    // constructor con datos y el de vista odon
    public Odontologo(String nombre, String apellido, int matricula) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }
    public Odontologo(long id, String nombre, String apellido, int matricula) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }

    // constructor vacío
    public Odontologo() {}

    // devuelve id
    public long getId() { return id; }
    // devuelve nombre
    public String getNombre() { return nombre; }
    // devuelve apellido
    public String getApellido() { return apellido; }
    // devuelve matrícula
    public int getMatricula() { return matricula; }

    // cambia id
    public void setId(long id) { this.id = id; }
    // cambia nombre
    public void setNombre(String nombre) { this.nombre = nombre; }
    // cambia apellido
    public void setApellido(String apellido) { this.apellido = apellido; }
    // cambia matrícula
    public void setMatricula(int matricula) { this.matricula = matricula; }

    // nombre y apellido juntos
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    // texto corto para listar
    @Override
    public String toString() {
        return "ID: " + id + " | " + getNombreCompleto() + " | Matrícula: " + matricula;
    }
}