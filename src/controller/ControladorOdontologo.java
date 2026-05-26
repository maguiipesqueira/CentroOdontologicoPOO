
package controller;

import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;
import service.ServicioOdontologo;

import java.util.List;

public class ControladorOdontologo {

    private final ServicioOdontologo servicio;

    public ControladorOdontologo(ServicioOdontologo servicio) {
        this.servicio = servicio;
    }

    // Ahora declara throws para propagar la excepción del servicio
    public Odontologo registrarOdontologo(Odontologo odontologo) throws DatoInvalidoException {
        return servicio.registrarOdontologo(odontologo);
    }

    // Ahora declara throws para propagar la excepción de no encontrado
    public Odontologo buscarPorId(long id) throws OdontologoNoEncontradoException {
        return servicio.buscarPorId(id);
    }

    // Ahora declara throws para propagar ambos tipos de excepciones
    public Odontologo buscarPorMatricula(int matricula) throws OdontologoNoEncontradoException, DatoInvalidoException {
        return servicio.buscarPorMatricula(matricula);
    }

    public List<Odontologo> listarTodos() {
        return servicio.listarTodos();
    }

    // Ahora declara throws para ambos casos
    public Odontologo actualizarOdontologo(long id, String nombre, String apellido)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        return servicio.actualizarOdontologo(id, nombre, apellido);
    }

    // Ahora declara throws para propagar el error de no encontrado
    public void eliminarOdontologo(long id) throws OdontologoNoEncontradoException {
        servicio.eliminarOdontologo(id);
    }
}