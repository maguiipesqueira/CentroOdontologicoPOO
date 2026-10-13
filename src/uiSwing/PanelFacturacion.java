package uiSwing;

import controller.ControladorFactura;

import javax.swing.*;
import java.awt.*;

// Pantalla de facturacion. Por ahora solo muestra el titulo; las tablas y el cobro se agregan en las etapas siguientes
public class PanelFacturacion extends JPanel {

    private final ControladorFactura controlador;

    public PanelFacturacion(ControladorFactura controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 247, 244));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel titulo = new JLabel("Facturación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(new Color(15, 23, 42));

        add(titulo, BorderLayout.NORTH);
    }
}