package service;

import entity.Odontologo;
import repository.RepositorioOdontologo;

import java.util.List;

public class ServicioOdontologo {

    private final RepositorioOdontologo repositorio;

    // guarda el repo
    public ServicioOdontologo(RepositorioOdontologo repositorio) {

        this.repositorio = repositorio;
    }

    // valida y guarda el odontólogo (sirve para cualquier subclase)
    public Odontologo registrarOdontologo(Odontologo odontologo) {

        if (!validarObjeto(odontologo)) return null;
        if (!validarNombreApellido(odontologo.getNombre(), odontologo.getApellido())) return null;
        if (!validarMatricula(odontologo.getMatricula())) return null;
        if (!validarDuplicado(odontologo.getMatricula())) return null;

        repositorio.guardar(odontologo);

        System.out.println("Odontólogo registrado correctamente.");
        return odontologo;
    }

    // busca por id
    public Odontologo buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    // busca por matrícula
    public Odontologo buscarPorMatricula(int matricula) {
        return repositorio.buscarPorMatricula(matricula);
    }

    // lista todos
    public List<Odontologo> listarTodos() {
        return repositorio.listarTodos();
    }

    // cambia nombre y apellido
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

    // borra si está
    public void eliminarOdontologo(long id) {

        Odontologo odontologo = buscarPorId(id);

        if (odontologo == null) {
            System.out.println("Error: no existe un odontólogo con ese ID.");
            return;
        }

        repositorio.eliminar(id);
    }

    // no puede ser null el objeto
    private boolean validarObjeto(Odontologo odontologo) {
        if (odontologo == null) {
            System.out.println("Error: el odontólogo no puede ser nulo.");
            return false;
        }
        return true;
    }

    // nombre y apellido con texto
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

    // matrícula positiva
    private boolean validarMatricula(int matricula) {

        if (matricula <= 0) {
            System.out.println("Error: la matrícula debe ser mayor a cero.");
            return false;
        }

        return true;
    }

    // que no esté repetida la matrícula
    private boolean validarDuplicado(int matricula) {

        if (repositorio.existeMatricula(matricula)) {
            System.out.println("Error: ya existe un odontólogo con esa matrícula.");
            return false;
        }

        return true;
    }
}
