package uiSwing;

import controller.ControladorFactura;
import entity.DesgloseMonto;
import entity.Factura;
import entity.Turno;
import exception.ClinicaException;
import pago.MedioPago;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

/** Pantalla de cobro y consulta de facturas emitidas. */
public class PanelFacturacion extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private final ControladorFactura controlador;
    private final TurnoCobroTableModel modeloTabla = new TurnoCobroTableModel();

    private JTable tabla;
    private JComboBox<OpcionMedioPago> comboMedioPago;
    private JLabel lblTotalAPagar;
    private JLabel lblEstado;
    private PanelFacturasEmitidas panelFacturasEmitidas;

    public PanelFacturacion(ControladorFactura controlador) {
        if (controlador == null) throw new IllegalArgumentException("El controlador de facturación es obligatorio.");
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(FONDO);

        JPanel panelPorCobrar = new JPanel(new BorderLayout());
        panelPorCobrar.setBackground(FONDO);
        panelPorCobrar.add(crearPanelTitulo(), BorderLayout.NORTH);
        panelPorCobrar.add(crearPanelTablaPorCobrar(), BorderLayout.CENTER);
        panelPorCobrar.add(crearPanelCobro(), BorderLayout.SOUTH);

        panelFacturasEmitidas = new PanelFacturasEmitidas(controlador);
        JTabbedPane pestanias = new JTabbedPane();
        pestanias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pestanias.addTab("Por cobrar", panelPorCobrar);
        pestanias.addTab("Facturas emitidas", panelFacturasEmitidas);
        add(pestanias, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarTotalAPagar();
        });
        comboMedioPago.addActionListener(e -> actualizarTotalAPagar());
        refrescar();
    }

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
        tabla = new JTable(modeloTabla) {
            @Override public String getToolTipText(MouseEvent evento) {
                int filaVista = rowAtPoint(evento.getPoint());
                if (filaVista < 0) return null;
                int filaModelo = convertRowIndexToModel(filaVista);
                return modeloTabla.getErrorAt(filaModelo);
            }
        };
        TablaSwing.configurar(tabla);
        TablaSwing.alinearDerecha(tabla, 3, 4, 5);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelCobro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        panel.add(crearLabel("Medio de pago:"));

        comboMedioPago = new JComboBox<>();
        comboMedioPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboMedioPago.addItem(OpcionMedioPago.sinSeleccion());
        for (MedioPago medio : controlador.listarMediosPago()) comboMedioPago.addItem(OpcionMedioPago.de(medio));
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

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(100, 116, 139));
        panel.add(lblEstado);
        return panel;
    }

    /** Recarga turnos por cobrar y facturas emitidas. */
    public void refrescar() {
        cargarFilasPorCobrar(controlador.listarTurnosPorCobrar());
        actualizarTotalAPagar();
        panelFacturasEmitidas.refrescar();
    }

    private void cargarFilasPorCobrar(List<Turno> turnos) {
        modeloTabla.limpiar();
        for (Turno turno : turnos) {
            try {
                DesgloseMonto desglose = controlador.calcularVistaPrevia(turno.getId());
                modeloTabla.agregar(turno, desglose, null);
            } catch (ClinicaException error) {
                modeloTabla.agregar(turno, null, error.getMessage());
            }
        }
    }

    private void actualizarTotalAPagar() {
        Turno turno = turnoSeleccionado();
        if (turno == null) {
            lblTotalAPagar.setText("Total a pagar: -");
            mostrarEstado("Seleccione un turno por cobrar.", false);
            return;
        }
        try {
            Optional<MedioPago> medio = medioSeleccionado();
            DesgloseMonto desglose = medio.isPresent()
                    ? controlador.calcularVistaPrevia(turno.getId(), medio.get())
                    : controlador.calcularVistaPrevia(turno.getId());
            lblTotalAPagar.setText("Total a pagar: " + FormatoPlata.formatear(desglose.getMontoTotalDecimal()));
            mostrarEstado(medio.isEmpty() ? "Vista previa sin descuento por medio de pago." : "Vista previa actualizada.", false);
        } catch (ClinicaException error) {
            lblTotalAPagar.setText("Total a pagar: -");
            mostrarEstado(error.getMessage(), true);
        }
    }

    private void cobrarSeleccionado() {
        Turno turno = turnoSeleccionado();
        if (turno == null) {
            informarError("Seleccione un turno por cobrar primero.");
            return;
        }
        Optional<MedioPago> medio = medioSeleccionado();
        if (medio.isEmpty()) {
            informarError("Debe elegir un medio de pago antes de cobrar.");
            comboMedioPago.requestFocusInWindow();
            return;
        }
        try {
            Factura factura = controlador.cobrarTurno(turno.getId(), medio.get());
            ComprobanteFactura.mostrar(this, factura, controlador.listarMediosPago());
            comboMedioPago.setSelectedIndex(0);
            tabla.clearSelection();
            refrescar();
            mostrarEstado("Factura #" + factura.getId() + " emitida correctamente.", false);
        } catch (ClinicaException error) {
            informarError(error.getMessage());
        }
    }

    private Turno turnoSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) return null;
        return modeloTabla.getTurnoAt(tabla.convertRowIndexToModel(filaVista));
    }

    private Optional<MedioPago> medioSeleccionado() {
        OpcionMedioPago opcion = (OpcionMedioPago) comboMedioPago.getSelectedItem();
        return opcion == null ? Optional.empty() : opcion.medioPago();
    }

    private void informarError(String mensaje) {
        mostrarEstado(mensaje, true);
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar el cobro", JOptionPane.ERROR_MESSAGE);
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
