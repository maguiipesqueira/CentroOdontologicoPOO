package service;

import entity.MotivoConsulta;
import entity.TipoCirugia;
import entity.TipoConsulta;
import entity.TipoOrtodoncia;
import exception.DatoInvalidoException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Catálogo único de precios de lista, indexado por el motivo tipado del turno. */
public final class Tarifario {
    private final Map<MotivoConsulta, BigDecimal> precios;
    private final Map<String, BigDecimal> preciosLegados;

    public Tarifario() {
        Map<MotivoConsulta, BigDecimal> valores = new HashMap<>();
        cargar(valores, TipoConsulta.CONTROL, "15000");
        cargar(valores, TipoConsulta.LIMPIEZA, "25000");
        cargar(valores, TipoConsulta.URGENCIA, "40000");
        cargar(valores, TipoConsulta.BLANQUEAMIENTO, "90000");
        cargar(valores, TipoCirugia.EXTRACCION_MUELA, "60000");
        cargar(valores, TipoCirugia.CIRUGIA_ENCIA, "120000");
        cargar(valores, TipoCirugia.IMPLANTE, "450000");
        cargar(valores, TipoCirugia.CIRUGIA_MAXILOFACIAL, "600000");
        cargar(valores, TipoOrtodoncia.BRACKETS_METALICOS, "150000");
        cargar(valores, TipoOrtodoncia.BRACKETS_CERAMICOS, "250000");
        precios = Collections.unmodifiableMap(valores);
        Map<String, BigDecimal> legado = new HashMap<>();
        valores.forEach((motivo, precio) -> legado.put(motivo.toString(), precio));
        preciosLegados = Collections.unmodifiableMap(legado);
    }

    private static void cargar(Map<MotivoConsulta, BigDecimal> destino, MotivoConsulta motivo, String precio) {
        destino.put(motivo, new BigDecimal(precio).setScale(2));
    }

    public BigDecimal precioDecimalDe(MotivoConsulta motivo) throws DatoInvalidoException {
        if (motivo == null) throw new DatoInvalidoException("Tratamiento", "El turno no tiene un tratamiento cargado.");
        BigDecimal precio = precios.get(motivo);
        if (precio == null) throw new DatoInvalidoException("Tratamiento", "No hay un precio cargado para '" + motivo + "'.");
        return precio;
    }

    /** API compatible: acepta el motivo tipado o el nombre técnico histórico. */
    public double precioDe(Object motivo) throws DatoInvalidoException {
        if (motivo instanceof MotivoConsulta tipado) return precioDecimalDe(tipado).doubleValue();
        if (motivo instanceof String texto) {
            BigDecimal precio = preciosLegados.get(texto);
            if (precio != null) return precio.doubleValue();
            throw new DatoInvalidoException("Tratamiento", "No hay un precio cargado para '" + texto + "'.");
        }
        throw new DatoInvalidoException("Tratamiento", motivo == null
                ? "El turno no tiene un tratamiento cargado." : "Tipo de tratamiento no reconocido.");
    }
}
