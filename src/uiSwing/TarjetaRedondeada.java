package uiSwing;

import javax.swing.*;
import java.awt.*;

/** Panel reutilizable con fondo y esquinas redondeadas. */
public class TarjetaRedondeada extends JPanel {

    private final int radio;

    public TarjetaRedondeada(Color fondo) {
        this(fondo, 25);
    }

    public TarjetaRedondeada(Color fondo, int radio) {
        if (fondo == null) throw new IllegalArgumentException("El color de fondo es obligatorio.");
        if (radio < 0) throw new IllegalArgumentException("El radio no puede ser negativo.");
        this.radio = radio;
        setBackground(fondo);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
        } finally {
            g2.dispose();
        }
    }
}
