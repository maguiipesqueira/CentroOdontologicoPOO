package pago;

// Contrato de un medio de pago. El resto del sistema trabaja contra esta interfaz
// y no conoce cada medio en particular: para agregar uno nuevo se crea una clase que la implemente
public interface MedioPago {

    // nombre que se muestra en pantalla y se guarda en la factura (ej "Efectivo")
    String getNombre();

    // descuento propio del medio como fraccion (0.10 = 10%, 0.0 = sin descuento)
    double getPorcentajeDescuento();
}