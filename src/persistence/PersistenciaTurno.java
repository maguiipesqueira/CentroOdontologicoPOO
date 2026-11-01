package persistence;

import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import java.nio.file.Path;
import java.util.function.Function;

/** Persistencia CSV de turnos, resolviendo sus referencias a personas. */
public final class PersistenciaTurno extends RepositorioCsv<Turno> {
    public PersistenciaTurno(Path archivo, Function<Long, Paciente> pacientes,
                             Function<Long, Odontologo> odontologos) {
        super(archivo, new TurnoCsvMapper(pacientes, odontologos));
    }
}
