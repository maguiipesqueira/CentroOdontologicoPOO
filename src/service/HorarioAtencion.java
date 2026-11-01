package service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Intervalo diario en el que se puede registrar el comienzo de un turno: desde la apertura (inclusive)
 * hasta el cierre (excluido, no se agenda un turno a la hora en que la clínica cierra).
 */
public record HorarioAtencion(LocalTime apertura, LocalTime cierre) {

    public HorarioAtencion {
        Objects.requireNonNull(apertura, "La hora de apertura no puede ser nula.");
        Objects.requireNonNull(cierre, "La hora de cierre no puede ser nula.");
        if (!apertura.isBefore(cierre)) {
            throw new IllegalArgumentException("La apertura debe ser anterior al cierre.");
        }
    }

    /** true si un turno puede comenzar a esa hora. */
    public boolean contiene(LocalTime hora) {
        return hora != null && !hora.isBefore(apertura) && hora.isBefore(cierre);
    }

    /** Horas en punto que abarca el horario (ej. 08:00, 09:00, ... 19:00); sirven para dibujar una agenda. */
    public List<LocalTime> horasEnPunto() {
        List<LocalTime> horas = new ArrayList<>();
        LocalTime hora = apertura.withMinute(0).withSecond(0).withNano(0);
        while (hora.isBefore(cierre)) {
            horas.add(hora);
            LocalTime siguiente = hora.plusHours(1);
            if (!siguiente.isAfter(hora)) break; // pasó la medianoche
            hora = siguiente;
        }
        return List.copyOf(horas);
    }
}