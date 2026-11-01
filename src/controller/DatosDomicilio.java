package controller;

/** Datos simples de domicilio recibidos desde una interfaz, sin exponer entidades en la UI. */
public record DatosDomicilio(
        String calle,
        int numero,
        String localidad,
        String provincia,
        entity.TipoHogar tipoHogar) {

    public DatosDomicilio {
        calle = normalizar(calle);
        localidad = normalizar(localidad);
        provincia = normalizar(provincia);
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
