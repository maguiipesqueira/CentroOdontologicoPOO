package controller;

import entity.MotivoConsulta;
import java.time.LocalDate;
import java.time.LocalTime;

/** Solicitud completa para reservar un turno. */
public record SolicitudTurno(
        long pacienteId,
        long odontologoId,
        LocalDate fecha,
        LocalTime hora,
        MotivoConsulta motivoConsulta) {
}
