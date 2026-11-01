package uiSwing;

import javax.swing.*;
import java.awt.*;

/** Tarjetas de resumen de la agenda semanal. */
public class PanelEstadistica extends JPanel {
    private final JLabel numTurnos;
    private final JLabel numPendientes;
    private final JLabel numConfirmados;
    private final JLabel numCompletados;
    private final JLabel numCancelados;

    public PanelEstadistica() {
        setBackground(new Color(248, 247, 244));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        numTurnos = crearTarjeta("Turnos (semana)", new Color(226, 232, 240));
        numPendientes = crearTarjeta("Pendientes", PaletaEstadoTurno.fondo(entity.EstadoTurno.PENDIENTE));
        numConfirmados = crearTarjeta("Confirmados", PaletaEstadoTurno.fondo(entity.EstadoTurno.CONFIRMADO));
        numCompletados = crearTarjeta("Completados", PaletaEstadoTurno.fondo(entity.EstadoTurno.COMPLETADO));
        numCancelados = crearTarjeta("Cancelados", PaletaEstadoTurno.fondo(entity.EstadoTurno.CANCELADO));
    }

    private JLabel crearTarjeta(String titulo, Color colorFondo) {
        TarjetaRedondeada tarjeta = new TarjetaRedondeada(colorFondo);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPreferredSize(new Dimension(160, 80));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel numero = new JLabel("0", SwingConstants.CENTER);
        numero.setFont(new Font("Segoe UI", Font.BOLD, 26));
        numero.setForeground(new Color(15, 23, 42));
        numero.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(numero);
        tarjeta.add(Box.createVerticalStrut(2));

        JLabel etiqueta = new JLabel(titulo, SwingConstants.CENTER);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etiqueta.setForeground(new Color(71, 85, 105));
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(etiqueta);
        add(tarjeta);
        return numero;
    }

    public void setTurnos(int n) { numTurnos.setText(String.valueOf(n)); }
    public void setPendientes(int n) { numPendientes.setText(String.valueOf(n)); }
    public void setConfirmados(int n) { numConfirmados.setText(String.valueOf(n)); }
    public void setCompletados(int n) { numCompletados.setText(String.valueOf(n)); }
    public void setCancelados(int n) { numCancelados.setText(String.valueOf(n)); }
}
