package entity;

import java.util.Objects;

public class Ortodoncista extends Odontologo {

    private final TipoOrtodoncia tipoOrtodoncia;

    public Ortodoncista(long id, String nombre, String apellido, int matricula, TipoOrtodoncia tipoOrtodoncia) {
        super(id, nombre, apellido, matricula);
        this.tipoOrtodoncia = Objects.requireNonNull(tipoOrtodoncia, "El tipo no puede ser nulo");
    }

    public TipoOrtodoncia getTipoOrtodoncia() { return tipoOrtodoncia; }

    @Override
    public Especialidad getEspecialidad() { return Especialidad.ORTODONCISTA; }

    @Override
    public MotivoConsulta getSubespecialidad() { return tipoOrtodoncia; }
}