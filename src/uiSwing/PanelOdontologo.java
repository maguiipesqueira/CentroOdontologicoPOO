package uiSwing;

import controller.ControladorOdontologo;
import controller.DatosOdontologo;
import entity.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelOdontologo extends JPanel {
    private ControladorOdontologo controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campNombre, campApellido, campMatricula;
    private JComboBox<Especialidad> combTipo;
    private long idSeleccionado = -1;

    public PanelOdontologo(ControladorOdontologo controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Nombre", "Apellido", "Matrícula", "Especialidad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        campNombre = new JTextField();
        campApellido = new JTextField();
        campMatricula = new JTextField();
        combTipo = new JComboBox<>(Especialidad.values());

        form.add(new JLabel("Nombre:")); form.add(campNombre);
        form.add(new JLabel("Apellido:")); form.add(campApellido);
        form.add(new JLabel("Matrícula:")); form.add(campMatricula);
        form.add(new JLabel("Tipo:")); form.add(combTipo);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar"); // NUEVO BOTÓN

        form.add(btnGuardar);
        form.add(btnEliminar);
        form.add(btnLimpiar); // LO AGREGAMOS AL PANEL
        add(form, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario()); // ACCIÓN DEL BOTÓN

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    idSeleccionado = (long) modeloTabla.getValueAt(fila, 0);
                    campNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                    campApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                    campMatricula.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                }
            }
        });

        refrescarTabla();
    }

    private void guardar() {
        try {
            int mat = Integer.parseInt(campMatricula.getText());

            if (idSeleccionado == -1) {
                Especialidad especialidad = (Especialidad) combTipo.getSelectedItem();
                controlador.registrarOdontologo(new DatosOdontologo(
                        campNombre.getText(), campApellido.getText(), mat, especialidad, null));
                JOptionPane.showMessageDialog(this, "Odontólogo guardado correctamente");
            } else {
                controlador.actualizarOdontologo(idSeleccionado, campNombre.getText(), campApellido.getText());
                JOptionPane.showMessageDialog(this, "Odontólogo actualizado correctamente");
            }
            limpiarFormulario();
            refrescarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique los datos ingresados: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un odontólogo de la tabla primero");
            return;
        }
        try {
            controlador.eliminarOdontologo(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Odontólogo eliminado");
            limpiarFormulario();
            refrescarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }

    // NUEVO: Método para vaciar los campos de texto
    private void limpiarFormulario() {
        idSeleccionado = -1;
        campNombre.setText("");
        campApellido.setText("");
        campMatricula.setText("");
        combTipo.setSelectedIndex(0);
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Odontologo o : controlador.listarTodos()) {
            modeloTabla.addRow(new Object[]{o.getId(), o.getNombre(), o.getApellido(), o.getMatricula(),
                    o.getEspecialidad().getNombre()});
        }
    }
}