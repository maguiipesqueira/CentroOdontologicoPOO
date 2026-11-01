package persistence;

import entity.*;
import repository.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/** Configura y coordina los cuatro almacenes de la aplicación. */
public final class PersistenciaClinica {
    private final Persistencia<Paciente> pacientes;
    private final Persistencia<Odontologo> odontologos;
    private final Persistencia<Turno> turnos;
    private final Persistencia<Factura> facturas;
    private final RepositorioPaciente repoPaciente;
    private final RepositorioOdontologo repoOdontologo;
    private final RepositorioTurno repoTurno;
    private final RepositorioFactura repoFactura;

    public PersistenciaClinica(Path directorio, RepositorioPaciente repoPaciente,
                               RepositorioOdontologo repoOdontologo, RepositorioTurno repoTurno,
                               RepositorioFactura repoFactura, Function<String, String> resolverMedioPago) {
        Path base = java.util.Objects.requireNonNull(directorio).toAbsolutePath().normalize();
        this.repoPaciente = java.util.Objects.requireNonNull(repoPaciente);
        this.repoOdontologo = java.util.Objects.requireNonNull(repoOdontologo);
        this.repoTurno = java.util.Objects.requireNonNull(repoTurno);
        this.repoFactura = java.util.Objects.requireNonNull(repoFactura);
        pacientes = new PersistenciaPaciente(base.resolve("pacientes.txt"));
        odontologos = new PersistenciaOdontologo(base.resolve("odontologos.txt"));
        turnos = new PersistenciaTurno(base.resolve("turnos.txt"),
                id -> repoPaciente.buscarPorId(id).orElse(null), id -> repoOdontologo.buscarPorId(id).orElse(null));
        facturas = new PersistenciaFactura(base.resolve("facturas.txt"), resolverMedioPago);
    }

    /** Carga en orden de dependencias: personas, turnos y finalmente facturas. */
    public void cargar() throws IOException {
        cargarEnRepositorio(pacientes, repoPaciente::guardar, "pacientes");
        cargarEnRepositorio(odontologos, repoOdontologo::guardar, "odontólogos");
        cargarEnRepositorio(turnos, repoTurno::guardar, "turnos");
        cargarEnRepositorio(facturas, repoFactura::guardar, "facturas");
    }

    public void guardar(List<Paciente> ps, List<Turno> ts, List<Odontologo> os, List<Factura> fs) throws IOException {
        pacientes.guardar(ps);
        odontologos.guardar(os);
        turnos.guardar(ts);
        facturas.guardar(fs);
    }

    private static <T> void cargarEnRepositorio(Persistencia<T> persistencia,
                                                java.util.function.Consumer<T> guardar, String entidad) throws IOException {
        ResultadoCarga<T> resultado = persistencia.cargar();
        resultado.registros().forEach(guardar);
        for (ErrorRegistro error : resultado.errores()) {
            System.err.println("No se pudo cargar " + entidad + " (línea " + error.numeroLinea() + "): " + error.motivo());
        }
    }
}
