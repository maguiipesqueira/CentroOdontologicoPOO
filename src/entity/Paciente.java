package entity;

import java.time.LocalDate;
import java.util.Objects;

public class Paciente {

    private long id;
    private String nombre;
    private String apellido;
    private int dni;
    private String email;
    private LocalDate fechaAlta;
    private Domicilio domicilio;

    public Paciente(long id, String nombre, String apellido, int dni, String email, LocalDate fechaAlta, Domicilio domicilio) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.domicilio = domicilio;
    }

    public Paciente(long id, String nombre, String apellido, int dni, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
    }

    public Paciente() {}

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public int getDni() { return dni; }
    public String getEmail() { return email; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public Domicilio getDomicilio() { return domicilio; }

    public void setId(long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setDni(int dni) { this.dni = dni; }
    public void setEmail(String email) { this.email = email; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }
    public void setDomicilio(Domicilio domicilio) { this.domicilio = domicilio; }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    public boolean tieneDomicilio() {
        return this.domicilio != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return id == paciente.id && dni == paciente.dni &&
                Objects.equals(nombre, paciente.nombre) &&
                Objects.equals(apellido, paciente.apellido) &&
                Objects.equals(email, paciente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellido, dni, email);
    }

    @Override
    public String toString() {
        String dom = (domicilio != null) ? domicilio.toString() : "Sin domicilio";
        return "Paciente ID: " + id + " | " + getNombreCompleto() +
               " | DNI: " + dni + " | Email: " + email + " | Domicilio: " + dom;
    }
}
