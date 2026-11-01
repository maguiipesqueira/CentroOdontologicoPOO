package uiSwing;

import entity.Factura;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Modelo tipado de facturas emitidas; permite recuperar la factura aunque la tabla se ordene. */
public final class FacturaTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {"N°", "Fecha", "Paciente", "Tratamiento", "Medio de pago", "Total"};
    private final List<Factura> facturas = new ArrayList<>();
    private Map<String, String> nombresMedios = Map.of();

    public void setFacturas(List<Factura> nuevas, Map<String, String> nombresMedios) {
        facturas.clear();
        facturas.addAll(Objects.requireNonNull(nuevas, "La lista de facturas es obligatoria."));
        this.nombresMedios = Map.copyOf(Objects.requireNonNull(nombresMedios, "Los nombres de medios son obligatorios."));
        fireTableDataChanged();
    }

    public Factura getFacturaAt(int filaModelo) { return facturas.get(filaModelo); }
    @Override public int getRowCount() { return facturas.size(); }
    @Override public int getColumnCount() { return COLUMNAS.length; }
    @Override public String getColumnName(int columna) { return COLUMNAS[columna]; }
    @Override public boolean isCellEditable(int fila, int columna) { return false; }

    @Override
    public Object getValueAt(int fila, int columna) {
        Factura factura = facturas.get(fila);
        return switch (columna) {
            case 0 -> factura.getId();
            case 1 -> factura.getFecha().toString();
            case 2 -> factura.getPacienteNombre();
            case 3 -> factura.getConcepto();
            case 4 -> nombresMedios.getOrDefault(factura.getMetodoPagoCodigo(), factura.getMetodoPagoCodigo());
            case 5 -> FormatoPlata.formatear(factura.getMontoTotalDecimal());
            default -> throw new IndexOutOfBoundsException("Columna inválida: " + columna);
        };
    }
}
