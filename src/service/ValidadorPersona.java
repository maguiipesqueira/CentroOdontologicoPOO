package service;

import exception.DatoInvalidoException;

/** Reglas de validación compartidas por los servicios de personas. */
final class ValidadorPersona {

    private ValidadorPersona() {
    }

    static void validarNombreApellido(String nombre, String apellido) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("Nombre", "El valor del campo es requerido.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new DatoInvalidoException("Apellido", "El valor del campo es requerido.");
        }
    }
}
