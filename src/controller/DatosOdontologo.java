package controller;

import entity.Especialidad;
import entity.MotivoConsulta;

/** Datos de entrada para crear o actualizar un odontólogo sin construir subclases en la UI. */
public record DatosOdontologo(
        String nombre,
        String apellido,
        int matricula,
        Especialidad especialidad,
        MotivoConsulta subespecialidad) {

    public DatosOdontologo {
        nombre = normalizar(nombre);
        apellido = normalizar(apellido);
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
