package entity;

public class General extends Odontologo {

    private TipoConsulta tipoConsulta;

    // constructor que llama al padre y guarda el tipo de consulta
    public General(long id, String nombre, String apellido, int matricula, TipoConsulta tipoConsulta) {
        super(id, nombre, apellido, matricula);
        this.tipoConsulta = tipoConsulta;
    }

    // devuelve el tipo de consulta
    public TipoConsulta getTipoConsulta() { return tipoConsulta; }
    // cambia el tipo de consulta
    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta; }

    // texto con la especialidad
    @Override
    public String toString() {
        return "Especialidad: General (" + tipoConsulta + ") | " + super.toString();
    }
}
