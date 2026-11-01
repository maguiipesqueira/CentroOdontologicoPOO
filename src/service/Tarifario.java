package service;

import entity.MotivoConsulta;
import exception.DatoInvalidoException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Catálogo de precios de lista, indexado por el motivo tipado del turno.
 * Por defecto toma el precio que declara cada motivo (MotivoConsulta.getPrecioLista()), de modo que
 * agregar un tratamiento nuevo se hace en un solo lugar. Un Tarifario también se puede armar con
 * otra lista de precios (promociones, pruebas).
 */
public final class Tarifario {

    private final Map<MotivoConsulta, BigDecimal> precios;
    private final Map<String, BigDecimal> preciosLegados;

    /** Tarifario de producción: el precio de lista de cada motivo. */
    public Tarifario() {
        this(preciosDeLista());
    }

    /** Tarifario con precios propios; los motivos que no figuren no se pueden cobrar. */
    public Tarifario(Map<MotivoConsulta, BigDecimal> precios) {
        Objects.requireNonNull(precios, "La lista de precios es obligatoria.");
        Map<MotivoConsulta, BigDecimal> copia = new HashMap<>();
        precios.forEach((motivo, precio) -> copia.put(
                Objects.requireNonNull(motivo, "El motivo es obligatorio."),
                Objects.requireNonNull(precio, "El precio es obligatorio.").setScale(2)));
        this.precios = Collections.unmodifiableMap(copia);
        Map<String, BigDecimal> legado = new HashMap<>();
        copia.forEach((motivo, precio) -> legado.put(motivo.toString(), precio));
        this.preciosLegados = Collections.unmodifiableMap(legado);
    }

    private static Map<MotivoConsulta, BigDecimal> preciosDeLista() {
        Map<MotivoConsulta, BigDecimal> valores = new HashMap<>();
        for (MotivoConsulta motivo : MotivoConsulta.todos()) {
            valores.put(motivo, motivo.getPrecioLista());
        }
        return valores;
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