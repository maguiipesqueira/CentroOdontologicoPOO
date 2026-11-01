package uiSwing;

/** Secciones disponibles en la ventana principal y sus metadatos de navegación. */
public enum SeccionContenido {
    AGENDA("AGENDA", "Agenda semanal", "AG"),
    PACIENTES("PACIENTES", "Pacientes", "PAC"),
    ODONTOLOGOS("ODONTOLOGOS", "Odontólogos", "OD"),
    TURNOS("TURNOS", "Turnos", "TUR"),
    FACTURACION("FACTURACION", "Facturación", "FAC");

    private final String claveTarjeta;
    private final String titulo;
    private final String etiquetaSidebar;

    SeccionContenido(String claveTarjeta, String titulo, String etiquetaSidebar) {
        this.claveTarjeta = claveTarjeta;
        this.titulo = titulo;
        this.etiquetaSidebar = etiquetaSidebar;
    }

    public String claveTarjeta() { return claveTarjeta; }
    public String titulo() { return titulo; }
    public String etiquetaSidebar() { return etiquetaSidebar; }
}
