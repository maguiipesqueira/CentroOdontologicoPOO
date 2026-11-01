package entity;

import java.math.BigDecimal;

public enum TipoConsulta implements MotivoConsulta {
    LIMPIEZA("Limpieza", "25000"),
    CONTROL("Control", "15000"),
    URGENCIA("Urgencia", "40000"),
    BLANQUEAMIENTO("Blanqueamiento", "90000");

    private final String nombreLegible;
    private final BigDecimal precioLista;

    TipoConsulta(String nombreLegible, String precioLista) {
        this.nombreLegible = nombreLegible;
        this.precioLista = new BigDecimal(precioLista).setScale(2);
    }

    @Override
    public String getNombreLegible() {
        return nombreLegible;
    }

    @Override
    public BigDecimal getPrecioLista() {
        return precioLista;
    }
}