package persistence;

import entity.Odontologo;
import java.nio.file.Path;

/** Persistencia CSV de odontólogos. */
public final class PersistenciaOdontologo extends RepositorioCsv<Odontologo> {
    public PersistenciaOdontologo(Path archivo) { super(archivo, new OdontologoCsvMapper()); }
}
