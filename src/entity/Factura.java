package entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Comprobante de cobro de un turno. Una vez emitida no se modifica, por eso solo tiene getters.
 * Se arma con el Builder:
 * <pre>
 * Factura.builder().id(1).turnoId(5).pacienteId(2).pacienteNombre("Ana Perez")
 *        .concepto("LIMPIEZA").fecha(hoy).metodoPago("Efectivo").desglose(desglose).build();
 * </pre>
 * Decision de diseno: el nombre del paciente, el concepto y el medio de pago se guardan como texto (copia de
 * lo que valian al cobrar) y no como referencias, para que el comprobante no cambie si despues se modifica
 * o se borra el paciente, el tratamiento o el medio de pago.
 */
public class Factura {

    private final long id;                // numero de factura
    private final long turnoId;           // turno cobrado (un turno tiene una sola factura)
    private final long pacienteId;        // paciente que pago
    private final String pacienteNombre;  // copia del nombre al momento del cobro
    private final String concepto;        // tratamiento cobrado (nombre tecnico del motivo), tambien copiado
    private final LocalDate fecha;        // fecha de pago, se usa en el cierre del dia
    private final String metodoPago;      // identifica el medio con que se pago (hoy: su nombre, ej "Efectivo")
    private final DesgloseMonto desglose; // precio de lista, descuentos y total

    private Factura(Builder b) {
        this.id = b.id;
        this.turnoId = b.turnoId;
        this.pacienteId = b.pacienteId;
        this.pacienteNombre = Objects.requireNonNull(b.pacienteNombre, "Falta el nombre del paciente");
        this.concepto = Objects.requireNonNull(b.concepto, "Falta el concepto");
        this.fecha = Objects.requireNonNull(b.fecha, "Falta la fecha");
        this.metodoPago = Objects.requireNonNull(b.metodoPago, "Falta el medio de pago");
        this.desglose = Objects.requireNonNull(b.desglose, "Falta el desglose de montos");
    }

    // empieza a armar una factura
    public static Builder builder() {
        return new Builder();
    }

    // devuelve el numero de factura
    public long getId() { return id; }
    // devuelve el id del turno cobrado
    public long getTurnoId() { return turnoId; }
    // devuelve el id del paciente
    public long getPacienteId() { return pacienteId; }
    // devuelve el nombre del paciente
    public String getPacienteNombre() { return pacienteNombre; }
    // devuelve el tratamiento cobrado
    public String getConcepto() { return concepto; }
    // devuelve la fecha de pago
    public LocalDate getFecha() { return fecha; }
    // devuelve el medio de pago con que se cobro
    public String getMetodoPago() { return metodoPago; }
    // devuelve el desglose completo de los importes
    public DesgloseMonto getDesglose() { return desglose; }

    // atajos al desglose (asi quien muestra la factura no tiene que encadenar llamadas)
    public double getMontoBase() { return desglose.getMontoBase(); }
    public double getDescuentoObraSocial() { return desglose.getDescuentoObraSocial(); }
    public double getDescuentoEfectivo() { return desglose.getDescuentoEfectivo(); }
    public double getMontoTotal() { return desglose.getMontoTotal(); }

    // texto con los datos de la factura
    @Override
    public String toString() {
        return "FACTURA #" + id +
                " | Turno: " + turnoId +
                " | Paciente: " + pacienteNombre +
                " | Concepto: " + concepto +
                " | Fecha: " + fecha +
                " | Medio: " + metodoPago +
                " | Base: $" + getMontoBase() +
                " | Desc. obra social: $" + getDescuentoObraSocial() +
                " | Desc. efectivo: $" + getDescuentoEfectivo() +
                " | Total: $" + getMontoTotal();
    }

    // arma una Factura campo por campo, con el nombre de cada dato a la vista (no hay que acordarse del orden)
    public static class Builder {

        private long id;
        private long turnoId;
        private long pacienteId;
        private String pacienteNombre;
        private String concepto;
        private LocalDate fecha;
        private String metodoPago;
        private DesgloseMonto desglose;

        private Builder() {}

        public Builder id(long id) { this.id = id; return this; }
        public Builder turnoId(long turnoId) { this.turnoId = turnoId; return this; }
        public Builder pacienteId(long pacienteId) { this.pacienteId = pacienteId; return this; }
        public Builder pacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; return this; }
        public Builder concepto(String concepto) { this.concepto = concepto; return this; }
        public Builder fecha(LocalDate fecha) { this.fecha = fecha; return this; }
        public Builder metodoPago(String metodoPago) { this.metodoPago = metodoPago; return this; }
        public Builder desglose(DesgloseMonto desglose) { this.desglose = desglose; return this; }

        // crea la factura; si falta algun dato obligatorio lanza NullPointerException indicando cual
        public Factura build() {
            return new Factura(this);
        }
    }
}