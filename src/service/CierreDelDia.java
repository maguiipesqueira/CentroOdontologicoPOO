package service;

import entity.Factura;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Resultado inmutable y responsable de agregar los importes de una jornada. */
public final class CierreDelDia {
    private final LocalDate fecha;
    private final int cantidadFacturas;
    private final BigDecimal montoTotal;
    private final Map<String, BigDecimal> totalesPorMedio;

    public CierreDelDia(LocalDate fecha, int cantidadFacturas, BigDecimal montoTotal,
                        Map<String, BigDecimal> totalesPorMedio) {
        this.fecha = fecha;
        this.cantidadFacturas = cantidadFacturas;
        this.montoTotal = dinero(montoTotal);
        this.totalesPorMedio = Collections.unmodifiableMap(new LinkedHashMap<>(totalesPorMedio));
    }

    public static CierreDelDia calcular(LocalDate fecha, List<Factura> facturas,
                                        Map<String, String> nombresPorCodigo) {
        Map<String, BigDecimal> porMedio = new LinkedHashMap<>();
        nombresPorCodigo.values().forEach(nombre -> porMedio.put(nombre, BigDecimal.ZERO.setScale(2)));
        BigDecimal total = BigDecimal.ZERO;
        for (Factura factura : facturas) {
            BigDecimal importe = factura.getDesglose().getMontoTotalDecimal();
            total = total.add(importe);
            String nombre = nombresPorCodigo.getOrDefault(factura.getMetodoPagoCodigo(), factura.getMetodoPagoCodigo());
            porMedio.merge(nombre, importe, BigDecimal::add);
        }
        porMedio.replaceAll((k, v) -> dinero(v));
        return new CierreDelDia(fecha, facturas.size(), dinero(total), porMedio);
    }

    private static BigDecimal dinero(BigDecimal valor) { return valor.setScale(2, RoundingMode.HALF_UP); }
    public LocalDate getFecha() { return fecha; }
    public int getCantidadFacturas() { return cantidadFacturas; }
    public BigDecimal getMontoTotalDecimal() { return montoTotal; }
    public Map<String, BigDecimal> getTotalesPorMedioDecimal() { return totalesPorMedio; }
    public double getMontoTotal() { return montoTotal.doubleValue(); }
    public Map<String, Double> getTotalPorMedioPago() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        totalesPorMedio.forEach((k, v) -> resultado.put(k, v.doubleValue()));
        return Collections.unmodifiableMap(resultado);
    }
    @Override public String toString() {
        return "CIERRE DEL DIA " + fecha + " | Facturas: " + cantidadFacturas
                + " | Total recibido: $" + montoTotal + " | Por medio: " + totalesPorMedio;
    }
}
