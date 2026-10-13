package uiSwing;

import controller.ControladorFactura;
import entity.Factura;
import exception.ClinicaException;
import service.CierreDelDia;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// panel con las facturas emitidas (mas nueva primero), el comprobante de cada una y el cierre del dia
public class PanelFacturasEmitidas extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private static final String[] COLUMNAS = {"N°", "Fecha", "Paciente", "Tratamiento", "Medio de pago", "Total"};

    private final ControladorFactura controlador;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtFechaCierre;

    // facturas que muestra la tabla, en el mismo orden, para saber cual es la fila elegida
    private List<Factura> facturasCache = new ArrayList<>();

    public PanelFacturasEmitidas(ControladorFactura controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout());
        setBackground(FONDO);

        add(crearPanelCierre(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        refrescar();
    }

    // ---------- construccion de subpaneles ----------

    // franja superior: fecha y boton del cierre del dia
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
        txtFechaCierre.setToolTipText("YYYY-MM-DD (vacio = hoy)");
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

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // la tabla es de solo lectura
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setForeground(new Color(71, 85, 105));
        tabla.setGridColor(new Color(226, 232, 240));

        // el total va alineado a la derecha
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(5).setCellRenderer(derecha);

        // la columna del numero de factura es angosta
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

    // ---------- carga y refresco de datos ----------

    // recarga la tabla con todas las facturas emitidas, la mas nueva primero
    public void refrescar() {
        facturasCache = new ArrayList<>(controlador.listarTodos());
        Collections.reverse(facturasCache);

        modeloTabla.setRowCount(0);
        for (Factura f : facturasCache) {
            modeloTabla.addRow(new Object[]{
                    f.getId(),
                    f.getFecha().toString(),
                    f.getPacienteNombre(),
                    f.getConcepto(),
                    f.getMetodoPago(),
                    FormatoPlata.formatear(f.getMontoTotal())
            });
        }
    }

    // ---------- acciones ----------

    // abre el comprobante de la factura seleccionada
    private void verComprobanteSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura de la tabla primero.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ComprobanteFactura.mostrar(this, facturasCache.get(fila));
    }

    // muestra el cierre del dia de la fecha escrita (hoy si esta vacia): total, cantidad de facturas y detalle por medio
    private void mostrarCierreDelDia() {
        String fechaTexto = txtFechaCierre.getText().trim();

        try {
            CierreDelDia cierre = fechaTexto.isEmpty()
                    ? controlador.cerrarDia()
                    : controlador.cerrarDia(LocalDate.parse(fechaTexto));

            JOptionPane.showMessageDialog(this, armarTextoCierre(cierre),
                    "Cierre del día " + cierre.getFecha(), JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha invalido. Use YYYY-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    // ---------- helpers ----------

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }
}