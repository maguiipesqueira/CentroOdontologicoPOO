package entity;

public class Cirujano extends Odontologo {

    private TipoCirugia tipoCirugia;

    // constructor con tipo de cirugía
    public Cirujano(long id, String nombre, String apellido, int matricula, TipoCirugia tipoCirugia) {
        super(id, nombre, apellido, matricula);
        this.tipoCirugia = tipoCirugia;
    }

    // devuelve el tipo de cirugía
    public TipoCirugia getTipoCirugia() {
        return tipoCirugia;
    }

    // cambia el tipo de cirugía
    public void setTipoCirugia(TipoCirugia tipoCirugia) {
        this.tipoCirugia = tipoCirugia;
    }

    // texto con especialidad
    @Override
    public String toString() {
        return "Especialidad: Cirujano (" + tipoCirugia + ") | " + super.toString();
    }
}
