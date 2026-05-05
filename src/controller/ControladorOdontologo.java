
package controller;

import entity.Odontologo;
import service.ServicioOdontologo;

import java.util.List;

public class ControladorOdontologo {

    private final ServicioOdontologo servicio;

    public ControladorOdontologo(ServicioOdontologo servicio) {
        this.servicio = servicio;
    }

    public Odontologo registrarOdontologo(Odontologo odontologo) {
        return servicio.registrarOdontologo(odontologo);
    }

    public Odontologo buscarPorId(long id) {
        return servicio.buscarPorId(id);
    }

    public Odontologo buscarPorMatricula(int matricula) {
        return servicio.buscarPorMatricula(matricula);
    }

    public List<Odontologo> listarTodos() {
        return servicio.listarTodos();
    }

    public Odontologo actualizarOdontologo(long id, String nombre, String apellido) {
        return servicio.actualizarOdontologo(id, nombre, apellido);
    }

    public void eliminarOdontologo(long id) {
        servicio.eliminarOdontologo(id);
    }
}