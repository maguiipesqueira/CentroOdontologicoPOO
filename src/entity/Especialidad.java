package entity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Especialidades que puede tener un odontologo. Reune en un solo lugar lo que cambia de una a otra:
 * el nombre con el que se guarda en los archivos, los motivos de consulta que atiende, el motivo por
 * defecto y como se crea el odontologo. Agregar una especialidad nueva es agregar una constante aca
 * (mas su clase), sin tocar la persistencia ni las pantallas.
 */
public enum Especialidad {

    GENERAL("General", TipoConsulta.values(), TipoConsulta.LIMPIEZA,
            (id, nombre, apellido, matricula, sub) -> new General(id, nombre, apellido, matricula, (TipoConsulta) sub)),

    CIRUJANO("Cirujano", TipoCirugia.values(), TipoCirugia.EXTRACCION_MUELA,
            (id, nombre, apellido, matricula, sub) -> new Cirujano(id, nombre, apellido, matricula, (TipoCirugia) sub)),

    ORTODONCISTA("Ortodoncista", TipoOrtodoncia.values(), TipoOrtodoncia.BRACKETS_METALICOS,
            (id, nombre, apellido, matricula, sub) -> new Ortodoncista(id, nombre, apellido, matricula, (TipoOrtodoncia) sub));

    @FunctionalInterface
    private interface Creador {
        Odontologo crear(long id, String nombre, String apellido, int matricula, MotivoConsulta subespecialidad);
    }

    private final String nombre;
    private final List<MotivoConsulta> motivosPosibles;
    private final MotivoConsulta subespecialidadPorDefecto;
    private final Creador creador;

    Especialidad(String nombre, MotivoConsulta[] motivosPosibles, MotivoConsulta subespecialidadPorDefecto, Creador creador) {
        this.nombre = nombre;
        this.motivosPosibles = List.of(motivosPosibles);
        this.subespecialidadPorDefecto = subespecialidadPorDefecto;
        this.creador = creador;
    }

    // nombre estable: es el que se guarda en odontologos.txt, no hay que cambiarlo
    public String getNombre() { return nombre; }

    // motivos de consulta que atiende esta especialidad (lista que no se puede modificar)
    public List<MotivoConsulta> getMotivosPosibles() { return motivosPosibles; }

    public MotivoConsulta getSubespecialidadPorDefecto() { return subespecialidadPorDefecto; }

    // crea el odontologo de esta especialidad; la subespecialidad tiene que ser uno de sus motivos posibles
    public Odontologo crear(long id, String nombre, String apellido, int matricula, MotivoConsulta subespecialidad) {
        Objects.requireNonNull(subespecialidad, "La subespecialidad no puede ser nula");
        if (!motivosPosibles.contains(subespecialidad)) {
            throw new IllegalArgumentException(
                    "'" + subespecialidad.name() + "' no es un motivo de la especialidad " + this.nombre);
        }
        return creador.crear(id, nombre, apellido, matricula, subespecialidad);
    }

    // igual que crear, usando la subespecialidad por defecto
    public Odontologo crearConSubespecialidadPorDefecto(long id, String nombre, String apellido, int matricula) {
        return crear(id, nombre, apellido, matricula, subespecialidadPorDefecto);
    }

    // busca uno de los motivos de esta especialidad por su nombre tecnico (ej. "IMPLANTE")
    public MotivoConsulta subespecialidadPorNombre(String nombreTecnico) {
        return motivosPosibles.stream()
                .filter(m -> m.name().equals(nombreTecnico))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'" + nombreTecnico + "' no es un motivo de la especialidad " + nombre));
    }

    // busca por el nombre guardado en los archivos (distingue mayusculas, como antes)
    public static Optional<Especialidad> desdeNombre(String nombre) {
        for (Especialidad e : values()) {
            if (e.nombre.equals(nombre)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    // el combo de la UI muestra el nombre
    @Override
    public String toString() { return nombre; }
}