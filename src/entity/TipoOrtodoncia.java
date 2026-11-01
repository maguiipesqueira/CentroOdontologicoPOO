package entity;

import java.math.BigDecimal;

public enum TipoOrtodoncia implements MotivoConsulta {
    BRACKETS_METALICOS("Brackets metálicos", "150000"),
    BRACKETS_CERAMICOS("Brackets cerámicos", "250000");

    private final String nombreLegible;
    private final BigDecimal precioLista;

    TipoOrtodoncia(String nombreLegible, String precioLista) {
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