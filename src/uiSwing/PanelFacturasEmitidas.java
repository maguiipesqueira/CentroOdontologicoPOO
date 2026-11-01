package uiSwing;

import controller.ControladorFactura;
import entity.Factura;
import exception.ClinicaException;
import pago.MedioPago;
import service.CierreDelDia;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Consulta de facturas emitidas, comprobantes y cierre diario. */
public class PanelFacturasEmitidas extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private final ControladorFactura controlador;
    private final FacturaTableModel modeloTabla = new FacturaTableModel();
    private JTable tabla;
    private JTextField txtFechaCierre;
    private JLabel lblEstado;

    public PanelFacturasEmitidas(ControladorFactura controlador) {
        if (controlador == null) throw new IllegalArgumentException("El controlador de facturación es obligatorio.");
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        add(crearPanelCierre(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
        refrescar();
    }

    private JPanel crearPanelCierre() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        JLabel titulo = new JLabel("Facturas emitidas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(15, 23, 42));
        panel.add(titulo);
        panel.add(crearLabel("   Fecha del cierre:"));
        txtFechaCierre = new JTextField(LocalDate.now().toString(), 9);
        txtFechaCierre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaCierre.setToolTipText("YYYY-MM-DD (vacío = hoy)");
        panel.add(txtFechaCierre);

        JButton btnCierre = new JButton("Cierre del día");
        btnCierre.setBackground(new Color(14, 165, 233));
        btnCierre.setForeground(Color.WHITE);
        btnCierre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCierre.setBorderPainted(false);
        btnCierre.setFocusPainted(false);
        btnCierre.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCierre.addActionListener(e -> mostrarCierreDelDia());
        panel.add(btnCierre);

        lblEstado = crearLabel(" ");
        panel.add(lblEstado);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabla = new JTable(modeloTabla);
        TablaSwing.configurar(tabla);
        TablaSwing.alinearDerecha(tabla, 0, 5);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        JButton btnComprobante = new JButton("Ver comprobante");
        btnComprobante.setBackground(new Color(241, 245, 249));
        btnComprobante.setForeground(new Color(100, 116, 139));
        btnComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnComprobante.setBorderPainted(false);
        btnComprobante.setFocusPainted(false);
        btnComprobante.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComprobante.addActionListener(e -> verComprobanteSeleccionado());
        panel.add(btnComprobante);
        return panel;
    }

    /** Recarga las facturas más nuevas primero y conserva el acceso tipado a cada factura. */
    public void refrescar() {
        List<Factura> facturas = new ArrayList<>(controlador.listarTodos());
        java.util.Collections.reverse(facturas);
        Map<String, String> nombresMedios = new HashMap<>();
        for (MedioPago medio : controlador.listarMediosPago()) {
            nombresMedios.put(medio.getCodigo(), medio.getNombre());
        }
        modeloTabla.setFacturas(facturas, nombresMedios);
    }

    private void verComprobanteSeleccionado() {
        Factura factura = facturaSeleccionada();
        if (factura == null) {
            informarError("Seleccione una factura de la tabla primero.");
            return;
        }
        ComprobanteFactura.mostrar(this, factura, controlador.listarMediosPago());
    }

    private Factura facturaSeleccionada() {
        int filaVista = tabla.getSelectedRow();
        return filaVista < 0 ? null : modeloTabla.getFacturaAt(tabla.convertRowIndexToModel(filaVista));
    }

    private void mostrarCierreDelDia() {
        String fechaTexto = txtFechaCierre.getText().trim();
        try {
            CierreDelDia cierre = fechaTexto.isEmpty()
                    ? controlador.cerrarDia()
                    : controlador.cerrarDia(LocalDate.parse(fechaTexto));
            mostrarEstado("Cierre generado para " + cierre.getFecha() + ".", false);
            JOptionPane.showMessageDialog(this, armarTextoCierre(cierre),
                    "Cierre del día " + cierre.getFecha(), JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException error) {
            informarError("Formato de fecha inválido. Use YYYY-MM-DD.");
        } catch (ClinicaException error) {
            informarError(error.getMessage());
        }
    }

    private String armarTextoCierre(CierreDelDia cierre) {
        StringBuilder sb = new StringBuilder();
        sb.append("Facturas emitidas: ").append(cierre.getCantidadFacturas()).append("\n");
        sb.append("Total recibido: ").append(FormatoPlata.formatear(cierre.getMontoTotal())).append("\n\n");
        sb.append("Detalle por medio de pago:\n");
        for (Map.Entry<String, Double> entrada : cierre.getTotalPorMedioPago().entrySet()) {
            sb.append("  ").append(entrada.getKey()).append(": ")
                    .append(FormatoPlata.formatear(entrada.getValue())).append("\n");
        }
        return sb.toString();
    }

    private void informarError(String mensaje) {
        mostrarEstado(mensaje, true);
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarEstado(String mensaje, boolean error) {
        lblEstado.setText(mensaje == null || mensaje.isBlank() ? " " : mensaje);
        lblEstado.setForeground(error ? new Color(185, 28, 28) : new Color(100, 116, 139));
        lblEstado.setToolTipText(mensaje);
    }

    private JLabel crearLabel(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etiqueta.setForeground(new Color(71, 85, 105));
        return etiqueta;
    }
}
