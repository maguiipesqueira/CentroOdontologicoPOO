package entity;

public class Ortodoncista extends Odontologo {

    private TipoOrtodoncia tipoOrtodoncia;

    // constructor con tipo de ortodoncia
    public Ortodoncista(long id, String nombre, String apellido, int matricula, TipoOrtodoncia tipoOrtodoncia) {
        super(id, nombre, apellido, matricula);
        this.tipoOrtodoncia = tipoOrtodoncia;
    }

    // devuelve tipo de ortodoncia
    public TipoOrtodoncia getTipoOrtodoncia() {
        return tipoOrtodoncia; }
    // cambia tipo de ortodoncia
    public void setTipoOrtodoncia(TipoOrtodoncia tipoOrtodoncia) {
        this.tipoOrtodoncia = tipoOrtodoncia; }

    // texto con especialidad
    @Override
    public String toString() {
        return "Especialidad: Ortodoncista (" + tipoOrtodoncia + ") | " + super.toString();
    }
}
