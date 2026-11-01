package uiSwing;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Navegación lateral construida a partir de las secciones disponibles. */
public class Sidebar extends JPanel {

    private static final Color FONDO = new Color(15, 23, 42);
    private static final Color ACTIVO = new Color(14, 165, 233);
    private static final Color INACTIVO = new Color(71, 85, 105);
    private static final Color FONDO_ACTIVO = new Color(30, 58, 95);
    private static final List<SeccionContenido> OPCIONES = List.of(SeccionContenido.values());

    private final Map<SeccionContenido, JLabel> botones = new EnumMap<>(SeccionContenido.class);
    private SeccionContenido seleccionada = SeccionContenido.AGENDA;

    public Sidebar() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(80, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(20));

        for (SeccionContenido seccion : OPCIONES) {
            JLabel boton = crearBoton(seccion.etiquetaSidebar());
            botones.put(seccion, boton);
            boton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    seleccionar(seccion);
                }
            });
            add(boton);
            add(Box.createVerticalStrut(8));
        }
        marcarActivo(seleccionada);
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

    private void seleccionar(SeccionContenido seccion) {
        SeccionContenido anterior = seleccionada;
        seleccionada = seccion;
        marcarActivo(seccion);
        firePropertyChange("seccionSeleccionada", anterior, seccion);
    }

    public void marcarActivo(SeccionContenido seccion) {
        botones.forEach((opcion, label) -> {
            boolean activo = opcion == seccion;
            label.setBackground(activo ? FONDO_ACTIVO : FONDO);
            label.setForeground(activo ? ACTIVO : INACTIVO);
        });
        seleccionada = seccion;
    }

    public JLabel getBoton(SeccionContenido seccion) { return botones.get(seccion); }
    public SeccionContenido getSeleccionada() { return seleccionada; }
    public List<SeccionContenido> getOpciones() { return OPCIONES; }
}
