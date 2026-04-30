package service;

import entity.Odontologo;
import repository.RepositorioOdontologo;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de Odontólogos.
 * Aplica SRP: solo contiene lógica de negocio relacionada con Odontologo.
 * Aplica patrón GRASP Controller: orquesta las operaciones delegando al repositorio.
 */
public class ServicioOdontologo {

    private final RepositorioOdontologo repositorio;

    public ServicioOdontologo(RepositorioOdontologo repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Registra un odontólogo con validaciones.
     * Se recibe la entidad ya construida (puede ser Cirujano, Ortodoncista o General).
     * Polimorfismo: el método acepta cualquier subtipo de Odontologo.
     */
    public Odontologo registrarOdontologo(Odontologo odontologo) {
        validarMatricula(odontologo.getMatricula());
        validarNombreApellido(odontologo.getNombre(), odontologo.getApellido());

        if (repositorio.existeMatricula(odontologo.getMatricula())) {
            throw new IllegalArgumentException(
                    "ERROR: Ya existe un odontólogo con matrícula " + odontologo.getMatricula() + ".");
        }

        repositorio.guardar(odontologo);
        return odontologo;
    }

    public Optional<Odontologo> buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    public Optional<Odontologo> buscarPorMatricula(int matricula) {
        return repositorio.buscarPorMatricula(matricula);
    }

    public List<Odontologo> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * Actualiza nombre y apellido de un odontólogo existente.
     */
    public void actualizarOdontologo(long id, String nombre, String apellido) {
        Odontologo odontologo = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un odontólogo con ID " + id + "."));

        validarNombreApellido(nombre, apellido);

        odontologo.setNombre(nombre);
        odontologo.setApellido(apellido);
        repositorio.actualizar(odontologo);
    }

    /**
     * Elimina un odontólogo por ID.
     */
    public void eliminarOdontologo(long id) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: No existe un odontólogo con ID " + id + "."));
        repositorio.eliminar(id);
    }

    // --- Validaciones ---

    private void validarMatricula(int matricula) {
        if (matricula <= 0)
            throw new IllegalArgumentException(
                    "ERROR: La matrícula debe ser un número positivo. Matrícula recibida: " + matricula);
    }

    private void validarNombreApellido(String nombre, String apellido) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("ERROR: El nombre no puede estar vacío.");
        if (apellido == null || apellido.isBlank())
            throw new IllegalArgumentException("ERROR: El apellido no puede estar vacío.");
    }
}
