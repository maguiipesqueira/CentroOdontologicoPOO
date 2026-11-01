package persistence;

import entity.Paciente;
import java.nio.file.Path;

/** Persistencia CSV de pacientes. */
public final class PersistenciaPaciente extends RepositorioCsv<Paciente> {
    public PersistenciaPaciente(Path archivo) { super(archivo, new PacienteCsvMapper()); }
}
