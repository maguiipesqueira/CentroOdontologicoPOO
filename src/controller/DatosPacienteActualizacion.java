package controller;

/** Datos editables de un paciente; el DNI se conserva y no forma parte de la edición. */
public record DatosPacienteActualizacion(
        String nombre,
        String apellido,
        String email,
        DatosDomicilio domicilio,
        boolean tieneObraSocial,
        boolean reemplazarDomicilio) {

    /** Compatibilidad: si no se indica el domicilio, se conserva el que ya tiene el paciente. */
    public DatosPacienteActualizacion(String nombre, String apellido, String email,
                                      DatosDomicilio domicilio, boolean tieneObraSocial) {
        this(nombre, apellido, email, domicilio, tieneObraSocial, false);
    }

    public DatosPacienteActualizacion {
        nombre = normalizar(nombre);
        apellido = normalizar(apellido);
        email = normalizar(email);
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
