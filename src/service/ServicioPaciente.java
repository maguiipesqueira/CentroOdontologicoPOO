package service;

import entity.Domicilio;
import entity.Paciente;
import repository.RepositorioPaciente;

import java.time.LocalDate;
import java.util.List;

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

        if (!validarNombreApellido(nombre, apellido)) return null;
        if (!validarDni(dni)) return null;
        if (!validarEmail(email)) return null;

        if (repositorio.existeDni(dni)) {
            System.out.println("Error: ya existe un paciente registrado con ese DNI.");
            return null;
        }

        Paciente nuevo = new Paciente(contadorId++, nombre, apellido, dni, email,
                LocalDate.now(), domicilio);

        repositorio.guardar(nuevo);
        return nuevo;
    }

    public Paciente buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    public Paciente buscarPorDni(int dni) {
        return repositorio.buscarPorDni(dni);
    }

    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * Actualiza los datos de un paciente existente.
     */
    public Paciente actualizarPaciente(long id, String nombre, String apellido,
                                       String email, Domicilio domicilio) {

        Paciente paciente = buscarPorId(id);

        if (paciente == null) {
            System.out.println("Error: no existe un paciente con ese ID.");
            return null;
        }

        if (!validarNombreApellido(nombre, apellido)) return null;
        if (!validarEmail(email)) return null;

        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setEmail(email);

        if (domicilio != null) {
            paciente.setDomicilio(domicilio);
        }

        repositorio.actualizar(paciente);
        return paciente;
    }

    /**
     * Elimina un paciente por ID.
     */
    public void eliminarPaciente(long id) {
        Paciente paciente = buscarPorId(id);

        if (paciente == null) {
            System.out.println("Error: no existe un paciente con ese ID.");
            return;
        }

        repositorio.eliminar(id);
    }

    // --- Validaciones privadas (SRP: la lógica de validación vive en el service) ---

    private boolean validarNombreApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank()) {
            System.out.println("Error: el nombre no puede estar vacío.");
            return false;
        }

        if (apellido == null || apellido.isBlank()) {
            System.out.println("Error: el apellido no puede estar vacío.");
            return false;
        }

        return true;
    }

    private boolean validarDni(int dni) {
        if (dni <= 0) {
            System.out.println("Error: el DNI debe ser un número positivo.");
            return false;
        }

        return true;
    }

    private boolean validarEmail(String email) {
        if (email == null || !email.contains("@")) {
            System.out.println("Error: el email no es válido.");
            return false;
        }

        return true;
    }
}
