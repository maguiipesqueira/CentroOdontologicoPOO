package service;

import entity.Domicilio;
import entity.Paciente;
import repository.RepositorioPaciente;

import java.time.LocalDate;
import java.util.List;

import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;
import java.util.stream.Collectors;

public class ServicioPaciente {

    private final RepositorioPaciente repositorio;
    private long contadorId = 1L;

    // guarda el repositorio para usarlo después
    public ServicioPaciente(RepositorioPaciente repositorio) {
        this.repositorio = repositorio;
    }

    // da de alta un paciente tirando error si los datos fallan o el DNI ya existe
    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email,
                                      Domicilio domicilio) throws DatoInvalidoException {

        // Llamamos a los controle si alguno falla, el método explota solo hacia arriba
        validarNombreApellido(nombre, apellido);
        validarDni(dni);
        validarEmail(email);

        // Si el DNI ya está registrado en el mapa tira eeoe
        if (repositorio.existeDni(dni)) {
            throw new DatoInvalidoException("DNI", "Ya existe un paciente con ese DNI.");
        }

        Paciente nuevo = new Paciente(contadorId++, nombre, apellido, dni, email,
                LocalDate.now(), domicilio);

        repositorio.guardar(nuevo);
        return nuevo;
    }

    // busca por id en el repo
    public Paciente buscarPorId(long id) {
        return repositorio.buscarPorId(id);
    }

    // busca por dni en el repo
    public Paciente buscarPorDni(int dni) {
        return repositorio.buscarPorDni(dni);
    }

    // trae todos los pacientes
    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    // Modifica los datos del paciente si existe sino error
    public Paciente actualizarPaciente(long id, String nombre, String apellido,
                                       String email, Domicilio domicilio)
            throws PacienteNoEncontradoException, DatoInvalidoException {

        Paciente paciente = buscarPorId(id);

        // Si el mapa nos devolvió null (el paciente no existe) sale error
        if (paciente == null) {
            throw new PacienteNoEncontradoException(id);
        }

        // si fallan interrumpe el método solos
        validarNombreApellido(nombre, apellido);
        validarEmail(email);

        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setEmail(email);

        if (domicilio != null) {
            paciente.setDomicilio(domicilio);
        }

        repositorio.actualizar(paciente);
        return paciente;
    }
    // borra el paciente si existe
    public void eliminarPaciente(long id) {
        Paciente paciente = buscarPorId(id);

        if (paciente == null) {
            System.out.println("Error: no existe un paciente con ese ID.");
            return;
        }

        repositorio.eliminar(id);
    }

    // control de datos: (throws)

    // Valida texto si el nombre o el apellido están vacíos o en null -> error
    private void validarNombreApellido(String nombre, String apellido) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("Nombre", "No puede estar vacío.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new DatoInvalidoException("Apellido", "No puede estar vacío.");
        }
    }

    // Valida número si el DNI es cero o negativo -> error
    private void validarDni(int dni) throws DatoInvalidoException {
        if (dni <= 0) {
            throw new DatoInvalidoException("DNI", "Debe ser mayor a cero.");
        }
    }
    // Valida correo si está vacío o no tiene el arroba -> error
    private void validarEmail(String email) throws DatoInvalidoException {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new DatoInvalidoException("Email", "Formato inválido (falta @).");
        }
    }

}
