package entity;

public enum TipoConsulta implements MotivoConsulta {
    LIMPIEZA("Limpieza"),
    CONTROL("Control"),
    URGENCIA("Urgencia"),
    BLANQUEAMIENTO("Blanqueamiento");

    private final String nombreLegible;

    TipoConsulta(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    @Override
    public String getNombreLegible() {
        return nombreLegible;
    }
}