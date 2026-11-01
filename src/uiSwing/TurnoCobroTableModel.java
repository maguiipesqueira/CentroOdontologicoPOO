package uiSwing;

import entity.DesgloseMonto;
import entity.Turno;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Modelo tipado de turnos pendientes de cobro; conserva el objeto real, no un ID oculto. */
public final class TurnoCobroTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {"Turno", "Paciente", "Tratamiento", "Precio", "Descuento", "Total"};
    private final List<Fila> filas = new ArrayList<>();

    public void limpiar() {
        int cantidad = filas.size();
        filas.clear();
        if (cantidad > 0) fireTableRowsDeleted(0, cantidad - 1);
    }

    public void agregar(Turno turno, DesgloseMonto desglose, String error) {
        filas.add(new Fila(Objects.requireNonNull(turno, "El turno es obligatorio."), desglose, error));
        fireTableRowsInserted(filas.size() - 1, filas.size() - 1);
    }

    public Turno getTurnoAt(int filaModelo) { return filas.get(filaModelo).turno(); }
    public String getErrorAt(int filaModelo) { return filas.get(filaModelo).error(); }

    @Override public int getRowCount() { return filas.size(); }
    @Override public int getColumnCount() { return COLUMNAS.length; }
    @Override public String getColumnName(int columna) { return COLUMNAS[columna]; }
    @Override public boolean isCellEditable(int fila, int columna) { return false; }

    @Override
    public Object getValueAt(int fila, int columna) {
        Fila dato = filas.get(fila);
        Turno turno = dato.turno();
        if (columna == 0) return turno.getFecha() + " " + turno.getHora();
        if (columna == 1) return turno.getPaciente().getNombreCompleto();
        if (columna == 2) return TextoMotivo.de(turno);
        if (dato.desglose() == null) {
            return switch (columna) {
                case 3 -> "Sin precio";
                case 4, 5 -> "-";
                default -> "";
            };
        }
        return switch (columna) {
            case 3 -> FormatoPlata.formatear(dato.desglose().getMontoBaseDecimal());
            case 4 -> FormatoPlata.formatear(dato.desglose().getDescuentoObraSocialDecimal());
            case 5 -> FormatoPlata.formatear(dato.desglose().getMontoTotalDecimal());
            default -> "";
        };
    }

    private record Fila(Turno turno, DesgloseMonto desglose, String error) {}
}
