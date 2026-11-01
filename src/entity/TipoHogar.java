package entity;

public enum TipoHogar {
    CASA("Casa"),
    DEPARTAMENTO("Departamento"),
    PH("PH");

    private final String nombreLegible;

    TipoHogar(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    // nombre para mostrar al usuario
    public String getNombreLegible() {
        return nombreLegible;
    }
}