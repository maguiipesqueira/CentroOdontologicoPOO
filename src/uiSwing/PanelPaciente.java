package uiSwing;

import controller.ControladorPaciente;
import entity.Paciente;

import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.util.List;

public class PanelPaciente extends JPanel {

    private ControladorPaciente controlador;

    // tabla
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // campos del formulario
    private JTextField campNombre;
    private JTextField campApellido;
    private JTextField campDni;
    private JTextField campEmail;
    private JTextField campCalle;
    private JTextField campNumero;
    private JTextField campLocalidad;
    private JTextField campProvincia;
    private JComboBox<String> combTipoHogar;

    // id del paciente seleccionado (para editar)
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
        combTipoHogar = new JComboBox<>(new String[]{"CASA", "DEPARTAMENTO", "PH"});

        panel.add(new JLabel("Nombre:")); panel.add(campNombre);
        panel.add(new JLabel("Apellido:")); panel.add(campApellido);
        panel.add(new JLabel("DNI:")); panel.add(campDni);
        panel.add(new JLabel("Email:")); panel.add(campEmail);
        panel.add(new JLabel("Calle:")); panel.add(campCalle);
        panel.add(new JLabel("Número:")); panel.add(campNumero);
        panel.add(new JLabel("Localidad:")); panel.add(campLocalidad);
        panel.add(new JLabel("Provincia:")); panel.add(campProvincia);
        panel.add(new JLabel("Tipo Hogar:")); panel.add(combTipoHogar);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

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
