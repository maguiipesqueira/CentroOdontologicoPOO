package entity;

public class Ortodoncista extends Odontologo {

    private String tipoAparato;

    public Ortodoncista(long id, String nombre, String apellido, int matricula, String tipoAparato) {
        super(id, nombre, apellido, matricula);
        this.tipoAparato = tipoAparato;
    }

    public String getTipoAparato() { return tipoAparato; }
    public void setTipoAparato(String tipoAparato) { this.tipoAparato = tipoAparato; }

    @Override
    public String toString() {
        return "Especialidad: Ortodoncista (" + tipoAparato + ") | " + super.toString();
    }
}
