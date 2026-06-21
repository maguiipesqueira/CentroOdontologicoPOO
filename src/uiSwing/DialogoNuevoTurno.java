package uiSwing;

import controller.ControladorTurno;
import entity.Cirujano;
import entity.General;
import entity.Odontologo;
import entity.Ortodoncista;
import entity.Paciente;
import entity.TipoCirugia;
import entity.TipoConsulta;
import entity.TipoOrtodoncia;
import exception.ClinicaException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

// dialogo de nuevo turno, version con combos en vez de ids sueltos
// misma estetica que BotonNuevoTurno
public class DialogoNuevoTurno extends JDialog {

    private JComboBox<Paciente> comboPaciente;
    private JComboBox<Odontologo> comboOdontologo;
    private JComboBox<Object> comboMotivo;
    private JTextField txtFecha;
    private JTextField txtHora;
    private final ControladorTurno controlador;
    private final Runnable alGuardar; // callback para refrescar el panel que abrio el dialogo

    public DialogoNuevoTurno(JFrame padre, ControladorTurno controlador, Runnable alGuardar) {
        super(padre, "Nuevo Turno", true);
        this.controlador = controlador;
        this.alGuardar = alGuardar;

        setSize(380, 340);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        setResizable(false);

        // panel del formulario
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(crearLabel("Paciente:"));
        comboPaciente = new JComboBox<>();
        comboPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboPaciente.setRenderer(new PacienteRenderer());
        cargarPacientes();
        form.add(comboPaciente);

        form.add(crearLabel("Odontologo:"));
        comboOdontologo = new JComboBox<>();
        comboOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboOdontologo.setRenderer(new OdontologoRenderer());
        cargarOdontologos();
        form.add(comboOdontologo);

        form.add(crearLabel("Tipo de consulta:"));
        comboMotivo = new JComboBox<>();
        comboMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(comboMotivo);

        form.add(crearLabel("Fecha:"));
        txtFecha = crearCampo("Ej: 2026-06-20");
        form.add(txtFecha);

        form.add(crearLabel("Hora:"));
        txtHora = crearCampo("Ej: 09:00");
        form.add(txtHora);

        // cada vez que cambia el odontologo elegido, se recalculan las opciones del combo de motivo
        comboOdontologo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                actualizarComboMotivo();
            }
        });
        actualizarComboMotivo(); // carga inicial segun el primer odontologo de la lista

        // botones
        JPanel botones = new JPanel(new GridLayout(1, 2, 8, 0));
        botones.setBackground(Color.WHITE);
        botones.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(14, 165, 233));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(241, 245, 249));
        btnCancelar.setForeground(new Color(100, 116, 139));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnGuardar.addActionListener(e -> guardarTurno());
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void cargarPacientes() {
        List<Paciente> pacientes = controlador.listarPacientes();
        for (Paciente p : pacientes) {
            comboPaciente.addItem(p);
        }
    }

    private void cargarOdontologos() {
        List<Odontologo> odontologos = controlador.listarOdontologos();
        for (Odontologo o : odontologos) {
            comboOdontologo.addItem(o);
        }
    }

    // segun la subclase del odontologo seleccionado, carga el enum correspondiente en el combo de motivo
    private void actualizarComboMotivo() {
        comboMotivo.removeAllItems();

        // primera opcion siempre visible, asi el combo no arranca mostrando un tipo concreto
        comboMotivo.addItem("Tipo");

        Odontologo odontologo = (Odontologo) comboOdontologo.getSelectedItem();

        if (odontologo instanceof General) {
            for (TipoConsulta tipo : TipoConsulta.values()) {
                comboMotivo.addItem(tipo);
            }
        } else if (odontologo instanceof Cirujano) {
            for (TipoCirugia tipo : TipoCirugia.values()) {
                comboMotivo.addItem(tipo);
            }
        } else if (odontologo instanceof Ortodoncista) {
            for (TipoOrtodoncia tipo : TipoOrtodoncia.values()) {
                comboMotivo.addItem(tipo);
            }
        }
        // si no hay odontologo seleccionado o es de un tipo no contemplado, solo queda la opcion "Tipo"
    }

    private void guardarTurno() {
        Paciente paciente = (Paciente) comboPaciente.getSelectedItem();
        Odontologo odontologo = (Odontologo) comboOdontologo.getSelectedItem();
        Object motivo = comboMotivo.getSelectedItem();

        if (paciente == null || odontologo == null) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos un paciente y un odontologo registrados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // si el usuario dejo la opcion por defecto "Tipo", todavia no eligio un motivo real
        if (motivo == null || motivo.equals("Tipo")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de consulta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());

            controlador.registrarTurno(paciente.getId(), odontologo.getId(), fecha, hora, motivo);

            JOptionPane.showMessageDialog(this, "Turno registrado correctamente!");

            if (alGuardar != null) {
                alGuardar.run();
            }

            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Fecha u hora con formato incorrecto.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JTextField crearCampo(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setToolTipText(placeholder);
        return txt;
    }

    // muestra nombre completo + dni en el combo de pacientes
    private static class PacienteRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean seleccionado, boolean conFoco) {
            String texto = "";
            if (valor instanceof Paciente) {
                Paciente p = (Paciente) valor;
                texto = p.getNombreCompleto() + " (DNI " + p.getDni() + ")";
            }
            return super.getListCellRendererComponent(lista, texto, indice, seleccionado, conFoco);
        }
    }

    // muestra nombre completo + matricula en el combo de odontologos
    private static class OdontologoRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean seleccionado, boolean conFoco) {
            String texto = "";
            if (valor instanceof Odontologo) {
                Odontologo o = (Odontologo) valor;
                texto = o.getNombreCompleto() + " (Mat. " + o.getMatricula() + ")";
            }
            return super.getListCellRendererComponent(lista, texto, indice, seleccionado, conFoco);
        }
    }
}