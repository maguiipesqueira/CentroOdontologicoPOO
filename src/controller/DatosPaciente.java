package controller;

import entity.Domicilio;

/** Datos de entrada para registrar o actualizar un paciente desde cualquier interfaz. */
public record DatosPaciente(
        String nombre,
        String apellido,
        int dni,
        String email,
        Domicilio domicilio,
        boolean tieneObraSocial) {

    public DatosPaciente {
        nombre = normalizar(nombre);
        apellido = normalizar(apellido);
        email = normalizar(email);
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
