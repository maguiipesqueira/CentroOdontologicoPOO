package pago;

// Pago con tarjeta: no tiene descuento propio
public class Tarjeta implements MedioPago {

    // nombre del medio
    @Override
    public String getNombre() {
        return "Tarjeta";
    }

    // sin descuento
    @Override
    public double getPorcentajeDescuento() {
        return 0.0;
    }

    // el nombre es lo que se ve en un JComboBox
    @Override
    public String toString() {
        return getNombre();
    }
}