package entity;

import java.time.LocalDate;

// Comprobante de cobro de un turno. Una vez emitida no se modifica, por eso solo tiene getters
public class Factura {

    private final long id;                    // numero de factura
    private final long turnoId;               // turno cobrado (un turno tiene una sola factura)
    private final long pacienteId;            // paciente que pago
    private final String pacienteNombre;      // se guarda como texto para que el comprobante no dependa del paciente
    private final String concepto;            // tratamiento cobrado, tambien como texto
    private final LocalDate fecha;            // fecha de pago, se usa en el cierre del dia
    private final String metodoPago;          // nombre del medio de pago (ej "Efectivo")
    private final double montoBase;           // precio de lista del tratamiento
    private final double descuentoObraSocial; // importe descontado por obra social
    private final double descuentoEfectivo;   // importe descontado por pagar en efectivo
    private final double montoTotal;          // total efectivamente pagado

    // constructor con todos los datos de la factura
    public Factura(long id, long turnoId, long pacienteId, String pacienteNombre, String concepto,
                   LocalDate fecha, String metodoPago, double montoBase,
                   double descuentoObraSocial, double descuentoEfectivo, double montoTotal) {
        this.id = id;
        this.turnoId = turnoId;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.concepto = concepto;
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.montoBase = montoBase;
        this.descuentoObraSocial = descuentoObraSocial;
        this.descuentoEfectivo = descuentoEfectivo;
        this.montoTotal = montoTotal;
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
    // devuelve el nombre del medio de pago
    public String getMetodoPago() { return metodoPago; }
    // devuelve el precio de lista
    public double getMontoBase() { return montoBase; }
    // devuelve lo descontado por obra social
    public double getDescuentoObraSocial() { return descuentoObraSocial; }
    // devuelve lo descontado por pagar en efectivo
    public double getDescuentoEfectivo() { return descuentoEfectivo; }
    // devuelve el total pagado
    public double getMontoTotal() { return montoTotal; }

    // texto con los datos de la factura
    @Override
    public String toString() {
        return "FACTURA #" + id +
                " | Turno: " + turnoId +
                " | Paciente: " + pacienteNombre +
                " | Concepto: " + concepto +
                " | Fecha: " + fecha +
                " | Medio: " + metodoPago +
                " | Base: $" + montoBase +
                " | Desc. obra social: $" + descuentoObraSocial +
                " | Desc. efectivo: $" + descuentoEfectivo +
                " | Total: $" + montoTotal;
    }
}