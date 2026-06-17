package uiSwing;

import javax.swing.*;
import java.awt.*;

public class PanelAgenda extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private static final String[] DIAS = {"", "Lun 16", "Mar 17", "Mie 18", "Jue 19", "Vie 20"};
    private static final String[] HORAS = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00"};

    public PanelAgenda() {
        setLayout(new BorderLayout());
        setBackground(FONDO);

        JPanel grilla = new JPanel(new GridLayout(HORAS.length + 1, DIAS.length));
        grilla.setBackground(Color.WHITE);
        grilla.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        // primera fila: encabezados de días
        for (String dia : DIAS) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(100, 116, 139));
            lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));
            lbl.setOpaque(true);
            lbl.setBackground(new Color(248, 250, 252));
            grilla.add(lbl);
        }

        // filas de horas
        for (String hora : HORAS) {
            // columna de hora
            JLabel lblHora = new JLabel(hora, SwingConstants.CENTER);
            lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblHora.setForeground(new Color(148, 163, 184));
            lblHora.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));
            lblHora.setOpaque(true);
            lblHora.setBackground(new Color(248, 250, 252));
            grilla.add(lblHora);

            // 5 celdas por fila (una por día)
            for (int dia = 0; dia < 5; dia++) {
                JPanel celda = new JPanel(new BorderLayout());
                celda.setBackground(Color.WHITE);
                celda.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));

                // turnos hardcodeados de prueba
                if (hora.equals("09:00") && dia == 0) celda.add(crearBloque("F. Persi", "Limpieza", new Color(224, 242, 254), new Color(14, 165, 233)));
                if (hora.equals("11:00") && dia == 0) celda.add(crearBloque("T. Barea", "Control", new Color(237, 233, 254), new Color(124, 58, 237)));
                if (hora.equals("09:00") && dia == 1) celda.add(crearBloque("M. Garcia", "Ortod.", new Color(209, 250, 229), new Color(5, 150, 105)));
                if (hora.equals("10:00") && dia == 2) celda.add(crearBloque("F. Persi", "Blanq.", new Color(224, 242, 254), new Color(14, 165, 233)));
                if (hora.equals("09:00") && dia == 3) celda.add(crearBloque("T. Barea", "Control", new Color(237, 233, 254), new Color(124, 58, 237)));
                if (hora.equals("08:00") && dia == 4) celda.add(crearBloque("N. Torres", "Ortod.", new Color(254, 243, 199), new Color(245, 158, 11)));

                grilla.add(celda);
            }
        }

        add(grilla, BorderLayout.CENTER);
    }

    // crea el bloquecito de color dentro de la celda
    private JPanel crearBloque(String nombre, String tipo, Color fondo, Color borde) {
        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setBackground(fondo);
        bloque.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, borde));

        JLabel lblNombre = new JLabel(" " + nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNombre.setForeground(new Color(15, 23, 42));

        JLabel lblTipo = new JLabel(" " + tipo);
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTipo.setForeground(new Color(71, 85, 105));

        bloque.add(lblNombre);
        bloque.add(lblTipo);

        return bloque;
    }
}

