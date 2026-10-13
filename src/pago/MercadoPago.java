package pago;

// Pago con Mercado Pago: no tiene descuento propio
public class MercadoPago implements MedioPago {

    // nombre del medio
    @Override
    public String getNombre() {
        return "Mercado Pago";
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
