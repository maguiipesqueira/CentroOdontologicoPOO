package entity;

import exception.DatoInvalidoException;

/**
 * Ciclo de vida de un turno. Cada estado sabe a cuales puede pasar (patron State):
 * <pre>
 * PENDIENTE  -> CONFIRMADO | COMPLETADO | CANCELADO | AUSENTE
 * CONFIRMADO -> COMPLETADO | CANCELADO | AUSENTE
 * COMPLETADO, CANCELADO, AUSENTE -> (finales, no pasan a ningun otro)
 * </pre>
 * Cada transicion devuelve el estado nuevo o lanza DatoInvalidoException si no esta permitida.
 * La urgencia no es un estado: es un motivo (TipoConsulta.URGENCIA).
 */
public enum EstadoTurno {

    PENDIENTE("Pendiente") {
        @Override
        public EstadoTurno confirmar() { return CONFIRMADO; }

        @Override
        public EstadoTurno cancelar() { return CANCELADO; }

        @Override
        public EstadoTurno completar() { return COMPLETADO; }

        @Override
        public EstadoTurno marcarAusente() { return AUSENTE; }
    },

    CONFIRMADO("Confirmado") {
        @Override
        public EstadoTurno cancelar() { return CANCELADO; }

        @Override
        public EstadoTurno completar() { return COMPLETADO; }

        @Override
        public EstadoTurno marcarAusente() { return AUSENTE; }
    },

    CANCELADO("Cancelado") {
        @Override
        public EstadoTurno cancelar() throws DatoInvalidoException {
            throw rechazo("Este turno ya fue cancelado.");
        }

        @Override
        public EstadoTurno completar() throws DatoInvalidoException {
            throw rechazo("No se puede completar un turno que fue cancelado.");
        }
    },

    COMPLETADO("Completado") {
        @Override
        public EstadoTurno cancelar() throws DatoInvalidoException {
            throw rechazo("No se puede cancelar un turno completado.");
        }

        @Override
        public EstadoTurno completar() throws DatoInvalidoException {
            throw rechazo("Este turno ya fue completado.");
        }

        @Override
        public EstadoTurno marcarAusente() throws DatoInvalidoException {
            throw rechazo("No se puede marcar como ausente un turno completado.");
        }
    },

    // el paciente no se presento (no es lo mismo que un turno cancelado)
    AUSENTE("Ausente") {
        @Override
        public EstadoTurno cancelar() throws DatoInvalidoException {
            throw rechazo("No se puede cancelar un turno en el que el paciente no asistió.");
        }

        @Override
        public EstadoTurno completar() throws DatoInvalidoException {
            throw rechazo("No se puede completar un turno en el que el paciente no asistió.");
        }

        @Override
        public EstadoTurno marcarAusente() throws DatoInvalidoException {
            throw rechazo("Este turno ya fue marcado como ausente.");
        }
    };

    private final String nombreLegible;

    EstadoTurno(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    // nombre para mostrar al usuario
    public String getNombreLegible() {
        return nombreLegible;
    }

    // true si el turno se puede cobrar: no si se cancelo ni si el paciente no asistio
    public boolean esCobrable() {
        return this != CANCELADO && this != AUSENTE;
    }

    // por defecto una transicion no esta permitida; cada estado sobrescribe solo las que si admite

    // pasa a CONFIRMADO (solo se confirma lo pendiente)
    public EstadoTurno confirmar() throws DatoInvalidoException {
        throw rechazo("Solo se pueden confirmar turnos pendientes.");
    }

    // pasa a CANCELADO
    public EstadoTurno cancelar() throws DatoInvalidoException {
        throw rechazo("No se puede cancelar este turno.");
    }

    // pasa a COMPLETADO
    public EstadoTurno completar() throws DatoInvalidoException {
        throw rechazo("No se puede completar este turno.");
    }

    // pasa a AUSENTE (el paciente no se presento)
    public EstadoTurno marcarAusente() throws DatoInvalidoException {
        throw rechazo("No se puede marcar como ausente este turno.");
    }

    // arma la excepcion de una transicion no permitida (mismo campo y formato que usaba el servicio)
    private static DatoInvalidoException rechazo(String motivo) {
        return new DatoInvalidoException("Estado", motivo);
    }
}