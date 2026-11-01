package entity;

import java.time.LocalDate;

public class Paciente extends Persona {

    private final int dni;
    private String email;
    private final LocalDate fechaAlta;
    private Domicilio domicilio; // puede ser null: cargar el domicilio es opcional
    private boolean tieneObraSocial; // la facturacion decide que descuento le corresponde

    // unico constructor: todos los datos del paciente (el domicilio puede ser null)
    public Paciente(long id, String nombre, String apellido, int dni, String email,
                    LocalDate fechaAlta, Domicilio domicilio, boolean tieneObraSocial) {
        super(id, nombre, apellido);
        this.dni = dni;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.domicilio = domicilio;
        this.tieneObraSocial = tieneObraSocial;
    }

    public int getDni() { return dni; }

    public String getEmail() { return email; }

    public LocalDate getFechaAlta() { return fechaAlta; }

    public Domicilio getDomicilio() { return domicilio; }

    public boolean tieneObraSocial() { return tieneObraSocial; }

    public void setEmail(String email) { this.email = email; }

    public void setDomicilio(Domicilio domicilio) { this.domicilio = domicilio; }

    public void setTieneObraSocial(boolean tieneObraSocial) { this.tieneObraSocial = tieneObraSocial; }

    // true si tiene domicilio cargado
    public boolean tieneDomicilio() {
        return this.domicilio != null;
    }

    // dos pacientes son el mismo si tienen el mismo DNI (es lo que los identifica en la vida real)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente)) return false;
        return dni == ((Paciente) o).dni;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(dni);
    }

    // texto para imprimir por pantalla
    @Override
    public String toString() {
        String dom = (domicilio != null) ? domicilio.toString() : "Sin domicilio";
        return "Paciente ID: " + getId() + " | " + getNombreCompleto() +
                " | DNI: " + dni + " | Email: " + email + " | Domicilio: " + dom +
                " | Obra social: " + (tieneObraSocial ? "Sí" : "No");
    }
}