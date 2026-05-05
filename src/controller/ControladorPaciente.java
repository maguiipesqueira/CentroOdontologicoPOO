
package controller;

import entity.Domicilio;
import entity.Paciente;
import service.ServicioPaciente;

import java.util.List;

public class ControladorPaciente {

    private final ServicioPaciente servicio;

    public ControladorPaciente(ServicioPaciente servicio) {
        this.servicio = servicio;
    }

    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email, Domicilio domicilio) {
        return servicio.registrarPaciente(nombre, apellido, dni, email, domicilio);
    }

    public Paciente buscarPorId(long id) {
        return servicio.buscarPorId(id);
    }

    public Paciente buscarPorDni(int dni) {
        return servicio.buscarPorDni(dni);
    }

    public List<Paciente> listarTodos() {
        return servicio.listarTodos();
    }

    public Paciente actualizarPaciente(long id, String nombre, String apellido, String email, Domicilio domicilio) {
        return servicio.actualizarPaciente(id, nombre, apellido, email, domicilio);
    }

    public void eliminarPaciente(long id) {
        servicio.eliminarPaciente(id);
    }
}
