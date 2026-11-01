package service;

import java.time.LocalTime;
import java.util.Objects;

/** Intervalo diario permitido para registrar turnos, incluyendo ambos extremos. */
public record HorarioAtencion(LocalTime apertura, LocalTime cierre) {

    public HorarioAtencion {
        Objects.requireNonNull(apertura, "La hora de apertura no puede ser nula.");
        Objects.requireNonNull(cierre, "La hora de cierre no puede ser nula.");
        if (apertura.isAfter(cierre)) {
            throw new IllegalArgumentException("La apertura no puede ser posterior al cierre.");
        }
    }

    public boolean contiene(LocalTime hora) {
        return hora != null && !hora.isBefore(apertura) && !hora.isAfter(cierre);
    }
}
