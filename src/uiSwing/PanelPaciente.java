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

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(248, 247, 244));

        JTextField campBusquedaDni = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar por DNI");
        JButton btnMostrarTodos = new JButton("Mostrar todos");

        panelBusqueda.add(new JLabel("DNI:"));
        panelBusqueda.add(campBusquedaDni);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);

        btnBuscar.addActionListener(e -> {
            try {
                int dni = Integer.parseInt(campBusquedaDni.getText());
                Paciente encontrado = controlador.buscarPorDni(dni);
                modeloTabla.setRowCount(0);
                modeloTabla.addRow(new Object[]{
                        encontrado.getId(), encontrado.getNombre(),
                        encontrado.getApellido(), encontrado.getDni(), encontrado.getEmail()
                });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ingresá un DNI válido");
            } catch (exception.PacienteNoEncontradoException ex) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún paciente con ese DNI");
            } catch (exception.DatoInvalidoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        btnMostrarTodos.addActionListener(e -> refrescarTabla());

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    idSeleccionado = (long) modeloTabla.getValueAt(fila, 0);
                    campNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                    campApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                    campDni.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                    campEmail.setText((String) modeloTabla.getValueAt(fila, 4));

                    try {
                        Paciente p = controlador.buscarPorId(idSeleccionado);
                        if (p.tieneDomicilio()) {
                            campCalle.setText(p.getDomicilio().getCalle());
                            campNumero.setText(String.valueOf(p.getDomicilio().getNumero()));
                            campLocalidad.setText(p.getDomicilio().getLocalidad());
                            campProvincia.setText(p.getDomicilio().getProvincia());
                            combTipoHogar.setSelectedItem(p.getDomicilio().getHogar());
                        }
                    } catch (exception.PacienteNoEncontradoException ex) {
                        JOptionPane.showMessageDialog(null, "Error al cargar paciente");
                    }
                }
            }
        });

        return panel;
    } // <-- ACA CIERRA crearPanelTabla(), todo bien hasta aca

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

        btnGuardar.addActionListener(e -> {
            if (!validarCampos()) return;
            try {
                Domicilio domicilio = new Domicilio(
                        campCalle.getText(),
                        Integer.parseInt(campNumero.getText()),
                        campLocalidad.getText(),
                        campProvincia.getText(),
                        (TipoHogar) combTipoHogar.getSelectedItem()
                );

                if (idSeleccionado == -1) {
                    controlador.registrarPaciente(
                            campNombre.getText(),
                            campApellido.getText(),
                            Integer.parseInt(campDni.getText()),
                            campEmail.getText(),
                            domicilio
                    );
                    JOptionPane.showMessageDialog(null, "Paciente guardado correctamente");
                } else {
                    controlador.actualizarPaciente(
                            idSeleccionado,
                            campNombre.getText(),
                            campApellido.getText(),
                            campEmail.getText(),
                            domicilio
                    );
                    JOptionPane.showMessageDialog(null, "Paciente actualizado correctamente");
                }

                limpiarFormulario();
                refrescarTabla();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "DNI y número de calle deben ser números");
            } catch (exception.DatoInvalidoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            } catch (exception.PacienteNoEncontradoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        btnEliminar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(null, "Seleccioná un paciente de la tabla primero");
                return;
            }
            try {
                controlador.eliminarPaciente(idSeleccionado);
                JOptionPane.showMessageDialog(null, "Paciente eliminado");
                limpiarFormulario();
                refrescarTabla();
            } catch (exception.PacienteNoEncontradoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        return panel;
    } // <-- ACA CIERRA crearPanelFormulario(), todo bien hasta aca

    // ESTE METODO ESTABA MAL PUESTO ADENTRO DE crearPanelFormulario()
    // AHORA ESTA AFUERA, AL MISMO NIVEL QUE LOS OTROS METODOS
    private boolean validarCampos() {
        boolean valido = true;

        javax.swing.border.Border bordeNormal = new JTextField().getBorder();
        javax.swing.border.Border bordeRojo = BorderFactory.createLineBorder(Color.RED, 2);

        if (campNombre.getText().isBlank()) {
            campNombre.setBorder(bordeRojo);
            valido = false;
        } else {
            campNombre.setBorder(bordeNormal);
        }

        if (campApellido.getText().isBlank()) {
            campApellido.setBorder(bordeRojo);
            valido = false;
        } else {
            campApellido.setBorder(bordeNormal);
        }

        try {
            int dni = Integer.parseInt(campDni.getText());
            if (dni <= 0) throw new NumberFormatException();
            campDni.setBorder(bordeNormal);
        } catch (NumberFormatException e) {
            campDni.setBorder(bordeRojo);
            valido = false;
        }

        if (campEmail.getText().isBlank() || !campEmail.getText().contains("@")) {
            campEmail.setBorder(bordeRojo);
            valido = false;
        } else {
            campEmail.setBorder(bordeNormal);
        }

        if (campCalle.getText().isBlank()) {
            campCalle.setBorder(bordeRojo);
            valido = false;
        } else {
            campCalle.setBorder(bordeNormal);
        }

        try {
            Integer.parseInt(campNumero.getText());
            campNumero.setBorder(bordeNormal);
        } catch (NumberFormatException e) {
            campNumero.setBorder(bordeRojo);
            valido = false;
        }

        return valido;
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