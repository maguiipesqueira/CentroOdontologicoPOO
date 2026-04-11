package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Recepcionista {
    private List<Paciente> pacientes;
    private List<Odontologo> odontologos;
    private List<Turno> listaTurnos;

    // constructor de inicializacion
    public Recepcionista() {
        this.pacientes = new ArrayList<>();
        this.odontologos = new ArrayList<>();
        this.listaTurnos = new ArrayList<>();
    }
    // constructor con parametros
    public Recepcionista(List<Paciente> pacientes, List<Odontologo> odontologos, List<Turno> listaTurnos) {
        this.pacientes = pacientes;
        this.odontologos = odontologos;
        this.listaTurnos = listaTurnos;
    }

    // getters y setters
    public List<Paciente> getPacientes() { return pacientes; }
    public void setPacientes(List<Paciente> pacientes) { this.pacientes = pacientes; }

    public List<Odontologo> getOdontologos() { return odontologos; }
    public void setOdontologos(List<Odontologo> odontologos) { this.odontologos = odontologos; }

    public List<Turno> getListaTurnos() { return listaTurnos; }
    public void setListaTurnos(List<Turno> listaTurnos) { this.listaTurnos = listaTurnos; }

    // metodos
    // de pacientes
    public void altaPaciente(Paciente paciente) {
        this.pacientes.add(paciente);
    }
    public List<Paciente> listarPacientes() {
        return this.pacientes;
    }

    public Paciente buscarPorId(int id) {
        for (Paciente p : pacientes) {
            if (p.getId() == id) return p;
        }
        return null;
    }
    public Paciente buscarPorDni(int dni) {
        for (Paciente p : pacientes) {
            if (p.getDni() == dni) return p;
        }
        return null;
    }


    //de odontologos
    public void altaOdontologo(Odontologo odontologo) {
        if (odontologo.getMatricula() > 0) {
            this.odontologos.add(odontologo);
            System.out.println("El odontólogo " + odontologo.getNombre() + " fue dado de alta correctamente.");
        } else {
            System.out.println("ERROR: No se puede dar de alta al odontólogo " + odontologo.getNombre() + ". La matrícula " + odontologo.getMatricula() + " no es válida.");
        }
    }

    public Odontologo buscarPorMatricula(int matricula) {
        for (Odontologo o : odontologos) {
            if (o.getMatricula() == matricula) return o;
        }
        return null;
    }

    // de turnos
    public void registrarTurno(Paciente paciente, Odontologo odontologo, LocalDate fecha, LocalTime hora) {
        Turno nuevo = new Turno(listaTurnos.size() + 1, paciente, odontologo, fecha, hora);
        nuevo.setEstado(EstadoTurno.PENDIENTE);
        this.listaTurnos.add(nuevo);
    }

    public List<Turno> listarTurnos() {
        return this.listaTurnos;
    }

    public void cancelarTurno(int id) {
        for (Turno t : listaTurnos) {
            if (t.getId() == id) {
                t.setEstado(EstadoTurno.CANCELADO);
            }
        }
    }

    @Override
    public String toString() {
        return "Recepcionista{" +
                "pacientes=" + pacientes.size() +
                ", odontologos=" + odontologos.size() +
                ", turnos=" + listaTurnos.size() +
                '}';
    }
}