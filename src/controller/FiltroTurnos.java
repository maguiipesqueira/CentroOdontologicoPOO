package controller;

import entity.EstadoTurno;
import java.time.LocalDate;

/** Filtros opcionales combinables para la agenda. Los identificadores nulos significan "todos". */
public record FiltroTurnos(
        Long pacienteId,
        Long odontologoId,
        LocalDate desde,
        LocalDate hasta,
        EstadoTurno estado) {

    public FiltroTurnos {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final.");
        }
    }

    public static FiltroTurnos todos() {
        return new FiltroTurnos(null, null, null, null, null);
    }
}
