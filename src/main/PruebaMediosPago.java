package main;

import pago.Efectivo;
import pago.MedioPago;
import pago.MercadoPago;
import pago.Tarjeta;

import java.util.List;

// PRUEBA TEMPORAL de la Etapa 4: se corre, se mira la salida y se borra
public class PruebaMediosPago {
    public static void main(String[] args) {
        List<MedioPago> medios = List.of(new MercadoPago(), new Tarjeta(), new Efectivo());

        for (MedioPago m : medios) {
            System.out.println(m.getNombre() + " -> descuento propio: " + (m.getPorcentajeDescuento() * 100) + "%");
        }
    }
}