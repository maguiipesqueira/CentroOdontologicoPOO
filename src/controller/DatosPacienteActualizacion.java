package controller;

import entity.Domicilio;

/** Datos editables de un paciente; el DNI se conserva y no forma parte de la edición. */
public record DatosPacienteActualizacion(
        String nombre,
        String apellido,
        String email,
        Domicilio domicilio,
        boolean tieneObraSocial) {

    public DatosPacienteActualizacion {
        nombre = normalizar(nombre);
        apellido = normalizar(apellido);
        email = normalizar(email);
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
