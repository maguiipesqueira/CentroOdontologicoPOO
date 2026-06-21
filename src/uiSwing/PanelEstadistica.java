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
        // panel de la tarjeta
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        tarjeta.setPreferredSize(new Dimension(160, 80));
        tarjeta.add(Box.createVerticalStrut(10));

        // cuadradito de color arriba
        JLabel iconoColor = new JLabel("  ");
        iconoColor.setOpaque(true);
        iconoColor.setBackground(colorFondo);
        iconoColor.setPreferredSize(new Dimension(28, 28));
        iconoColor.setMaximumSize(new Dimension(28, 28));
        iconoColor.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(iconoColor);
        tarjeta.add(Box.createVerticalStrut(6));

        // número grande
        JLabel lblNumero = new JLabel(numero, SwingConstants.CENTER);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNumero.setForeground(new Color(15, 23, 42));
        lblNumero.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblNumero);

        // texto abajo
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setForeground(new Color(148, 163, 184));
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