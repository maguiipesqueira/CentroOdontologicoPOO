package controller;

import entity.Domicilio;
import entity.Paciente;
import service.ServicioPaciente;
import java.util.List;
import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;

public class ControladorPaciente {

    private final ServicioPaciente servicio;

    // constructor guarda el servicio
    public ControladorPaciente(ServicioPaciente servicio) {
        this.servicio = servicio;
    }

    // registra dejando pasar el error de datos
    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email, Domicilio domicilio) throws DatoInvalidoException {
        return servicio.registrarPaciente(nombre, apellido, dni, email, domicilio);
    }

    // busca dejando pasar el error si no existe id
    public Paciente buscarPorId(long id) throws PacienteNoEncontradoException {
        return servicio.buscarPorId(id);
    }

    // busca dejando pasar el error si no existe dni o es negativo
    public Paciente buscarPorDni(int dni) throws PacienteNoEncontradoException, DatoInvalidoException {
        return servicio.buscarPorDni(dni);
    }

    // lista todos los pacientes comunes
    public List<Paciente> listarTodos() {
        return servicio.listarTodos();
    }

    // pide tu lista de pacientes ordenados por apellido con streams
    public List<Paciente> listarPacientesOrdenadosPorApellido() {
        return servicio.listarPacientesOrdenadosPorApellido();
    }

    // actualiza dejando pasar los errores de no encontrado o dato malo
    public Paciente actualizarPaciente(long id, String nombre, String apellido, String email, Domicilio domicilio) throws PacienteNoEncontradoException, DatoInvalidoException {
        return servicio.actualizarPaciente(id, nombre, apellido, email, domicilio);
    }

    // elimina dejando pasar el error si el id no existe
    public void eliminarPaciente(long id) throws PacienteNoEncontradoException {
        servicio.eliminarPaciente(id);
    }
}