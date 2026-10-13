package service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Resultado del cierre del dia: cuanto se recibio en la jornada, cuantas facturas fueron y el detalle por medio de pago
public class CierreDelDia {

    private final LocalDate fecha;
    private final int cantidadFacturas;
    private final double montoTotal;
    private final Map<String, Double> totalPorMedioPago;

    // constructor con todos los datos (guarda una copia del detalle para que no se pueda modificar desde afuera)
    public CierreDelDia(LocalDate fecha, int cantidadFacturas, double montoTotal, Map<String, Double> totalPorMedioPago) {
        this.fecha = fecha;
        this.cantidadFacturas = cantidadFacturas;
        this.montoTotal = montoTotal;
        this.totalPorMedioPago = Collections.unmodifiableMap(new LinkedHashMap<>(totalPorMedioPago));
    }

    // devuelve la fecha del cierre
    public LocalDate getFecha() { return fecha; }
    // devuelve cuantas facturas se emitieron
    public int getCantidadFacturas() { return cantidadFacturas; }
    // devuelve el monto total recibido en la jornada
    public double getMontoTotal() { return montoTotal; }
    // devuelve cuanto se recibio por cada medio de pago (nombre del medio -> total)
    public Map<String, Double> getTotalPorMedioPago() { return totalPorMedioPago; }

    // texto con el resumen del cierre
    @Override
    public String toString() {
        return "CIERRE DEL DIA " + fecha +
                " | Facturas: " + cantidadFacturas +
                " | Total recibido: $" + montoTotal +
                " | Por medio: " + totalPorMedioPago;
    }
}