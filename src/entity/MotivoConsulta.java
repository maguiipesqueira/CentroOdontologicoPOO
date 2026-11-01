package entity;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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

    // busca un motivo por su nombre tecnico (ej. "LIMPIEZA"), distinguiendo mayusculas.
    // Se usa al recargar los turnos guardados. Los nombres de los tres enums no se repiten entre si
    static Optional<MotivoConsulta> desdeNombre(String nombre) {
        return Stream.<MotivoConsulta[]>of(TipoConsulta.values(), TipoCirugia.values(), TipoOrtodoncia.values())
                .flatMap(Arrays::stream)
                .filter(motivo -> motivo.name().equals(nombre))
                .findFirst();
    }
}