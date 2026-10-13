package uiSwing;

import controller.ControladorFactura;
import entity.Factura;
import entity.Turno;
import exception.ClinicaException;
import pago.MedioPago;
import service.DesgloseMonto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// pantalla de facturacion con dos pestañas: turnos por cobrar (con el cobro) y facturas emitidas (con el cierre del dia)
public class PanelFacturacion extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private static final String[] COLUMNAS = {"ID", "Turno", "Paciente", "Tratamiento", "Precio", "Descuento", "Total"};
    private static final String TEXTO_SIN_MEDIO = "Elegir medio de pago...";

    private final ControladorFactura controlador;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<MedioPago> comboMedioPago;
    private JLabel lblTotalAPagar;
    private PanelFacturasEmitidas panelFacturasEmitidas;

    // mensaje de error de cada fila (null si la fila esta bien), para mostrarlo como tooltip
    private final List<String> erroresPorFila = new ArrayList<>();

    public PanelFacturacion(ControladorFactura controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout());
        setBackground(FONDO);

        // pestaña 1: turnos por cobrar (titulo, tabla y franja de cobro)
        JPanel panelPorCobrar = new JPanel(new BorderLayout());
        panelPorCobrar.setBackground(FONDO);
        panelPorCobrar.add(crearPanelTitulo(), BorderLayout.NORTH);
        panelPorCobrar.add(crearPanelTablaPorCobrar(), BorderLayout.CENTER);
        panelPorCobrar.add(crearPanelCobro(), BorderLayout.SOUTH);

        // pestaña 2: facturas emitidas y cierre del dia
        panelFacturasEmitidas = new PanelFacturasEmitidas(controlador);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pestanias.addTab("Por cobrar", panelPorCobrar);
        pestanias.addTab("Facturas emitidas", panelFacturasEmitidas);
        add(pestanias, BorderLayout.CENTER);

        // los listeners se agregan cuando ya estan armados todos los componentes
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarTotalAPagar();
            }
        });
        comboMedioPago.addActionListener(e -> actualizarTotalAPagar());

        refrescar();
    }

    // ---------- construccion de subpaneles ----------

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JLabel titulo = new JLabel("Turnos por cobrar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(15, 23, 42));
        panel.add(titulo);

        JLabel ayuda = new JLabel("El descuento de la tabla es el de obra social; el de efectivo se aplica al elegir el medio de pago.");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ayuda.setForeground(new Color(100, 116, 139));
        panel.add(ayuda);

        return panel;
    }

    private JPanel crearPanelTablaPorCobrar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // la tabla es de solo lectura
            }
        };

        // el tooltip de cada fila muestra por que no tiene precio (si corresponde)
        tabla = new JTable(modeloTabla) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int fila = rowAtPoint(e.getPoint());
                if (fila >= 0 && fila < erroresPorFila.size()) {
                    return erroresPorFila.get(fila);
                }
                return null;
            }
        };
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setForeground(new Color(71, 85, 105));
        tabla.setGridColor(new Color(226, 232, 240));

        // precio, descuento y total alineados a la derecha
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int columna = 4; columna <= 6; columna++) {
            tabla.getColumnModel().getColumn(columna).setCellRenderer(derecha);
        }

        // oculta la columna ID visualmente pero la deja accesible (la usamos para cobrar)
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // franja inferior: medio de pago, total a pagar con ese medio y boton cobrar
    private JPanel crearPanelCobro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        panel.add(crearLabel("Medio de pago:"));

        // el combo se llena con los medios que informa el controlador: un medio nuevo aparece solo
        comboMedioPago = new JComboBox<>();
        comboMedioPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboMedioPago.addItem(null); // primera opcion vacia: obliga a elegir un medio (CU6, flujo 5a)
        for (MedioPago medio : controlador.listarMediosPago()) {
            comboMedioPago.addItem(medio);
        }
        // se muestra getNombre() de la interfaz, asi el medio nuevo no necesita un toString propio
        comboMedioPago.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                String texto = (valor instanceof MedioPago) ? ((MedioPago) valor).getNombre() : TEXTO_SIN_MEDIO;
                return super.getListCellRendererComponent(lista, texto, indice, seleccionado, foco);
            }
        });
        panel.add(comboMedioPago);

        lblTotalAPagar = new JLabel("Total a pagar: -");
        lblTotalAPagar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalAPagar.setForeground(new Color(15, 23, 42));
        panel.add(lblTotalAPagar);

        JButton btnCobrar = new JButton("Cobrar");
        btnCobrar.setBackground(new Color(14, 165, 233));
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCobrar.setBorderPainted(false);
        btnCobrar.setFocusPainted(false);
        btnCobrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCobrar.addActionListener(e -> cobrarSeleccionado());
        panel.add(btnCobrar);

        return panel;
    }

    // ---------- carga y refresco de datos ----------

    // recarga las dos pestañas: los turnos que todavia se pueden cobrar y las facturas emitidas
    public void refrescar() {
        cargarFilasPorCobrar(controlador.listarTurnosPorCobrar());
        actualizarTotalAPagar();
        panelFacturasEmitidas.refrescar();
    }

    private void cargarFilasPorCobrar(List<Turno> turnos) {
        modeloTabla.setRowCount(0);
        erroresPorFila.clear();

        for (Turno t : turnos) {
            String turnoTexto = t.getFecha() + " " + t.getHora();
            String paciente = t.getPaciente().getNombreCompleto();
            String tratamiento = t.getMotivoConsultaTexto();

            try {
                // sin medio de pago todavia: solo se ve el descuento de obra social
                DesgloseMonto desglose = controlador.calcularVistaPrevia(t.getId());

                modeloTabla.addRow(new Object[]{
                        t.getId(), turnoTexto, paciente, tratamiento,
                        plata(desglose.getMontoBase()),
                        plata(desglose.getDescuentoObraSocial()),
                        plata(desglose.getMontoTotal())
                });
                erroresPorFila.add(null);

            } catch (ClinicaException e) {
                // turno sin tratamiento o sin precio: se muestra igual, marcado, para que no desaparezca de la lista
                modeloTabla.addRow(new Object[]{
                        t.getId(), turnoTexto, paciente, tratamiento, "Sin precio", "-", "-"
                });
                erroresPorFila.add(e.getMessage());
            }
        }
    }

    // ---------- cobro ----------

    // muestra cuanto pagaria el turno seleccionado con el medio elegido (sin guardar nada)
    private void actualizarTotalAPagar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            lblTotalAPagar.setText("Total a pagar: -");
            return;
        }

        long turnoId = (long) modeloTabla.getValueAt(fila, 0);
        MedioPago medio = (MedioPago) comboMedioPago.getSelectedItem();

        try {
            DesgloseMonto desglose = (medio == null)
                    ? controlador.calcularVistaPrevia(turnoId)
                    : controlador.calcularVistaPrevia(turnoId, medio);
            lblTotalAPagar.setText("Total a pagar: " + plata(desglose.getMontoTotal()));
        } catch (ClinicaException e) {
            lblTotalAPagar.setText("Total a pagar: -");
        }
    }

    // cobra el turno seleccionado con el medio elegido
    private void cobrarSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno por cobrar primero.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long turnoId = (long) modeloTabla.getValueAt(fila, 0);
        MedioPago medio = (MedioPago) comboMedioPago.getSelectedItem();

        try {
            // si el medio es null el servicio lanza DatoInvalidoException y no genera la factura
            Factura factura = controlador.cobrarTurno(turnoId, medio);

            // el comprobante muestra el desglose del cobro recien hecho
            ComprobanteFactura.mostrar(this, factura);

            // vuelve a pedir el medio en el proximo cobro, para no cobrar con uno elegido de antes
            comboMedioPago.setSelectedIndex(0);
            refrescar();

        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- helpers ----------

    private String plata(double monto) {
        return FormatoPlata.formatear(monto);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }
}