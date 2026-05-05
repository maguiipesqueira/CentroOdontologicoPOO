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

    // constructor con domicilio
    public Paciente(long id, String nombre, String apellido, int dni, String email, LocalDate fechaAlta, Domicilio domicilio) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.domicilio = domicilio;
    }

    // constructor sin domicilio
    public Paciente(long id, String nombre, String apellido, int dni, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
    }

    // constructor vacío por si hace falta
    public Paciente() {}

    // devuelve el id
    public long getId() { return id; }
    // devuelve el nombre
    public String getNombre() { return nombre; }
    // devuelve el apellido
    public String getApellido() { return apellido; }
    // devuelve el dni
    public int getDni() { return dni; }
    // devuelve el mail
    public String getEmail() { return email; }
    // devuelve la fecha de alta
    public LocalDate getFechaAlta() { return fechaAlta; }
    // devuelve el domicilio
    public Domicilio getDomicilio() { return domicilio; }

    // cambia el id
    public void setId(long id) { this.id = id; }
    // cambia el nombre
    public void setNombre(String nombre) { this.nombre = nombre; }
    // cambia el apellido
    public void setApellido(String apellido) { this.apellido = apellido; }
    // cambia el dni
    public void setDni(int dni) { this.dni = dni; }
    // cambia el mail
    public void setEmail(String email) { this.email = email; }
    // cambia la fecha de alta
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }
    // cambia el domicilio
    public void setDomicilio(Domicilio domicilio) { this.domicilio = domicilio; }

    // junta nombre y apellido en un string
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    // true si tiene domicilio cargado
    public boolean tieneDomicilio() {
        return this.domicilio != null;
    }

    // mira si es el mismo paciente para comparar
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return id == paciente.id && dni == paciente.dni &&
                Objects.equals(nombre, paciente.nombre) &&
                Objects.equals(apellido, paciente.apellido) &&
                Objects.equals(email, paciente.email);
    }

    // hash para sets y eso
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellido, dni, email);
    }

    // texto para imprimir por pantalla
    @Override
    public String toString() {
        String dom = (domicilio != null) ? domicilio.toString() : "Sin domicilio";
        return "Paciente ID: " + id + " | " + getNombreCompleto() +
               " | DNI: " + dni + " | Email: " + email + " | Domicilio: " + dom;
    }
}
