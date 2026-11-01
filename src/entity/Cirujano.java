package entity;

import java.util.Objects;

public class Cirujano extends Odontologo {

    private final TipoCirugia tipoCirugia;

    public Cirujano(long id, String nombre, String apellido, int matricula, TipoCirugia tipoCirugia) {
        super(id, nombre, apellido, matricula);
        this.tipoCirugia = Objects.requireNonNull(tipoCirugia, "El tipo no puede ser nulo");
    }

    public TipoCirugia getTipoCirugia() { return tipoCirugia; }

    @Override
    public Especialidad getEspecialidad() { return Especialidad.CIRUJANO; }

    @Override
    public MotivoConsulta getSubespecialidad() { return tipoCirugia; }
}