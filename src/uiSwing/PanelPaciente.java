package uiSwing;

import controller.ControladorPaciente;
import entity.Domicilio;
import entity.Paciente;
import entity.TipoHogar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelPaciente extends JPanel {

    private ControladorPaciente controlador;

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

    private long idSeleccionado = -1;

    public PanelPaciente(ControladorPaciente controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 247, 244));

        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelFormulario(), BorderLayout.SOUTH);

        refrescarTabla();
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 247, 244));

        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // cuando el usuario hace click en una fila de la tabla, este listener se activa
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // obtenemos el número de fila que se clickeó (-1 si no hay ninguna)
                int fila = tabla.getSelectedRow();
                if (fila != -1) {  // si hay una fila seleccionada

                    // guardamos el id del paciente de esa fila (columna 0)
                    idSeleccionado = (long) modeloTabla.getValueAt(fila, 0);
                    // cargamos los datos de la tabla en los campos del formulario
                    campNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                    campApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                    campDni.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                    campEmail.setText((String) modeloTabla.getValueAt(fila, 4));

                    try {
                        // buscamos el paciente completo para obtener el domicilio
                        Paciente p = controlador.buscarPorId(idSeleccionado);
                        if (p.tieneDomicilio()) {
                            // cargamos los datos del domicilio en el formulario
                            campCalle.setText(p.getDomicilio().getCalle());
                            campNumero.setText(String.valueOf(p.getDomicilio().getNumero()));
                            campLocalidad.setText(p.getDomicilio().getLocalidad());
                            campProvincia.setText(p.getDomicilio().getProvincia());
                            combTipoHogar.setSelectedItem(p.getDomicilio().getHogar());
                        }
                    } catch (exception.PacienteNoEncontradoException ex) {
                        // si no encuentra el paciente muestra un mensaje de error
                        JOptionPane.showMessageDialog(null, "Error al cargar paciente");
                    }
                }
            }
        });
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));
        panel.setBackground(new Color(248, 247, 244));

        campNombre    = new JTextField();
        campApellido  = new JTextField();
        campDni       = new JTextField();
        campEmail     = new JTextField();
        campCalle     = new JTextField();
        campNumero    = new JTextField();
        campLocalidad = new JTextField();
        campProvincia = new JTextField();
        combTipoHogar = new JComboBox<>(TipoHogar.values());

        panel.add(new JLabel("Nombre:"));    panel.add(campNombre);
        panel.add(new JLabel("Apellido:"));  panel.add(campApellido);
        panel.add(new JLabel("DNI:"));       panel.add(campDni);
        panel.add(new JLabel("Email:"));     panel.add(campEmail);
        panel.add(new JLabel("Calle:"));     panel.add(campCalle);
        panel.add(new JLabel("Número:"));    panel.add(campNumero);
        panel.add(new JLabel("Localidad:")); panel.add(campLocalidad);
        panel.add(new JLabel("Provincia:")); panel.add(campProvincia);
        panel.add(new JLabel("Tipo Hogar:")); panel.add(combTipoHogar);

        JButton btnGuardar  = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar  = new JButton("Limpiar");

        panel.add(btnGuardar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);

        btnLimpiar.addActionListener(e -> limpiarFormulario());

        return panel;
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        List<Paciente> lista = controlador.listarTodos();
        for (Paciente p : lista) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getEmail()
            });
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        campNombre.setText("");
        campApellido.setText("");
        campDni.setText("");
        campEmail.setText("");
        campCalle.setText("");
        campNumero.setText("");
        campLocalidad.setText("");
        campProvincia.setText("");
        combTipoHogar.setSelectedIndex(0);
    }
}
