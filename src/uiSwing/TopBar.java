package uiSwing;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {

    private JLabel lblTitulo;
    private JButton btnNuevo;

    public TopBar() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 60)); // Un poco más alta
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // título a la izquierda
        lblTitulo = new JLabel("  Agenda semanal");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTitulo.setForeground(new Color(15, 23, 42));

        // botón a la derecha (MEJORADO)
        btnNuevo = new JButton("+ Nuevo turno");
        btnNuevo.setBackground(new Color(14, 165, 233));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Letra más grande y negrita
        btnNuevo.setPreferredSize(new Dimension(160, 40)); // Tamaño explícito, más grande
        btnNuevo.setBorderPainted(false);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // panel derecho con GridBagLayout para centrar el botón verticalmente de forma perfecta
        JPanel derecha = new JPanel(new GridBagLayout());
        derecha.setBackground(Color.WHITE);
        derecha.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // Margen derecho extra
        derecha.add(btnNuevo);

        add(lblTitulo, BorderLayout.WEST);
        add(derecha, BorderLayout.EAST);
    }

    public void setTitulo(String titulo) {
        lblTitulo.setText("  " + titulo);
    }

    public JButton getBtnNuevo() { return btnNuevo; }
}

