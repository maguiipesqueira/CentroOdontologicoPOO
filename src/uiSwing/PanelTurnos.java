package uiSwing;

import controller.ControladorTurno;
import controller.FiltroTurnos;
import entity.EstadoTurno;
import entity.Odontologo;
import entity.Paciente;
import exception.ClinicaException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Panel de agenda: filtros combinables, tabla tipada y acciones sobre el turno seleccionado. */
public class PanelTurnos extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private final ControladorTurno controlador;
    private final TurnoTableModel modeloTabla = new TurnoTableModel();
    private JTable tabla;
    private JComboBox<OpcionFiltro<Paciente>> comboFiltroPaciente;
    private JComboBox<OpcionFiltro<Odontologo>> comboFiltroOdontologo;
    private JComboBox<OpcionFiltro<EstadoTurno>> comboFiltroEstado;
    private JTextField txtFiltroDesde;
    private JTextField txtFiltroHasta;

    public PanelTurnos(ControladorTurno controlador) {
        if (controlador == null) throw new IllegalArgumentException("El controlador es obligatorio.");
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        add(crearPanelFiltros(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
        refrescarTabla();
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        panel.add(crearLabel("Paciente:"));
        comboFiltroPaciente = new JComboBox<>();
        comboFiltroPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comboFiltroPaciente);

        panel.add(crearLabel("Odontólogo:"));
        comboFiltroOdontologo = new JComboBox<>();
        comboFiltroOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comboFiltroOdontologo);

        panel.add(crearLabel("Estado:"));
        comboFiltroEstado = new JComboBox<>();
        comboFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comboFiltroEstado);

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
        cargarOpcionesFiltro();
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setForeground(new Color(71, 85, 105));
        tabla.setGridColor(new Color(226, 232, 240));
        tabla.getColumnModel().getColumn(5).setCellRenderer(new EstadoTurnoCellRenderer());
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        JButton confirmar = crearBoton("Confirmar", new Color(14, 165, 233), Color.WHITE);
        confirmar.addActionListener(e -> ejecutarAccion("Turno confirmado correctamente.", controlador::confirmarTurno));
        JButton cancelar = crearBoton("Cancelar", new Color(255, 228, 230), new Color(136, 19, 55));
        cancelar.addActionListener(e -> ejecutarAccion("Turno cancelado correctamente.", controlador::cancelarTurno));
        JButton completar = crearBoton("Completar", new Color(209, 250, 229), new Color(6, 78, 59));
        completar.addActionListener(e -> ejecutarAccion("Turno completado correctamente.", controlador::completarTurno));
        panel.add(confirmar); panel.add(cancelar); panel.add(completar);
        return panel;
    }

    public void refrescarTabla() {
        cargarOpcionesFiltro();
        modeloTabla.setTurnos(controlador.listarTodos());
    }

    private void cargarOpcionesFiltro() {
        if (comboFiltroPaciente == null) return;
        Object pacienteSeleccionado = comboFiltroPaciente.getSelectedItem();
        Object odontologoSeleccionado = comboFiltroOdontologo.getSelectedItem();
        Object estadoSeleccionado = comboFiltroEstado.getSelectedItem();
        comboFiltroPaciente.removeAllItems();
        comboFiltroPaciente.addItem(OpcionFiltro.todos("Todos los pacientes"));
        controlador.listarPacientes().forEach(p -> comboFiltroPaciente.addItem(OpcionFiltro.de(p.getNombreCompleto(), p)));
        comboFiltroOdontologo.removeAllItems();
        comboFiltroOdontologo.addItem(OpcionFiltro.todos("Todos los odontólogos"));
        controlador.listarOdontologos().forEach(o -> comboFiltroOdontologo.addItem(OpcionFiltro.de(o.getNombreCompleto(), o)));
        comboFiltroEstado.removeAllItems();
        comboFiltroEstado.addItem(OpcionFiltro.todos("Todos los estados"));
        for (EstadoTurno estado : EstadoTurno.values()) {
            comboFiltroEstado.addItem(OpcionFiltro.de(estado.name(), estado));
        }
        restaurarSeleccion(comboFiltroPaciente, pacienteSeleccionado);
        restaurarSeleccion(comboFiltroOdontologo, odontologoSeleccionado);
        restaurarSeleccion(comboFiltroEstado, estadoSeleccionado);
    }

    @SuppressWarnings("unchecked")
    private <T> OpcionFiltro<T> seleccion(JComboBox<OpcionFiltro<T>> combo) {
        return (OpcionFiltro<T>) combo.getSelectedItem();
    }

    private <T> void restaurarSeleccion(JComboBox<OpcionFiltro<T>> combo, Object anterior) {
        if (anterior instanceof OpcionFiltro<?> opcion && opcion.valor() != null) {
            for (int i = 1; i < combo.getItemCount(); i++) {
                OpcionFiltro<T> actual = combo.getItemAt(i);
                if (actual.valor().equals(opcion.valor())) { combo.setSelectedIndex(i); return; }
            }
        }
        combo.setSelectedIndex(0);
    }

    private void aplicarFiltros() {
        try {
            String desdeTexto = txtFiltroDesde.getText().trim();
            String hastaTexto = txtFiltroHasta.getText().trim();
            LocalDate desde = desdeTexto.isEmpty() ? null : LocalDate.parse(desdeTexto);
            LocalDate hasta = hastaTexto.isEmpty() ? null : LocalDate.parse(hastaTexto);
            OpcionFiltro<Paciente> paciente = seleccion(comboFiltroPaciente);
            OpcionFiltro<Odontologo> odontologo = seleccion(comboFiltroOdontologo);
            OpcionFiltro<EstadoTurno> estado = seleccion(comboFiltroEstado);
            FiltroTurnos filtro = new FiltroTurnos(
                    paciente == null || paciente.valor() == null ? null : paciente.valor().getId(),
                    odontologo == null || odontologo.valor() == null ? null : odontologo.valor().getId(),
                    desde, hasta, estado == null ? null : estado.valor());
            modeloTabla.setTurnos(controlador.filtrar(filtro));
        } catch (DateTimeParseException e) {
            mostrarError("Formato de fecha inválido. Use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (ClinicaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarFiltros() {
        comboFiltroPaciente.setSelectedIndex(0);
        comboFiltroOdontologo.setSelectedIndex(0);
        comboFiltroEstado.setSelectedIndex(0);
        txtFiltroDesde.setText("");
        txtFiltroHasta.setText("");
        modeloTabla.setTurnos(controlador.listarTodos());
    }

    private void ejecutarAccion(String mensajeExito, AccionTurno accion) {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla primero.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        long id = modeloTabla.getTurnoAt(filaModelo).getId();
        try {
            accion.ejecutar(id);
            JOptionPane.showMessageDialog(this, mensajeExito);
            refrescarTabla();
        } catch (ClinicaException e) { mostrarError(e.getMessage()); }
    }

    private void abrirDialogoNuevoTurno() {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        JFrame padre = ventana instanceof JFrame ? (JFrame) ventana : null;
        new DialogoNuevoTurno(padre, controlador, this::refrescarTabla).setVisible(true);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105)); return lbl;
    }
    private JTextField crearCampoFecha() {
        JTextField txt = new JTextField(9); txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setToolTipText("YYYY-MM-DD"); return txt;
    }
    private void estilizarBotonSecundario(JButton boton) {
        boton.setBackground(new Color(241, 245, 249)); boton.setForeground(new Color(100, 116, 139));
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12)); boton.setBorderPainted(false);
        boton.setFocusPainted(false); boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private JButton crearBoton(String texto, Color fondo, Color frente) {
        JButton b = new JButton(texto); b.setBackground(fondo); b.setForeground(frente);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
    }

    @FunctionalInterface private interface AccionTurno { void ejecutar(long id) throws ClinicaException; }

    private record OpcionFiltro<T>(String etiqueta, T valor) {
        static <T> OpcionFiltro<T> todos(String etiqueta) { return new OpcionFiltro<>(etiqueta, null); }
        static <T> OpcionFiltro<T> de(String etiqueta, T valor) { return new OpcionFiltro<>(etiqueta, valor); }
        @Override public String toString() { return etiqueta; }
    }
}
