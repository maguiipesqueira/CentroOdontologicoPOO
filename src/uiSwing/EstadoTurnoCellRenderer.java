package uiSwing;

import entity.EstadoTurno;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// pinta la celda de estado con un color segun el valor (igual estetica que los bloques de PanelAgenda)
public class EstadoTurnoCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                   boolean conFoco, int fila, int columna) {

        JLabel celda = (JLabel) super.getTableCellRendererComponent(
                tabla, valor, seleccionado, conFoco, fila, columna);

        celda.setHorizontalAlignment(SwingConstants.CENTER);
        celda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        celda.setOpaque(true);

        String estado = valor != null ? valor.toString() : "";

        if (estado.equals(EstadoTurno.PENDIENTE.name())) {
            celda.setBackground(new Color(254, 243, 199));
            celda.setForeground(new Color(146, 64, 14));
        } else if (estado.equals(EstadoTurno.CONFIRMADO.name())) {
            celda.setBackground(new Color(224, 242, 254));
            celda.setForeground(new Color(12, 74, 110));
        } else if (estado.equals(EstadoTurno.COMPLETADO.name())) {
            celda.setBackground(new Color(209, 250, 229));
            celda.setForeground(new Color(6, 78, 59));
        } else if (estado.equals(EstadoTurno.CANCELADO.name())) {
            celda.setBackground(new Color(255, 228, 230));
            celda.setForeground(new Color(136, 19, 55));
        } else if (estado.equals(EstadoTurno.URGENTE.name())) {
            celda.setBackground(new Color(237, 233, 254));
            celda.setForeground(new Color(91, 33, 182));
        } else {
            celda.setBackground(Color.WHITE);
            celda.setForeground(new Color(15, 23, 42));
        }

        // si la fila esta seleccionada, oscurecemos un poco el fondo para que se note
        if (seleccionado) {
            celda.setBackground(celda.getBackground().darker());
        }

        return celda;
    }
}