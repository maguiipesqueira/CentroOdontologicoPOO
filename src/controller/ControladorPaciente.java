package controller;

import entity.Paciente;
import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;
import service.ServicioPaciente;

import java.util.List;
import java.util.Objects;

/** Coordina los casos de uso de pacientes y normaliza los datos recibidos de la UI. */
public class ControladorPaciente {

    private final ServicioPaciente servicio;

    public ControladorPaciente(ServicioPaciente servicio) {
        this.servicio = Objects.requireNonNull(servicio, "El servicio de pacientes es obligatorio.");
    }

    public Paciente registrarPaciente(DatosPaciente datos) throws DatoInvalidoException {
        DatosPaciente entrada = requerirDatos(datos);
        return servicio.registrarPaciente(entrada.nombre(), entrada.apellido(), entrada.dni(),
                entrada.email(), entrada.domicilio(), entrada.tieneObraSocial());
    }

    /** Puente de compatibilidad para llamadas anteriores a la introducción del DTO. */
    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email,
                                      entity.Domicilio domicilio, boolean tieneObraSocial)
            throws DatoInvalidoException {
        return registrarPaciente(new DatosPaciente(nombre, apellido, dni, email, domicilio, tieneObraSocial));
    }

    public Paciente buscarPorId(long id) throws PacienteNoEncontradoException {
        return servicio.buscarPorId(id);
    }

    public Paciente buscarPorDni(int dni) throws PacienteNoEncontradoException, DatoInvalidoException {
        return servicio.buscarPorDni(dni);
    }

    public List<Paciente> listarTodos() {
        return servicio.listarTodos();
    }

    public List<Paciente> listarPacientesOrdenadosPorApellido() {
        return servicio.listarPacientesOrdenadosPorApellido();
    }

    public Paciente actualizarPaciente(long id, DatosPacienteActualizacion datos)
            throws PacienteNoEncontradoException, DatoInvalidoException {
        if (datos == null) throw new DatoInvalidoException("Paciente", "Los datos de actualización son obligatorios.");
        return servicio.actualizarPaciente(id, datos.nombre(), datos.apellido(),
                datos.email(), datos.domicilio(), datos.tieneObraSocial());
    }

    /** Puente de compatibilidad para llamadas anteriores a la introducción del DTO. */
    public Paciente actualizarPaciente(long id, String nombre, String apellido, String email,
                                       entity.Domicilio domicilio, boolean tieneObraSocial)
            throws PacienteNoEncontradoException, DatoInvalidoException {
        return actualizarPaciente(id, new DatosPacienteActualizacion(nombre, apellido, email, domicilio, tieneObraSocial));
    }

    public void eliminarPaciente(long id) throws PacienteNoEncontradoException, DatoInvalidoException {
        servicio.eliminarPaciente(id);
    }

    private DatosPaciente requerirDatos(DatosPaciente datos) throws DatoInvalidoException {
        if (datos == null) throw new DatoInvalidoException("Paciente", "Los datos del paciente son obligatorios.");
        return datos;
    }
}
