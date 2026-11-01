package uiSwing;

import controller.ControladorPaciente;
import controller.DatosDomicilio;
import controller.DatosPaciente;
import controller.DatosPacienteActualizacion;
import entity.Paciente;
import entity.TipoHogar;
import exception.DatoInvalidoException;
import exception.PacienteNoEncontradoException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/** Pantalla de pacientes: captura datos y delega validación y construcción al controlador. */
public class PanelPaciente extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);

    private final ControladorPaciente controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campNombre;
    private JTextField campApellido;
    private JTextField campDni;
    private JTextField campEmail;
    private JTextField campCalle;
    private JTextField campNumero;
    private JTextField campLocalidad;
    private JTextField campProvincia;
    private JComboBox<TipoHogar> combTipoHogar;
    private JCheckBox chkObraSocial;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private long idSeleccionado = -1;
    private ModoFormulario modo = ModoFormulario.ALTA;

    public PanelPaciente(ControladorPaciente controlador) {
        this.controlador = Objects.requireNonNull(controlador, "El controlador de pacientes es obligatorio.");
        setLayout(new BorderLayout(8, 8));
        setBackground(FONDO);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelFormulario(), BorderLayout.SOUTH);
        cambiarModo(ModoFormulario.ALTA);
        refrescarTabla();
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Email", "Obra social"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(this::alSeleccionarPaciente);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.setBackground(FONDO);
        JTextField dniBusqueda = new JTextField(10);
        JButton buscar = new JButton("Buscar por DNI");
        JButton todos = new JButton("Mostrar todos");
        busqueda.add(new JLabel("DNI:"));
        busqueda.add(dniBusqueda);
        busqueda.add(buscar);
        busqueda.add(todos);
        buscar.addActionListener(e -> buscarPorDni(dniBusqueda.getText()));
        todos.addActionListener(e -> refrescarTabla());

        panel.add(busqueda, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));
        panel.setBackground(FONDO);
        campNombre = new JTextField();
        campApellido = new JTextField();
        campDni = new JTextField();
        campEmail = new JTextField();
        campCalle = new JTextField();
        campNumero = new JTextField();
        campLocalidad = new JTextField();
        campProvincia = new JTextField();
        combTipoHogar = new JComboBox<>(TipoHogar.values());
        chkObraSocial = new JCheckBox("Tiene obra social");
        chkObraSocial.setBackground(FONDO);

        agregarCampo(panel, "Nombre:", campNombre);
        agregarCampo(panel, "Apellido:", campApellido);
        agregarCampo(panel, "DNI:", campDni);
        agregarCampo(panel, "Email:", campEmail);
        agregarCampo(panel, "Calle:", campCalle);
        agregarCampo(panel, "Número:", campNumero);
        agregarCampo(panel, "Localidad:", campLocalidad);
        agregarCampo(panel, "Provincia:", campProvincia);
        agregarCampo(panel, "Tipo hogar:", combTipoHogar);
        agregarCampo(panel, "Obra social:", chkObraSocial);

        btnGuardar = new JButton();
        btnEliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar / nueva alta");
        panel.add(btnGuardar);
        panel.add(btnEliminar);
        panel.add(limpiar);
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiarFormulario());
        return panel;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JComponent campo) {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    private void alSeleccionarPaciente(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) return;
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            if (modo == ModoFormulario.EDICION) limpiarFormulario();
            return;
        }
        int fila = tabla.convertRowIndexToModel(filaVista);
        long id = ((Number) modeloTabla.getValueAt(fila, 0)).longValue();
        try {
            cargarPaciente(controlador.buscarPorId(id));
        } catch (PacienteNoEncontradoException ex) {
            informarError(ex.getMessage());
            limpiarFormulario();
        }
    }

    private void cargarPaciente(Paciente paciente) {
        idSeleccionado = paciente.getId();
        campNombre.setText(paciente.getNombre());
        campApellido.setText(paciente.getApellido());
        campDni.setText(String.valueOf(paciente.getDni()));
        campEmail.setText(paciente.getEmail());
        chkObraSocial.setSelected(paciente.tieneObraSocial());
        limpiarCamposDomicilio();
        if (paciente.tieneDomicilio()) {
            var domicilio = paciente.getDomicilio();
            campCalle.setText(domicilio.getCalle());
            campNumero.setText(String.valueOf(domicilio.getNumero()));
            campLocalidad.setText(domicilio.getLocalidad());
            campProvincia.setText(domicilio.getProvincia());
            combTipoHogar.setSelectedItem(domicilio.getHogar());
        }
        cambiarModo(ModoFormulario.EDICION);
    }

    private void buscarPorDni(String texto) {
        try {
            int dni = Integer.parseInt(texto.trim());
            Paciente paciente = controlador.buscarPorDni(dni);
            int fila = buscarFilaPorId(paciente.getId());
            if (fila >= 0) tabla.setRowSelectionInterval(fila, fila);
            else {
                modeloTabla.setRowCount(0);
                modeloTabla.addRow(crearFila(paciente));
                tabla.setRowSelectionInterval(0, 0);
            }
        } catch (NumberFormatException ex) {
            informarError("Ingresá un DNI numérico válido.");
        } catch (PacienteNoEncontradoException | DatoInvalidoException ex) {
            informarError(ex.getMessage());
        }
    }

    private int buscarFilaPorId(long id) {
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            if (((Number) modeloTabla.getValueAt(fila, 0)).longValue() == id) return fila;
        }
        return -1;
    }

    private void guardar() {
        try {
            int dni = Integer.parseInt(campDni.getText().trim());
            DatosDomicilio domicilio = leerDomicilio();
            if (modo == ModoFormulario.ALTA) {
                controlador.registrarPaciente(new DatosPaciente(campNombre.getText(), campApellido.getText(),
                        dni, campEmail.getText(), domicilio, chkObraSocial.isSelected()));
                informar("Paciente registrado correctamente.");
            } else {
                controlador.actualizarPaciente(idSeleccionado, new DatosPacienteActualizacion(
                        campNombre.getText(), campApellido.getText(), campEmail.getText(),
                        domicilio, chkObraSocial.isSelected(), true));
                informar("Paciente actualizado correctamente.");
            }
            limpiarFormulario();
            refrescarTabla();
        } catch (NumberFormatException ex) {
            informarError("El DNI y el número de calle deben ser números.");
        } catch (DatoInvalidoException | PacienteNoEncontradoException ex) {
            informarError(ex.getMessage());
        }
    }

    private DatosDomicilio leerDomicilio() {
        String calle = campCalle.getText();
        String numeroTexto = campNumero.getText().trim();
        String localidad = campLocalidad.getText();
        String provincia = campProvincia.getText();
        boolean vacio = calle.isBlank() && numeroTexto.isBlank() && localidad.isBlank() && provincia.isBlank();
        if (vacio) return null;
        int numero = Integer.parseInt(numeroTexto);
        return new DatosDomicilio(calle, numero, localidad, provincia, (TipoHogar) combTipoHogar.getSelectedItem());
    }

    private void eliminar() {
        if (modo != ModoFormulario.EDICION) {
            informarError("Seleccioná un paciente de la tabla primero.");
            return;
        }
        try {
            controlador.eliminarPaciente(idSeleccionado);
            limpiarFormulario();
            refrescarTabla();
            informar("Paciente eliminado correctamente.");
        } catch (DatoInvalidoException | PacienteNoEncontradoException ex) {
            informarError(ex.getMessage());
        }
    }

    private void cambiarModo(ModoFormulario nuevoModo) {
        modo = nuevoModo;
        boolean alta = modo == ModoFormulario.ALTA;
        btnGuardar.setText(alta ? "Registrar paciente" : "Actualizar paciente");
        btnEliminar.setEnabled(!alta);
        campDni.setEditable(alta);
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        cambiarModo(ModoFormulario.ALTA);
        tabla.clearSelection();
        campNombre.setText("");
        campApellido.setText("");
        campDni.setText("");
        campEmail.setText("");
        limpiarCamposDomicilio();
        chkObraSocial.setSelected(false);
        cambiarModo(ModoFormulario.ALTA);
    }

    private void limpiarCamposDomicilio() {
        campCalle.setText("");
        campNumero.setText("");
        campLocalidad.setText("");
        campProvincia.setText("");
        combTipoHogar.setSelectedIndex(0);
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Paciente> pacientes = controlador.listarTodos();
        for (Paciente paciente : pacientes) modeloTabla.addRow(crearFila(paciente));
    }

    private Object[] crearFila(Paciente paciente) {
        return new Object[]{paciente.getId(), paciente.getNombre(), paciente.getApellido(), paciente.getDni(),
                paciente.getEmail(), paciente.tieneObraSocial() ? "Sí" : "No"};
    }

    private void informar(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    private void informarError(String mensaje) { JOptionPane.showMessageDialog(this, mensaje, "Pacientes", JOptionPane.ERROR_MESSAGE); }
}
