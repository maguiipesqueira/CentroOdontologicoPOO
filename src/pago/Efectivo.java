package pago;

// Pago en efectivo: tiene un 10% de descuento adicional
public class Efectivo implements MedioPago {

    private static final double DESCUENTO = 0.10;

    // nombre del medio
    @Override
    public String getNombre() {
        return "Efectivo";
    }

    // 10% de descuento
    @Override
    public double getPorcentajeDescuento() {
        return DESCUENTO;
    }

    // el nombre es lo que se ve en un JComboBox
    @Override
    public String toString() {
        return getNombre();
    }
}