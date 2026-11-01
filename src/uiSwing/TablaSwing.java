package uiSwing;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Font;

/** Estilos y configuraciones comunes para las tablas de Swing. */
public final class TablaSwing {

    private static final Color FONDO_CABECERA = new Color(248, 250, 252);
    private static final Color TEXTO_CABECERA = new Color(71, 85, 105);
    private static final Color LINEA = new Color(226, 232, 240);

    private TablaSwing() {}

    /** Aplica la presentación y el comportamiento común de las tablas de la aplicación. */
    public static void configurar(JTable tabla) {
        if (tabla == null) throw new IllegalArgumentException("La tabla es obligatoria.");
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setGridColor(LINEA);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(FONDO_CABECERA);
        tabla.getTableHeader().setForeground(TEXTO_CABECERA);
        tabla.setAutoCreateRowSorter(true);
    }

    /** Alinea a la derecha las columnas indicadas. */
    public static void alinearDerecha(JTable tabla, int... columnas) {
        if (tabla == null || columnas == null) throw new IllegalArgumentException("Tabla y columnas son obligatorias.");
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int columna : columnas) {
            if (columna < 0 || columna >= tabla.getColumnModel().getColumnCount()) {
                throw new IllegalArgumentException("Índice de columna fuera de rango: " + columna);
            }
            tabla.getColumnModel().getColumn(columna).setCellRenderer(renderer);
        }
    }
}
