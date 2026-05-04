package entity;

public class General extends Odontologo {

    private TipoConsulta tipoConsulta;

    public General(long id, String nombre, String apellido, int matricula, TipoConsulta tipoConsulta) {
        super(id, nombre, apellido, matricula);
        this.tipoConsulta = tipoConsulta;
    }

    public TipoConsulta getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta; }

    @Override
    public String toString() {
        return "Especialidad: General (" + tipoConsulta + ") | " + super.toString();
    }
}
