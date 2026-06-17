package uiSwing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Sidebar extends JPanel {

    private static final Color FONDO = new Color(15, 23, 42);
    private static final Color ACTIVO = new Color(14, 165, 233);
    private static final Color INACTIVO = new Color(71, 85, 105);

    private JLabel btnAgenda;
    private JLabel btnPacientes;
    private JLabel btnOdontologos;

    public Sidebar() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(80, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(20));

        btnAgenda      = crearBoton("AG");
        btnPacientes   = crearBoton("PAC");
        btnOdontologos = crearBoton("OD");

        add(btnAgenda);
        add(Box.createVerticalStrut(8));
        add(btnPacientes);
        add(Box.createVerticalStrut(8));
        add(btnOdontologos);

        marcarActivo(btnAgenda);
    }

    private JLabel crearBoton(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(INACTIVO);
        lbl.setOpaque(true);
        lbl.setBackground(FONDO);
        lbl.setPreferredSize(new Dimension(60, 44));
        lbl.setMaximumSize(new Dimension(60, 44));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return lbl;
    }

    public void marcarActivo(JLabel label) {
        for (JLabel l : new JLabel[]{btnAgenda, btnPacientes, btnOdontologos}) {
            l.setBackground(FONDO);
            l.setForeground(INACTIVO);
        }
        label.setBackground(new Color(30, 58, 95));
        label.setForeground(ACTIVO);
    }

    public JLabel getBtnAgenda()      { return btnAgenda; }
    public JLabel getBtnPacientes()   { return btnPacientes; }
    public JLabel getBtnOdontologos() { return btnOdontologos; }
}
