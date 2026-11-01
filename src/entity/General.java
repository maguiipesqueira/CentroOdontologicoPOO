package entity;

import java.util.Objects;

public class General extends Odontologo {

    private final TipoConsulta tipoConsulta;

    public General(long id, String nombre, String apellido, int matricula, TipoConsulta tipoConsulta) {
        super(id, nombre, apellido, matricula);
        this.tipoConsulta = Objects.requireNonNull(tipoConsulta, "El tipo no puede ser nulo");
    }

    public TipoConsulta getTipoConsulta() { return tipoConsulta; }

    @Override
    public Especialidad getEspecialidad() { return Especialidad.GENERAL; }

    @Override
    public MotivoConsulta getSubespecialidad() { return tipoConsulta; }
}