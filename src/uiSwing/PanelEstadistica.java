package uiSwing;
import javax.swing.*;
import java.awt.*;

public class PanelEstadistica extends JPanel {
    private JLabel numTurnos;
    private JLabel numPendientes;
    private JLabel numConfirmados;
    private JLabel numCompletados;
    private JLabel numCancelados;

    public PanelEstadistica() {
        setBackground(new Color(248, 247, 244));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        numTurnos      = crearTarjeta("Turnos (semana)",   "0", new Color(226, 232, 240));
        numPendientes  = crearTarjeta("Pendientes",    "0", new Color(254, 243, 199));
        numConfirmados = crearTarjeta("Confirmados",   "0", new Color(224, 242, 254));
        numCompletados = crearTarjeta("Completados",   "0", new Color(209, 250, 229));
        numCancelados  = crearTarjeta("Cancelados",    "0", new Color(255, 228, 230));
    }

    private JLabel crearTarjeta(String titulo, String numero, Color colorFondo) {
        // SOBREESCRIBIMOS el panel para poder dibujarle bordes redondeados
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Suaviza los bordes para que no se vean pixelados
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Dibuja un rectángulo con las esquinas redondeadas (radio de 25)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };

        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(colorFondo); // Ahora el fondo completo toma el color
        tarjeta.setOpaque(false); // Necesario para que las esquinas redondeadas sean transparentes
        tarjeta.setPreferredSize(new Dimension(160, 80));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Margen interno para centrar los textos

        // Quitamos el cuadradito de color de arriba porque ahora todo el fondo es de color

        // número grande
        JLabel lblNumero = new JLabel(numero, SwingConstants.CENTER);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Un poquito más grande
        lblNumero.setForeground(new Color(15, 23, 42));
        lblNumero.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblNumero);

        tarjeta.add(Box.createVerticalStrut(2)); // Pequeña separación

        // texto abajo
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(new Color(71, 85, 105)); // Un gris un poco más oscuro para que lea bien
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblTitulo);

        add(tarjeta);
        return lblNumero;
    }

    // estos métodos los va a usar el panel de agenda para actualizar los números
    public void setTurnos(int n)       { numTurnos.setText(String.valueOf(n)); }
    public void setPendientes(int n)   { numPendientes.setText(String.valueOf(n)); }
    public void setConfirmados(int n)  { numConfirmados.setText(String.valueOf(n)); }
    public void setCompletados(int n)  { numCompletados.setText(String.valueOf(n)); }
    public void setCancelados(int n)   { numCancelados.setText(String.valueOf(n)); }
}