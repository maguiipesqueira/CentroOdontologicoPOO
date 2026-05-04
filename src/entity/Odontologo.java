package entity;

public class Odontologo {

    private long id;
    private String nombre;
    private String apellido;
    private int matricula;

    public Odontologo(long id, String nombre, String apellido, int matricula) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }

    public Odontologo() {}

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public int getMatricula() { return matricula; }

    public void setId(long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setMatricula(int matricula) { this.matricula = matricula; }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | " + getNombreCompleto() + " | Matrícula: " + matricula;
    }


}
