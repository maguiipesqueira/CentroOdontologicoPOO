package entity;

public class Cirujano extends Odontologo {

    private String tipoCirugia;

    public Cirujano(long id, String nombre, String apellido, int matricula, String tipoCirugia) {
        super(id, nombre, apellido, matricula);
        this.tipoCirugia = tipoCirugia;
    }

    public String getTipoCirugia() { return tipoCirugia; }
    public void setTipoCirugia(String tipoCirugia) { this.tipoCirugia = tipoCirugia; }

    @Override
    public String toString() {
        return "Especialidad: Cirujano (" + tipoCirugia + ") | " + super.toString();
    }
}
