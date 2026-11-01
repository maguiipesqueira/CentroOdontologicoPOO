package uiSwing;

import entity.EstadoTurno;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Renderer de estado que consume la paleta compartida con la agenda. */
public class EstadoTurnoCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                   boolean conFoco, int fila, int columna) {
        JLabel celda = (JLabel) super.getTableCellRendererComponent(
                tabla, valor, seleccionado, conFoco, fila, columna);
        celda.setHorizontalAlignment(SwingConstants.CENTER);
        celda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        celda.setOpaque(true);

        EstadoTurno estado = null;
        if (valor instanceof EstadoTurno estadoTurno) {
            estado = estadoTurno;
        } else if (valor != null) {
            try {
                estado = EstadoTurno.valueOf(valor.toString());
            } catch (IllegalArgumentException ignorado) {
                // El valor desconocido utiliza el estilo neutro de la paleta.
            }
        }

        PaletaEstadoTurno.Colores colores = PaletaEstadoTurno.de(estado);
        celda.setBackground(seleccionado ? colores.fondo().darker() : colores.fondo());
        celda.setForeground(colores.texto());
        return celda;
    }
}
