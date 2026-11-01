package controller;

import entity.MotivoConsulta;
import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;
import service.ServicioOdontologo;

import java.util.List;
import java.util.Objects;

/** Coordina los casos de uso de odontólogos y centraliza la creación por especialidad. */
public class ControladorOdontologo {

    private final ServicioOdontologo servicio;

    public ControladorOdontologo(ServicioOdontologo servicio) {
        this.servicio = Objects.requireNonNull(servicio, "El servicio de odontólogos es obligatorio.");
    }

    public Odontologo registrarOdontologo(DatosOdontologo datos) throws DatoInvalidoException {
        DatosOdontologo entrada = requerirDatos(datos);
        if (entrada.especialidad() == null) {
            throw new DatoInvalidoException("Especialidad", "Debe seleccionar una especialidad.");
        }
        MotivoConsulta sub = entrada.subespecialidad() == null
                ? entrada.especialidad().getSubespecialidadPorDefecto() : entrada.subespecialidad();
        final Odontologo nuevo;
        try {
            nuevo = entrada.especialidad().crear(0L, entrada.nombre(), entrada.apellido(), entrada.matricula(), sub);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new DatoInvalidoException("Subespecialidad", ex.getMessage());
        }
        return servicio.registrarOdontologo(nuevo);
    }

    /** Compatibilidad con las pantallas que todavía construyen el subtipo antes de llamar al controlador. */
    public Odontologo registrarOdontologo(Odontologo odontologo) throws DatoInvalidoException {
        return servicio.registrarOdontologo(odontologo);
    }

    public Odontologo buscarPorId(long id) throws OdontologoNoEncontradoException {
        return servicio.buscarPorId(id);
    }

    public Odontologo buscarPorMatricula(int matricula)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        return servicio.buscarPorMatricula(matricula);
    }

    public List<Odontologo> listarTodos() {
        return servicio.listarTodos();
    }

    public Odontologo actualizarOdontologo(long id, DatosOdontologo datos)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        DatosOdontologo entrada = requerirDatos(datos);
        if (entrada.especialidad() == null) {
            throw new DatoInvalidoException("Especialidad", "Debe seleccionar una especialidad.");
        }
        MotivoConsulta sub = entrada.subespecialidad() == null
                ? entrada.especialidad().getSubespecialidadPorDefecto() : entrada.subespecialidad();
        return servicio.actualizarOdontologo(id, entrada.nombre(), entrada.apellido(),
                entrada.matricula(), entrada.especialidad(), sub);
    }

    /** Compatibilidad temporal con el formulario anterior, que solo edita nombre y apellido. */
    public Odontologo actualizarOdontologo(long id, String nombre, String apellido)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        return servicio.actualizarOdontologo(id, nombre, apellido);
    }

    public void eliminarOdontologo(long id) throws OdontologoNoEncontradoException, DatoInvalidoException {
        servicio.eliminarOdontologo(id);
    }

    private DatosOdontologo requerirDatos(DatosOdontologo datos) throws DatoInvalidoException {
        if (datos == null) throw new DatoInvalidoException("Odontólogo", "Los datos del odontólogo son obligatorios.");
        return datos;
    }
}
