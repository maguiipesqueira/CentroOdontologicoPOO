package entity;

/**
 * Motivo por el que se pide un turno. Lo implementan los tres enums de tratamiento
 * (TipoConsulta, TipoCirugia y TipoOrtodoncia), asi el resto del sistema puede trabajar con un
 * tipo comun en lugar de Object.
 */
public interface MotivoConsulta {

    // nombre tecnico y estable del valor (es el que se guarda en los archivos y con el que se busca el precio)
    String name();

    // nombre para mostrar al usuario (ej. "Extraccion de muela")
    String getNombreLegible();
}