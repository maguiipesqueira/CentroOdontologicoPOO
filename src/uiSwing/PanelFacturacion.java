package uiSwing;

import controller.ControladorFactura;
import entity.Turno;
import exception.ClinicaException;
import service.DesgloseMonto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// pantalla de facturacion. Por ahora muestra los turnos por cobrar con su precio, descuento y total;
// el cobro, las facturas emitidas y el cierre del dia se agregan en las etapas siguientes
public class PanelFacturacion extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private static final String[] COLUMNAS = {"ID", "Turno", "Paciente", "Tratamiento", "Precio", "Descuento", "Total"};

    // formato de plata argentino: $ 15.000
    private static final DecimalFormat FORMATO_PLATA =
            new DecimalFormat("$ #,##0.##", new DecimalFormatSymbols(Locale.forLanguageTag("es-AR")));

    private final ControladorFactura controlador;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // mensaje de error de cada fila (null si la fila esta bien), para mostrarlo como tooltip
    private final List<String> erroresPorFila = new ArrayList<>();

    public PanelFacturacion(ControladorFactura controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout());
        setBackground(FONDO);

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(crearPanelTablaPorCobrar(), BorderLayout.CENTER);

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

        JLabel ayuda = new JLabel("El descuento es el de obra social; el de efectivo se aplica al elegir el medio de pago.");
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

        // oculta la columna ID visualmente pero la deja accesible (la vamos a usar para cobrar)
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ---------- carga y refresco de datos ----------

    // recarga la tabla con los turnos que todavia se pueden cobrar
    public void refrescar() {
        cargarFilasPorCobrar(controlador.listarTurnosPorCobrar());
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

    // ---------- helpers ----------

    private String plata(double monto) {
        return FORMATO_PLATA.format(monto);
    }
}