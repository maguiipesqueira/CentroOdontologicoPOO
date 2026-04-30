package entity;

public class General extends Odontologo {

    private String tipoConsulta;

    public General(long id, String nombre, String apellido, int matricula, String tipoConsulta) {
        super(id, nombre, apellido, matricula);
        this.tipoConsulta = tipoConsulta;
    }

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    @Override
    public String toString() {
        return "Especialidad: General (" + tipoConsulta + ") | " + super.toString();
    }
}
