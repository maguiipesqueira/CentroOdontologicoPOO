package service;

import entity.Especialidad;
import entity.MotivoConsulta;
import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;
import repository.GeneradorIds;
import repository.RepositorioOdontologo;
import repository.RepositorioTurno;

import java.util.List;
import java.util.Objects;

public class ServicioOdontologo {

    private final RepositorioOdontologo repositorio;
    private final RepositorioTurno repositorioTurnos;
    private final GeneradorIds generadorIds;

    public ServicioOdontologo(RepositorioOdontologo repositorio, RepositorioTurno repositorioTurnos) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio de odontólogos es obligatorio.");
        this.repositorioTurnos = Objects.requireNonNull(repositorioTurnos, "El repositorio de turnos es obligatorio.");
        this.generadorIds = new GeneradorIds();
    }

    public Odontologo registrarOdontologo(Odontologo odontologo) throws DatoInvalidoException {
        if (odontologo == null) {
            throw new DatoInvalidoException("Odontólogo", "El odontólogo no puede ser nulo.");
        }

        ValidadorPersona.validarNombreApellido(odontologo.getNombre(), odontologo.getApellido());
        validarMatricula(odontologo.getMatricula());
        if (repositorio.buscarPorMatricula(odontologo.getMatricula()).isPresent()) {
            throw new DatoInvalidoException("Matrícula", "Ya existe un odontólogo con esa matrícula.");
        }

        long nuevoId = generadorIds.siguienteId(() -> repositorio.listarTodos().stream()
                .mapToLong(Odontologo::getId).max().orElse(0L));
        Odontologo guardado = odontologo.conId(nuevoId);
        repositorio.guardar(guardado);
        return guardado;
    }

    public Odontologo buscarPorId(long id) throws OdontologoNoEncontradoException {
        return repositorio.buscarPorId(id).orElseThrow(() -> new OdontologoNoEncontradoException(id));
    }

    public Odontologo buscarPorMatricula(int matricula)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        validarMatricula(matricula);
        return repositorio.buscarPorMatricula(matricula)
                .orElseThrow(() -> new OdontologoNoEncontradoException(matricula));
    }

    public List<Odontologo> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * Actualiza todos los datos editables y reconstruye el subtipo correspondiente.
     * La matrícula debe seguir siendo única y la subespecialidad debe pertenecer a la especialidad.
     */
    public Odontologo actualizarOdontologo(long id, String nombre, String apellido, int matricula,
                                            Especialidad especialidad, MotivoConsulta subespecialidad)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        Odontologo actual = buscarPorId(id);
        ValidadorPersona.validarNombreApellido(nombre, apellido);
        validarMatricula(matricula);

        Odontologo conMatricula = repositorio.buscarPorMatricula(matricula).orElse(null);
        if (conMatricula != null && conMatricula.getId() != id) {
            throw new DatoInvalidoException("Matrícula", "Ya existe un odontólogo con esa matrícula.");
        }

        if (especialidad == null || subespecialidad == null) {
            throw new DatoInvalidoException("Especialidad", "La especialidad y la subespecialidad son obligatorias.");
        }

        final Odontologo actualizado;
        try {
            actualizado = especialidad.crear(id, nombre, apellido, matricula, subespecialidad);
        } catch (IllegalArgumentException ex) {
            throw new DatoInvalidoException("Subespecialidad", ex.getMessage());
        }
        repositorio.actualizar(actualizado);
        return actualizado;
    }

    /**
     * Compatibilidad temporal para las pantallas y el controlador actuales.
     * Conserva matrícula, especialidad y subespecialidad.
     */
    public Odontologo actualizarOdontologo(long id, String nombre, String apellido)
            throws OdontologoNoEncontradoException, DatoInvalidoException {
        Odontologo actual = buscarPorId(id);
        return actualizarOdontologo(id, nombre, apellido, actual.getMatricula(),
                actual.getEspecialidad(), actual.getSubespecialidad());
    }

    public void eliminarOdontologo(long id) throws OdontologoNoEncontradoException, DatoInvalidoException {
        buscarPorId(id);
        if (!repositorioTurnos.buscarPorOdontologo(id).isEmpty()) {
            throw new DatoInvalidoException("Odontólogo",
                    "No se puede eliminar porque tiene turnos asociados.");
        }
        repositorio.eliminar(id);
    }

    /** Punto de entrada controlado para reconstruir datos desde persistencia hasta la etapa 24. */
    public void guardarDesdePersistencia(Odontologo odontologo) {
        repositorio.guardar(Objects.requireNonNull(odontologo));
    }

    private void validarMatricula(int matricula) throws DatoInvalidoException {
        if (matricula <= 0) {
            throw new DatoInvalidoException("Matrícula", "Debe ser un número mayor a cero.");
        }
    }
}
