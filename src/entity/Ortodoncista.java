package entity;

public class Ortodoncista extends Odontologo {

    private TipoOrtodoncia tipoOrtodoncia;

    public Ortodoncista(long id, String nombre, String apellido, int matricula, TipoOrtodoncia tipoOrtodoncia) {
        super(id, nombre, apellido, matricula);
        this.tipoOrtodoncia = tipoOrtodoncia;
    }

    public TipoOrtodoncia getTipoOrtodoncia() {
        return tipoOrtodoncia; }
    public void setTipoOrtodoncia(TipoOrtodoncia tipoOrtodoncia) {
        this.tipoOrtodoncia = tipoOrtodoncia; }

    @Override
    public String toString() {
        return "Especialidad: Ortodoncista (" + tipoOrtodoncia + ") | " + super.toString();
    }
}
