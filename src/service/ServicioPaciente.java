package service;

import entity.Domicilio;
import entity.Paciente;
import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;
import repository.RepositorioPaciente;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioPaciente {

    private final RepositorioPaciente repositorio;
    private long contadorId = 1L;

    // constructor guarda el repositorio
    public ServicioPaciente(RepositorioPaciente repositorio) {
        this.repositorio = repositorio;
    }

    // registra un paciente tirando error si los datos fallan o el DNI ya existe
    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email,
                                      Domicilio domicilio) throws DatoInvalidoException {

        validarNombreApellido(nombre, apellido);
        validarDni(dni);
        validarEmail(email);

        // rompe si el DNI ya existe en el mapa
        if (repositorio.existeDni(dni)) {
            throw new DatoInvalidoException("DNI", "El documento ingresado ya se encuentra registrado.");
        }

        Paciente nuevo = new Paciente(contadorId++, nombre, apellido, dni, email,
                LocalDate.now(), domicilio);

        repositorio.guardar(nuevo);
        return nuevo;
    }

    // busca por id y tira excepcion si da null
    public Paciente buscarPorId(long id) throws PacienteNoEncontradoException {
        Paciente paciente = repositorio.buscarPorId(id);
        if (paciente == null) {
            throw new PacienteNoEncontradoException(id);
        }
        return paciente;
    }

    // busca por dni validando que sea positivo
    public Paciente buscarPorDni(int dni) throws PacienteNoEncontradoException, DatoInvalidoException {
        validarDni(dni);

        Paciente paciente = repositorio.buscarPorDni(dni);
        if (paciente == null) {
            throw new PacienteNoEncontradoException(dni);
        }
        return paciente;
    }

    // devuelve todos los pacientes sin orden
    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    // REQUERIMIENTO STREAMS: devuelve la lista ordenada alfabéticamente por apellido
    public List<Paciente> listarPacientesOrdenadosPorApellido() {
        return repositorio.listarTodos().stream()
                .sorted((p1, p2) -> p1.getApellido().compareToIgnoreCase(p2.getApellido()))
                .collect(Collectors.toList());
    }

    // actualiza los datos si encuentra el id o tira error
    public Paciente actualizarPaciente(long id, String nombre, String apellido,
                                       String email, Domicilio domicilio)
            throws PacienteNoEncontradoException, DatoInvalidoException {

        Paciente paciente = buscarPorId(id);

        validarNombreApellido(nombre, apellido);
        validarEmail(email);

        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setEmail(email);

        if (domicilio != null) {
            paciente.setDomicilio(domicilio);
        }

        repositorio.actualizar(paciente);
        return paciente;
    }

    // elimina por id comprobando que exista antes
    public void eliminarPaciente(long id) throws PacienteNoEncontradoException {
        buscarPorId(id);
        repositorio.eliminar(id);
    }

    // error si el nombre o el apellido estan vacios o nulos
    private void validarNombreApellido(String nombre, String apellido) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("Nombre", "El valor del campo es requerido.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new DatoInvalidoException("Apellido", "El valor del campo es requerido.");
        }
    }

    // error si el dni es cero o negativo
    private void validarDni(int dni) throws DatoInvalidoException {
        if (dni <= 0) {
            throw new DatoInvalidoException("DNI", "Debe ser un número entero positivo.");
        }
    }

    // error si el email no contiene el arroba
    private void validarEmail(String email) throws DatoInvalidoException {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new DatoInvalidoException("Email", "El formato ingresado no corresponde a una dirección válida.");
        }
    }
    // expone el repositorio para la persistencia
    public RepositorioPaciente getRepositorio() {
        return repositorio;
    }
}