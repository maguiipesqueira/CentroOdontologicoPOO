package entity;

/**
 * Datos que comparten todas las personas del sistema (pacientes y odontologos): identificador,
 * nombre y apellido. Es abstracta porque nunca existe una "persona" a secas.
 * El id se fija al crear el objeto y no cambia nunca; el nombre y el apellido si se pueden corregir.
 */
public abstract class Persona {

    private final long id;
    private String nombre;
    private String apellido;

    protected Persona(long id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public long getId() { return id; }

    public String getNombre() { return nombre; }

    public String getApellido() { return apellido; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setApellido(String apellido) { this.apellido = apellido; }

    // "nombre apellido"
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}