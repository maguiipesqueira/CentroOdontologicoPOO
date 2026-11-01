package entity;

// ciclo de vida de un turno. La urgencia no es un estado: es un motivo (TipoConsulta.URGENCIA)
public enum EstadoTurno {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado"),
    COMPLETADO("Completado");

    private final String nombreLegible;

    EstadoTurno(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    // nombre para mostrar al usuario
    public String getNombreLegible() {
        return nombreLegible;
    }
}