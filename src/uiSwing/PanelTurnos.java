package uiSwing;

import controller.ControladorTurno;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import exception.ClinicaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

// panel con el listado de turnos: tabla, filtros y acciones (confirmar/cancelar/completar)
public class PanelTurnos extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private static final String[] COLUMNAS = {"ID", "Paciente", "Odontologo", "Tipo de consulta", "Fecha", "Hora", "Estado"};

    private final ControladorTurno controlador;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JComboBox<String> comboFiltroPaciente;
    private JComboBox<String> comboFiltroOdontologo;
    private JTextField txtFiltroDesde;
    private JTextField txtFiltroHasta;

    // listas guardadas para poder mapear el indice elegido en el combo a un id real
    private List<Paciente> pacientesCache;
    private List<Odontologo> odontologosCache;

    public PanelTurnos(ControladorTurno controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout());
        setBackground(FONDO);

        add(crearPanelFiltros(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        refrescarTabla();
    }

    // ---------- construccion de subpaneles ----------

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        panel.add(crearLabel("Paciente:"));
        comboFiltroPaciente = new JComboBox<>();
        comboFiltroPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comboFiltroPaciente);

        panel.add(crearLabel("Odontologo:"));
        comboFiltroOdontologo = new JComboBox<>();
        comboFiltroOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comboFiltroOdontologo);

        panel.add(crearLabel("Desde:"));
        txtFiltroDesde = crearCampoFecha();
        panel.add(txtFiltroDesde);

        panel.add(crearLabel("Hasta:"));
        txtFiltroHasta = crearCampoFecha();
        panel.add(txtFiltroHasta);

        JButton btnFiltrar = new JButton("Filtrar");
        estilizarBotonSecundario(btnFiltrar);
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar filtros");
        estilizarBotonSecundario(btnLimpiar);
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar);

        JButton btnNuevo = new JButton("+ Nuevo turno");
        btnNuevo.setBackground(new Color(14, 165, 233));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnNuevo.setBorderPainted(false);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirDialogoNuevoTurno());
        panel.add(btnNuevo);

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
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setForeground(new Color(71, 85, 105));
        tabla.setGridColor(new Color(226, 232, 240));

        // columna estado con colores (ahora es la columna numero 6, porque agregamos la de tipo de consulta)
        tabla.getColumnModel().getColumn(6).setCellRenderer(new EstadoTurnoCellRenderer());

        // oculta la columna ID visualmente pero la deja accesible (la usamos para las acciones)
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setBackground(new Color(14, 165, 233));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.addActionListener(e -> confirmarSeleccionado());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(255, 228, 230));
        btnCancelar.setForeground(new Color(136, 19, 55));
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> cancelarSeleccionado());

        JButton btnCompletar = new JButton("Completar");
        btnCompletar.setBackground(new Color(209, 250, 229));
        btnCompletar.setForeground(new Color(6, 78, 59));
        btnCompletar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCompletar.setBorderPainted(false);
        btnCompletar.setFocusPainted(false);
        btnCompletar.addActionListener(e -> completarSeleccionado());

        panel.add(btnConfirmar);
        panel.add(btnCancelar);
        panel.add(btnCompletar);

        return panel;
    }

    // ---------- carga y refresco de datos ----------

    // recarga la tabla con todos los turnos (sin filtro)
    public void refrescarTabla() {
        cargarFiltros();
        cargarFilas(controlador.listarTodos());
    }

    private void cargarFiltros() {
        pacientesCache = controlador.listarPacientes();
        odontologosCache = controlador.listarOdontologos();

        comboFiltroPaciente.removeAllItems();
        comboFiltroPaciente.addItem("Todos");
        for (Paciente p : pacientesCache) {
            comboFiltroPaciente.addItem(p.getNombreCompleto() + " (DNI " + p.getDni() + ")");
        }

        comboFiltroOdontologo.removeAllItems();
        comboFiltroOdontologo.addItem("Todos");
        for (Odontologo o : odontologosCache) {
            comboFiltroOdontologo.addItem(o.getNombreCompleto() + " (Mat. " + o.getMatricula() + ")");
        }
    }

    private void cargarFilas(List<Turno> turnos) {
        modeloTabla.setRowCount(0);

        for (Turno t : turnos) {
            modeloTabla.addRow(new Object[]{
                    t.getId(),
                    t.getPaciente().getNombreCompleto(),
                    t.getOdontologo().getNombreCompleto(),
                    t.getMotivoConsultaTexto(),
                    t.getFecha().toString(),
                    t.getHora().toString(),
                    t.getEstado().name()
            });
        }
    }

    // ---------- filtros ----------

    private void aplicarFiltros() {
        int indicePaciente = comboFiltroPaciente.getSelectedIndex();
        int indiceOdontologo = comboFiltroOdontologo.getSelectedIndex();
        String desdeTexto = txtFiltroDesde.getText().trim();
        String hastaTexto = txtFiltroHasta.getText().trim();

        try {
            List<Turno> resultado;

            // si hay fechas cargadas, ese filtro tiene prioridad
            if (!desdeTexto.isEmpty() || !hastaTexto.isEmpty()) {
                if (desdeTexto.isEmpty() || hastaTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Debe completar ambas fechas (desde y hasta) para filtrar por rango.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate desde = LocalDate.parse(desdeTexto);
                LocalDate hasta = LocalDate.parse(hastaTexto);
                resultado = controlador.listarPorRangoFechas(desde, hasta);
            } else if (indicePaciente > 0) {
                Paciente p = pacientesCache.get(indicePaciente - 1);
                resultado = controlador.listarPorPaciente(p.getId());
            } else if (indiceOdontologo > 0) {
                Odontologo o = odontologosCache.get(indiceOdontologo - 1);
                resultado = controlador.listarPorOdontologo(o.getId());
            } else {
                resultado = controlador.listarTodos();
            }

            cargarFilas(resultado);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha invalido. Use YYYY-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFiltros() {
        comboFiltroPaciente.setSelectedIndex(0);
        comboFiltroOdontologo.setSelectedIndex(0);
        txtFiltroDesde.setText("");
        txtFiltroHasta.setText("");
        cargarFilas(controlador.listarTodos());
    }

    // ---------- acciones sobre turno seleccionado ----------

    private void confirmarSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla primero.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = (long) modeloTabla.getValueAt(filaSeleccionada, 0);

        try {
            controlador.confirmarTurno(id);
            JOptionPane.showMessageDialog(this, "Turno confirmado correctamente.");
            refrescarTabla();
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla primero.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = (long) modeloTabla.getValueAt(filaSeleccionada, 0);

        try {
            controlador.cancelarTurno(id);
            JOptionPane.showMessageDialog(this, "Turno cancelado correctamente.");
            refrescarTabla();
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completarSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla primero.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = (long) modeloTabla.getValueAt(filaSeleccionada, 0);

        try {
            controlador.completarTurno(id);
            JOptionPane.showMessageDialog(this, "Turno completado correctamente.");
            refrescarTabla();
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- dialogo nuevo turno ----------

    private void abrirDialogoNuevoTurno() {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        JFrame padre = null;

        if (ventana instanceof JFrame) {
            padre = (JFrame) ventana;
        }

        DialogoNuevoTurno dialogo = new DialogoNuevoTurno(padre, controlador, this::refrescarTabla);
        dialogo.setVisible(true);
    }

    // ---------- helpers visuales ----------

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JTextField crearCampoFecha() {
        JTextField txt = new JTextField(9);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setToolTipText("YYYY-MM-DD");
        return txt;
    }

    private void estilizarBotonSecundario(JButton boton) {
        boton.setBackground(new Color(241, 245, 249));
        boton.setForeground(new Color(100, 116, 139));
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
