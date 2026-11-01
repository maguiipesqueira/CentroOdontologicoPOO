package controller;

import entity.EstadoTurno;
import entity.Turno;
import java.time.LocalDate;
import java.util.List;

/** Indicadores de turnos de una semana calendario (lunes a domingo). */
public record ResumenSemanalTurnos(
        LocalDate desde,
        LocalDate hasta,
        int total,
        int pendientes,
        int confirmados,
        int completados,
        int cancelados,
        int ausentes) {

    public static ResumenSemanalTurnos calcular(LocalDate desde, LocalDate hasta, List<Turno> turnos) {
        return new ResumenSemanalTurnos(
                desde, hasta, turnos.size(), contar(turnos, EstadoTurno.PENDIENTE),
                contar(turnos, EstadoTurno.CONFIRMADO), contar(turnos, EstadoTurno.COMPLETADO),
                contar(turnos, EstadoTurno.CANCELADO), contar(turnos, EstadoTurno.AUSENTE));
    }

    private static int contar(List<Turno> turnos, EstadoTurno estado) {
        return (int) turnos.stream().filter(t -> t.getEstado() == estado).count();
    }
}
