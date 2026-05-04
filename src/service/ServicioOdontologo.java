package service;

import entity.Odontologo;
import repository.RepositorioOdontologo;

import java.util.List;

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
     * Registra un odontólogo.
     * Valida datos básicos y evita duplicados por matrícula.
     * Se recibe la entidad ya construida (puede ser Cirujano, Ortodoncista o General).
     * Polimorfismo: el método acepta cualquier subtipo de Odontologo.
     */
    public Odontologo registrarOdontologo(Odontologo odontologo) {

        if (!validarObjeto(odontologo)) return null;
        if (!validarNombreApellido(odontologo.getNombre(), odontologo.getApellido())) return null;
        if (!validarMatricula(odontologo.getMatricula())) return null;
        if (!validarDuplicado(odontologo.getMatricula())) return null;

        repositorio.guardar(odontologo);

        System.out.println("Odontólogo registrado correctamente.");
        return odontologo;
    }

    // Buscar por ID
    public Odontologo buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    // Buscar por matrícula
    public Odontologo buscarPorMatricula(int matricula) {
        return repositorio.buscarPorMatricula(matricula);
    }

    // Listar todos
    public List<Odontologo> listarTodos() {
        return repositorio.listarTodos();
    }

    // Actualizar odontólogo
    public Odontologo actualizarOdontologo(long id, String nombre, String apellido) {

        Odontologo odontologo = buscarPorId(id);

        if (odontologo == null) {
            System.out.println("Error: no existe un odontólogo con ese ID.");
            return null;
        }

        if (!validarNombreApellido(nombre, apellido)) return null;

        odontologo.setNombre(nombre);
        odontologo.setApellido(apellido);

        repositorio.actualizar(odontologo);

        return odontologo;
    }

    // Eliminar odontólogo
    public void eliminarOdontologo(long id) {

        Odontologo odontologo = buscarPorId(id);

        if (odontologo == null) {
            System.out.println("Error: no existe un odontólogo con ese ID.");
            return;
        }

        repositorio.eliminar(id);
    }

    // =========================
    // VALIDACIONES
    // =========================

    private boolean validarObjeto(Odontologo odontologo) {
        if (odontologo == null) {
            System.out.println("Error: el odontólogo no puede ser nulo.");
            return false;
        }
        return true;
    }

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

    private boolean validarMatricula(int matricula) {

        if (matricula <= 0) {
            System.out.println("Error: la matrícula debe ser mayor a cero.");
            return false;
        }

        return true;
    }

    private boolean validarDuplicado(int matricula) {

        if (repositorio.existeMatricula(matricula)) {
            System.out.println("Error: ya existe un odontólogo con esa matrícula.");
            return false;
        }

        return true;
    }
}
