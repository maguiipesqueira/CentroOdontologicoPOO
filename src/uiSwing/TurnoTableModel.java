package uiSwing;

import entity.Turno;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/** Modelo tipado de la tabla de turnos; conserva el objeto completo sin exponer una columna ID. */
public final class TurnoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
            "Paciente", "Odontólogo", "Tipo de consulta", "Fecha", "Hora", "Estado"
    };

    private List<Turno> turnos = List.of();

    public void setTurnos(List<Turno> nuevos) {
        turnos = nuevos == null ? List.of() : List.copyOf(nuevos);
        fireTableDataChanged();
    }

    public Turno getTurnoAt(int fila) {
        if (fila < 0 || fila >= turnos.size()) {
            throw new IndexOutOfBoundsException("Fila de turno fuera de rango: " + fila);
        }
        return turnos.get(fila);
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    @Override public int getRowCount() { return turnos.size(); }
    @Override public int getColumnCount() { return COLUMNAS.length; }
    @Override public String getColumnName(int columna) { return COLUMNAS[columna]; }
    @Override public boolean isCellEditable(int fila, int columna) { return false; }

    @Override
    public Object getValueAt(int fila, int columna) {
        Turno turno = getTurnoAt(fila);
        return switch (columna) {
            case 0 -> turno.getPaciente().getNombreCompleto();
            case 1 -> turno.getOdontologo().getNombreCompleto();
            case 2 -> TextoMotivo.de(turno);
            case 3 -> turno.getFecha().toString();
            case 4 -> turno.getHora().toString();
            case 5 -> turno.getEstado().name();
            default -> throw new IndexOutOfBoundsException("Columna inválida: " + columna);
        };
    }
}
