package entity;

import java.util.List;

/**
 * Odontologo del consultorio. Es abstracta: todo odontologo tiene una especialidad concreta
 * (General, Cirujano u Ortodoncista) y cada subclase dice cual es. Asi el resto del sistema
 * pregunta al objeto en lugar de usar instanceof o el nombre de la clase.
 * La matricula y el id no cambian una vez creado el odontologo.
 */
public abstract class Odontologo extends Persona {

    private final int matricula;

    protected Odontologo(long id, String nombre, String apellido, int matricula) {
        super(id, nombre, apellido);
        this.matricula = matricula;
    }

    public int getMatricula() { return matricula; }

    // especialidad del odontologo
    public abstract Especialidad getEspecialidad();

    // motivo concreto con el que fue dado de alta (ej. LIMPIEZA, IMPLANTE, BRACKETS_METALICOS)
    public abstract MotivoConsulta getSubespecialidad();

    // motivos por los que se le puede pedir un turno
    public List<MotivoConsulta> getMotivosPosibles() {
        return getEspecialidad().getMotivosPosibles();
    }

    // copia del odontologo con otro id (el original no se modifica)
    public Odontologo conId(long nuevoId) {
        return getEspecialidad().crear(nuevoId, getNombre(), getApellido(), matricula, getSubespecialidad());
    }

    // dos odontologos son el mismo si tienen la misma matricula, sea cual sea su especialidad
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Odontologo)) return false;
        return matricula == ((Odontologo) o).matricula;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(matricula);
    }

    @Override
    public String toString() {
        return "Especialidad: " + getEspecialidad().getNombre() + " (" + getSubespecialidad() + ") | ID: "
                + getId() + " | " + getNombreCompleto() + " | Matrícula: " + matricula;
    }
}