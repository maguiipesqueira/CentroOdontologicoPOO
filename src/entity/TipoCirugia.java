package entity;

import java.math.BigDecimal;

public enum TipoCirugia implements MotivoConsulta {
    EXTRACCION_MUELA("Extracción de muela", "60000"),
    IMPLANTE("Implante", "450000"),
    CIRUGIA_ENCIA("Cirugía de encía", "120000"),
    CIRUGIA_MAXILOFACIAL("Cirugía maxilofacial", "600000");

    private final String nombreLegible;
    private final BigDecimal precioLista;

    TipoCirugia(String nombreLegible, String precioLista) {
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