package service;

import entity.Domicilio;
import entity.Paciente;
import repository.RepositorioPaciente;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de Pacientes.
 * Aplica SRP: solo contiene lógica de negocio relacionada con Paciente.
 * Aplica patrón GRASP Controller: orquesta las operaciones delegando al repositorio.
 * Aplica patrón GRASP Creator: crea objetos Paciente cuando tiene los datos necesarios.
 */
public class ServicioPaciente {

    private final RepositorioPaciente repositorio;
    private long contadorId = 1L;

    public ServicioPaciente(RepositorioPaciente repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Registra un nuevo paciente con validaciones de negocio.
     * @throws IllegalArgumentException si los datos son inválidos o el DNI ya existe.
     */
    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email,
                                      Domicilio domicilio) {
        validarNombreApellido(nombre, apellido);
        validarDni(dni);
        validarEmail(email);

        if (repositorio.existeDni(dni)) {
            throw new IllegalArgumentException(
                    "ERROR: Ya existe un paciente registrado con DNI " + dni + ".");
        }

        // Patrón Creator: ServicioPaciente tiene los datos y crea el objeto
        Paciente nuevo = new Paciente(contadorId++, nombre, apellido, dni, email,
                LocalDate.now(), domicilio);
        repositorio.guardar(nuevo);
        return nuevo;
    }

    public Optional<Paciente> buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    public Optional<Paciente> buscarPorDni(int dni) {
        return repositorio.buscarPorDni(dni);
    }

    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * Actualiza los datos de un paciente existente.
     */
    public void actualizarPaciente(long id, String nombre, String apellido,
                                   String email, Domicilio domicilio) {
        Paciente paciente = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un paciente con ID " + id + "."));

        validarNombreApellido(nombre, apellido);
        validarEmail(email);

        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setEmail(email);
        if (domicilio != null) paciente.setDomicilio(domicilio);

        repositorio.actualizar(paciente);
    }

    /**
     * Elimina un paciente por ID.
     */
    public void eliminarPaciente(long id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un paciente con ID " + id + "."));
        repositorio.eliminar(id);
    }

    // --- Validaciones privadas (SRP: la lógica de validación vive en el service) ---

    private void validarNombreApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("ERROR: El nombre no puede estar vacío.");
        if (apellido == null || apellido.isBlank())
            throw new IllegalArgumentException("ERROR: El apellido no puede estar vacío.");
    }

    private void validarDni(int dni) {
        if (dni <= 0)
            throw new IllegalArgumentException("ERROR: El DNI debe ser un número positivo.");
    }

    private void validarEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("ERROR: El email no es válido.");
    }
}
