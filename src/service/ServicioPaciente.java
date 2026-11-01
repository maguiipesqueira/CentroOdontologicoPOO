package service;

import entity.Domicilio;
import entity.Paciente;
import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;
import repository.GeneradorIds;
import repository.RepositorioPaciente;
import repository.RepositorioTurno;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ServicioPaciente {

    private final RepositorioPaciente repositorio;
    private final RepositorioTurno repositorioTurnos;
    private final GeneradorIds generadorIds;
    private final Clock clock;

    public ServicioPaciente(RepositorioPaciente repositorio, RepositorioTurno repositorioTurnos) {
        this(repositorio, repositorioTurnos, Clock.systemDefaultZone());
    }

    public ServicioPaciente(RepositorioPaciente repositorio, RepositorioTurno repositorioTurnos, Clock clock) {
        this.repositorio = java.util.Objects.requireNonNull(repositorio, "El repositorio de pacientes es obligatorio.");
        this.repositorioTurnos = java.util.Objects.requireNonNull(repositorioTurnos, "El repositorio de turnos es obligatorio.");
        this.clock = java.util.Objects.requireNonNull(clock, "El reloj es obligatorio.");
        this.generadorIds = new GeneradorIds();
    }

    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email,
                                      Domicilio domicilio, boolean tieneObraSocial) throws DatoInvalidoException {
        ValidadorPersona.validarNombreApellido(nombre, apellido);
        validarDni(dni);
        validarEmail(email);

        if (repositorio.buscarPorDni(dni).isPresent()) {
            throw new DatoInvalidoException("DNI", "El documento ingresado ya se encuentra registrado.");
        }

        long nuevoId = generadorIds.siguienteId(() -> repositorio.listarTodos().stream()
                .mapToLong(Paciente::getId).max().orElse(0L));

        Paciente nuevo = new Paciente(nuevoId, nombre, apellido, dni, email,
                LocalDate.now(clock), domicilio, tieneObraSocial);
        repositorio.guardar(nuevo);
        return nuevo;
    }

    public Paciente buscarPorId(long id) throws PacienteNoEncontradoException {
        return repositorio.buscarPorId(id).orElseThrow(() -> new PacienteNoEncontradoException(id));
    }

    public Paciente buscarPorDni(int dni) throws PacienteNoEncontradoException, DatoInvalidoException {
        validarDni(dni);
        return repositorio.buscarPorDni(dni).orElseThrow(() -> new PacienteNoEncontradoException(dni));
    }

    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    public List<Paciente> listarPacientesOrdenadosPorApellido() {
        return repositorio.listarTodos().stream()
                .sorted((a, b) -> a.getApellido().compareToIgnoreCase(b.getApellido()))
                .toList();
    }

    public Paciente actualizarPaciente(long id, String nombre, String apellido,
                                       String email, Domicilio domicilio, boolean tieneObraSocial)
            throws PacienteNoEncontradoException, DatoInvalidoException {
        return actualizarPaciente(id, nombre, apellido, email, domicilio, tieneObraSocial, domicilio != null);
    }

    /** Permite distinguir entre conservar el domicilio y reemplazarlo explícitamente por null. */
    public Paciente actualizarPaciente(long id, String nombre, String apellido,
                                       String email, Domicilio domicilio, boolean tieneObraSocial,
                                       boolean reemplazarDomicilio)
            throws PacienteNoEncontradoException, DatoInvalidoException {
        Paciente paciente = buscarPorId(id);
        ValidadorPersona.validarNombreApellido(nombre, apellido);
        validarEmail(email);

        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setEmail(email);
        paciente.setTieneObraSocial(tieneObraSocial);
        if (reemplazarDomicilio) {
            paciente.setDomicilio(domicilio);
        }
        repositorio.actualizar(paciente);
        return paciente;
    }

    public void eliminarPaciente(long id) throws PacienteNoEncontradoException, DatoInvalidoException {
        buscarPorId(id);
        if (!repositorioTurnos.buscarPorPaciente(id).isEmpty()) {
            throw new DatoInvalidoException("Paciente",
                    "No se puede eliminar porque tiene turnos asociados.");
        }
        repositorio.eliminar(id);
    }

    private void validarDni(int dni) throws DatoInvalidoException {
        if (dni <= 0) {
            throw new DatoInvalidoException("DNI", "Debe ser un número entero positivo.");
        }
    }

    private void validarEmail(String email) throws DatoInvalidoException {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new DatoInvalidoException("Email", "El formato ingresado no corresponde a una dirección válida.");
        }
    }
}
