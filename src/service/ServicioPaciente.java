package service;

import entity.Domicilio;
import entity.Paciente;
import repository.RepositorioPaciente;

import java.time.LocalDate;
import java.util.List;

public class ServicioPaciente {

    private final RepositorioPaciente repositorio;
    private long contadorId = 1L;

    // guarda el repositorio para usarlo después
    public ServicioPaciente(RepositorioPaciente repositorio) {
        this.repositorio = repositorio;
    }

    // da de alta un paciente si los datos están bien y el dni no existe
    public Paciente registrarPaciente(String nombre, String apellido, int dni, String email,
                                      Domicilio domicilio) {

        if (!validarNombreApellido(nombre, apellido)) return null;
        if (!validarDni(dni)) return null;
        if (!validarEmail(email)) return null;

        if (repositorio.existeDni(dni)) {
            System.out.println("Error: ya existe un paciente registrado con ese DNI.");
            return null;
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

    // cambia datos del paciente y si mandás domicilio distinto de null también lo cambia
    public Paciente actualizarPaciente(long id, String nombre, String apellido,
                                       String email, Domicilio domicilio) {

        Paciente paciente = buscarPorId(id);

        if (paciente == null) {
            System.out.println("Error: no existe un paciente con ese ID.");
            return null;
        }

        if (!validarNombreApellido(nombre, apellido)) return null;
        if (!validarEmail(email)) return null;

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

    // mira que nombre y apellido no vengan vacíos
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

    // el dni tiene que ser mayor a 0
    private boolean validarDni(int dni) {
        if (dni <= 0) {
            System.out.println("Error: el DNI debe ser un número positivo.");
            return false;
        }

        return true;
    }

    // chequeo básico de mail que tenga arroba
    private boolean validarEmail(String email) {
        if (email == null || !email.contains("@")) {
            System.out.println("Error: el email no es válido.");
            return false;
        }

        return true;
    }
}
