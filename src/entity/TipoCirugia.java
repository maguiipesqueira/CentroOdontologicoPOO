package entity;

public enum TipoCirugia implements MotivoConsulta {
    EXTRACCION_MUELA("Extracción de muela"),
    IMPLANTE("Implante"),
    CIRUGIA_ENCIA("Cirugía de encía"),
    CIRUGIA_MAXILOFACIAL("Cirugía maxilofacial");

    private final String nombreLegible;

    TipoCirugia(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    @Override
    public String getNombreLegible() {
        return nombreLegible;
    }
}