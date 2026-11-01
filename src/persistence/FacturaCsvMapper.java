package persistence;

import entity.DesgloseMonto;
import entity.Factura;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class FacturaCsvMapper implements CsvMapper<Factura> {
    private static final List<String> COLUMNAS = List.of("id", "turnoId", "pacienteId", "pacienteNombre", "concepto", "fecha",
            "medioPago", "montoBase", "descuentoObraSocial", "descuentoMedioPago", "montoTotal");
    private final Function<String, String> resolverMedioPago;
    public FacturaCsvMapper(Function<String, String> resolverMedioPago) {
        this.resolverMedioPago = java.util.Objects.requireNonNull(resolverMedioPago);
    }
    @Override public List<String> columnas() { return COLUMNAS; }
    @Override public Map<String, String> aColumnas(Factura f) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", Long.toString(f.getId())); m.put("turnoId", Long.toString(f.getTurnoId()));
        m.put("pacienteId", Long.toString(f.getPacienteId())); m.put("pacienteNombre", f.getPacienteNombre());
        m.put("concepto", f.getConcepto()); m.put("fecha", f.getFecha().toString()); m.put("medioPago", f.getMetodoPagoCodigo());
        m.put("montoBase", f.getMontoBaseDecimal().toPlainString());
        m.put("descuentoObraSocial", f.getDescuentoObraSocialDecimal().toPlainString());
        m.put("descuentoMedioPago", f.getDescuentoMedioPagoDecimal().toPlainString());
        m.put("montoTotal", f.getMontoTotalDecimal().toPlainString());
        return m;
    }
    @Override public Factura desdeColumnas(Map<String, String> m) {
        String medio = resolverMedioPago.apply(req(m, "medioPago"));
        return Factura.builder().id(Long.parseLong(req(m, "id"))).turnoId(Long.parseLong(req(m, "turnoId")))
                .pacienteId(Long.parseLong(req(m, "pacienteId"))).pacienteNombre(m.getOrDefault("pacienteNombre", ""))
                .concepto(m.getOrDefault("concepto", "")).fecha(LocalDate.parse(req(m, "fecha"))).metodoPago(medio)
                .desglose(new DesgloseMonto(new BigDecimal(req(m, "montoBase")),
                        new BigDecimal(req(m, "descuentoObraSocial")), new BigDecimal(req(m, "descuentoMedioPago")),
                        new BigDecimal(req(m, "montoTotal")))).build();
    }
    @Override public Map<String, String> desdeFormatoLegado(List<String> v) {
        if (v.size() != COLUMNAS.size()) throw new IllegalArgumentException("cantidad de columnas de factura inválida: " + v.size());
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < COLUMNAS.size(); i++) m.put(COLUMNAS.get(i), v.get(i));
        return m;
    }
    private static String req(Map<String, String> m, String k) {
        String v = m.get(k); if (v == null || v.isBlank()) throw new IllegalArgumentException("falta la columna " + k); return v;
    }
}
