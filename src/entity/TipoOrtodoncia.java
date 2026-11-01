package entity;

public enum TipoOrtodoncia implements MotivoConsulta {
    BRACKETS_METALICOS("Brackets metálicos"),
    BRACKETS_CERAMICOS("Brackets cerámicos");

    private final String nombreLegible;

    TipoOrtodoncia(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    @Override
    public String getNombreLegible() {
        return nombreLegible;
    }
}