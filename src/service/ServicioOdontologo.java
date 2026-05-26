package service;

import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;
import repository.RepositorioOdontologo;

import java.util.List;

public class ServicioOdontologo {

    private final RepositorioOdontologo repositorio;

    public ServicioOdontologo(RepositorioOdontologo repositorio) {
        this.repositorio = repositorio;
    }

    // Ahora lanza DatoInvalidoException si algo falla
    public Odontologo registrarOdontologo(Odontologo odontologo) throws DatoInvalidoException {
        if (odontologo == null) {
            throw new DatoInvalidoException("Odontologo", "El odontólogo no puede ser nulo.");
        }

        validarNombreApellido(odontologo.getNombre(), odontologo.getApellido());
        validarMatricula(odontologo.getMatricula());

        if (repositorio.existeMatricula(odontologo.getMatricula())) {
            throw new DatoInvalidoException("Matrícula", "Ya existe un odontólogo con esa matrícula.");
        }

        repositorio.guardar(odontologo);
        return odontologo;
    }

    // Lanza OdontologoNoEncontradoException si no existe
    public Odontologo buscarPorId(long id) throws OdontologoNoEncontradoException {
        Odontologo odontologo = repositorio.buscarPorId(id);
        if (odontologo == null) {
            throw new OdontologoNoEncontradoException(id);
        }
        return odontologo;
    }

    // Lanza excepciones si no encuentra o el dato es malo
    public Odontologo buscarPorMatricula(int matricula) throws OdontologoNoEncontradoException, DatoInvalidoException {
        validarMatricula(matricula);

        Odontologo odontologo = repositorio.buscarPorMatricula(matricula);
        if (odontologo == null) {
            throw new OdontologoNoEncontradoException(matricula);
        }
        return odontologo;
    }

    public List<Odontologo> listarTodos() {
        return repositorio.listarTodos();
    }

    // Lanza excepciones de negocio
    public Odontologo actualizarOdontologo(long id, String nombre, String apellido)
            throws OdontologoNoEncontradoException, DatoInvalidoException {

        Odontologo odontologo = buscarPorId(id); // Si no existe, tira el error de no encontrado
        validarNombreApellido(nombre, apellido);

        odontologo.setNombre(nombre);
        odontologo.setApellido(apellido);
        repositorio.actualizar(odontologo);

        return odontologo;
    }

    public void eliminarOdontologo(long id) throws OdontologoNoEncontradoException {
        buscarPorId(id); // Si no existe, tira el error
        repositorio.eliminar(id);
    }

    // Métodos privados de validación ahora lanzan DatoInvalidoException
    private void validarNombreApellido(String nombre, String apellido) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()) {
            throw new DatoInvalidoException("Campos", "Nombre y apellido son requeridos.");
        }
    }

    private void validarMatricula(int matricula) throws DatoInvalidoException {
        if (matricula <= 0) {
            throw new DatoInvalidoException("Matrícula", "Debe ser un número mayor a cero.");
        }
    }
}