package entity;

public class Cirujano extends Odontologo {

    private TipoCirugia tipoCirugia;

    public Cirujano(long id, String nombre, String apellido, int matricula, TipoCirugia tipoCirugia) {
        super(id, nombre, apellido, matricula);
        this.tipoCirugia = tipoCirugia;
    }


    public TipoCirugia getTipoCirugia() {
        return tipoCirugia;
    }

    public void setTipoCirugia(TipoCirugia tipoCirugia) {
        this.tipoCirugia = tipoCirugia;
    }
    @Override
    public String toString() {
        return "Especialidad: Cirujano (" + tipoCirugia + ") | " + super.toString();
    }
}
