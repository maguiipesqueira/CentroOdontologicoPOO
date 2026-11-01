package uiSwing;

import controller.ControladorOdontologo;
import controller.DatosOdontologo;
import entity.Especialidad;
import entity.MotivoConsulta;
import entity.Odontologo;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Objects;

/** Pantalla de odontólogos; la creación polimórfica se delega al controlador. */
public class PanelOdontologo extends JPanel {

    private final ControladorOdontologo controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campNombre;
    private JTextField campApellido;
    private JTextField campMatricula;
    private JComboBox<Especialidad> combEspecialidad;
    private JComboBox<MotivoConsulta> combSubespecialidad;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private long idSeleccionado = -1;
    private ModoFormulario modo = ModoFormulario.ALTA;

    public PanelOdontologo(ControladorOdontologo controlador) {
        this.controlador = Objects.requireNonNull(controlador, "El controlador de odontólogos es obligatorio.");
        setLayout(new BorderLayout(8, 8));
        add(crearTabla(), BorderLayout.CENTER);
        add(crearFormulario(), BorderLayout.SOUTH);
        cambiarModo(ModoFormulario.ALTA);
        refrescarTabla();
    }

    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Apellido", "Matrícula", "Especialidad", "Subespecialidad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(this::alSeleccionarOdontologo);
        return new JScrollPane(tabla);
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridLayout(0, 4, 5, 5));
        campNombre = new JTextField();
        campApellido = new JTextField();
        campMatricula = new JTextField();
        combEspecialidad = new JComboBox<>(Especialidad.values());
        combSubespecialidad = new JComboBox<>();
        actualizarOpcionesSubespecialidad();
        combEspecialidad.addActionListener(e -> actualizarOpcionesSubespecialidad());

        agregarCampo(formulario, "Nombre:", campNombre);
        agregarCampo(formulario, "Apellido:", campApellido);
        agregarCampo(formulario, "Matrícula:", campMatricula);
        agregarCampo(formulario, "Especialidad:", combEspecialidad);
        agregarCampo(formulario, "Subespecialidad:", combSubespecialidad);

        btnGuardar = new JButton();
        btnEliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar / nueva alta");
        formulario.add(btnGuardar);
        formulario.add(btnEliminar);
        formulario.add(limpiar);
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiarFormulario());
        return formulario;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JComponent campo) {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    private void actualizarOpcionesSubespecialidad() {
        if (combEspecialidad == null || combSubespecialidad == null) return;
        MotivoConsulta seleccionado = (MotivoConsulta) combSubespecialidad.getSelectedItem();
        Especialidad especialidad = (Especialidad) combEspecialidad.getSelectedItem();
        DefaultComboBoxModel<MotivoConsulta> modelo = new DefaultComboBoxModel<>();
        if (especialidad != null) {
            especialidad.getMotivosPosibles().forEach(modelo::addElement);
        }
        combSubespecialidad.setModel(modelo);
        if (seleccionado != null && especialidad != null && especialidad.getMotivosPosibles().contains(seleccionado)) {
            combSubespecialidad.setSelectedItem(seleccionado);
        } else if (especialidad != null) {
            combSubespecialidad.setSelectedItem(especialidad.getSubespecialidadPorDefecto());
        }
    }

    private void alSeleccionarOdontologo(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) return;
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            if (modo == ModoFormulario.EDICION) limpiarFormulario();
            return;
        }
        int fila = tabla.convertRowIndexToModel(filaVista);
        long id = ((Number) modeloTabla.getValueAt(fila, 0)).longValue();
        try {
            cargarOdontologo(controlador.buscarPorId(id));
        } catch (OdontologoNoEncontradoException ex) {
            informarError(ex.getMessage());
            limpiarFormulario();
        }
    }

    private void cargarOdontologo(Odontologo odontologo) {
        idSeleccionado = odontologo.getId();
        campNombre.setText(odontologo.getNombre());
        campApellido.setText(odontologo.getApellido());
        campMatricula.setText(String.valueOf(odontologo.getMatricula()));
        combEspecialidad.setSelectedItem(odontologo.getEspecialidad());
        actualizarOpcionesSubespecialidad();
        combSubespecialidad.setSelectedItem(odontologo.getSubespecialidad());
        cambiarModo(ModoFormulario.EDICION);
    }

    private void guardar() {
        try {
            int matricula = Integer.parseInt(campMatricula.getText().trim());
            DatosOdontologo datos = new DatosOdontologo(campNombre.getText(), campApellido.getText(), matricula,
                    (Especialidad) combEspecialidad.getSelectedItem(),
                    (MotivoConsulta) combSubespecialidad.getSelectedItem());
            if (modo == ModoFormulario.ALTA) {
                controlador.registrarOdontologo(datos);
                informar("Odontólogo registrado correctamente.");
            } else {
                controlador.actualizarOdontologo(idSeleccionado, datos);
                informar("Odontólogo actualizado correctamente.");
            }
            limpiarFormulario();
            refrescarTabla();
        } catch (NumberFormatException ex) {
            informarError("La matrícula debe ser un número entero.");
        } catch (DatoInvalidoException | OdontologoNoEncontradoException ex) {
            informarError(ex.getMessage());
        }
    }

    private void eliminar() {
        if (modo != ModoFormulario.EDICION) {
            informarError("Seleccioná un odontólogo de la tabla primero.");
            return;
        }
        try {
            controlador.eliminarOdontologo(idSeleccionado);
            limpiarFormulario();
            refrescarTabla();
            informar("Odontólogo eliminado correctamente.");
        } catch (DatoInvalidoException | OdontologoNoEncontradoException ex) {
            informarError(ex.getMessage());
        }
    }

    private void cambiarModo(ModoFormulario nuevoModo) {
        modo = nuevoModo;
        boolean alta = modo == ModoFormulario.ALTA;
        btnGuardar.setText(alta ? "Registrar odontólogo" : "Actualizar odontólogo");
        btnEliminar.setEnabled(!alta);
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        cambiarModo(ModoFormulario.ALTA);
        tabla.clearSelection();
        campNombre.setText("");
        campApellido.setText("");
        campMatricula.setText("");
        combEspecialidad.setSelectedIndex(0);
        actualizarOpcionesSubespecialidad();
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Odontologo odontologo : controlador.listarTodos()) {
            modeloTabla.addRow(new Object[]{odontologo.getId(), odontologo.getNombre(), odontologo.getApellido(),
                    odontologo.getMatricula(), odontologo.getEspecialidad().getNombre(),
                    odontologo.getSubespecialidad().getNombreLegible()});
        }
    }

    private void informar(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    private void informarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Odontólogos", JOptionPane.ERROR_MESSAGE);
    }
}
