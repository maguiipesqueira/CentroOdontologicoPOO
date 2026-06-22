package uiSwing;

import controller.ControladorTurno;
import entity.Paciente;
import entity.TipoCirugia;
import entity.TipoConsulta;
import entity.TipoOrtodoncia;
import exception.ClinicaException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BotonNuevoTurno extends JDialog {

    private JTextField txtPacienteDni; // Ahora usamos DNI
    private JTextField txtOdontologoId;
    private JComboBox<Object> comboMotivo;
    private JTextField txtFecha;
    private JTextField txtHora;
    private ControladorTurno controlador;

    public BotonNuevoTurno(JFrame padre, ControladorTurno controlador) {
        super(padre, "Nuevo Turno", true);
        this.controlador = controlador;

        setSize(350, 320);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        setResizable(false);

        // panel del formulario
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(crearLabel("DNI Paciente:"));
        txtPacienteDni = crearCampo("Ej: 46113226");
        form.add(txtPacienteDni);

        form.add(crearLabel("ID Odontologo:"));
        txtOdontologoId = crearCampo("Ej: 1");
        form.add(txtOdontologoId);

        form.add(crearLabel("Tipo de consulta:"));
        comboMotivo = new JComboBox<>();
        comboMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboMotivo.addItem("Tipo");

        for (TipoConsulta tipo : TipoConsulta.values()) comboMotivo.addItem(tipo);
        for (TipoCirugia tipo : TipoCirugia.values()) comboMotivo.addItem(tipo);
        for (TipoOrtodoncia tipo : TipoOrtodoncia.values()) comboMotivo.addItem(tipo);
        form.add(comboMotivo);

        // Se cambia el texto de ayuda al nuevo formato
        form.add(crearLabel("Fecha:"));
        txtFecha = crearCampo("Ej: 21-06-2026");
        form.add(txtFecha);

        form.add(crearLabel("Hora:"));
        txtHora = crearCampo("Ej: 09:00");
        form.add(txtHora);

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

    private void guardarTurno() {
        Object motivo = comboMotivo.getSelectedItem();

        if (motivo == null || motivo.equals("Tipo")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de consulta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int pacienteDni = Integer.parseInt(txtPacienteDni.getText().trim());
            long odontologoId = Long.parseLong(txtOdontologoId.getText().trim());

            // Buscar el paciente por DNI iterando la lista
            long pacienteId = -1;
            List<Paciente> pacientes = controlador.listarPacientes();
            for (Paciente p : pacientes) {
                if (p.getDni() == pacienteDni) {
                    pacienteId = p.getId();
                    break;
                }
            }

            if (pacienteId == -1) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún paciente con el DNI ingresado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Aplicar el nuevo formato de fecha dd-MM-yyyy
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim(), formatter);
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());

            controlador.registrarTurno(pacienteId, odontologoId, fecha, hora, motivo);

            JOptionPane.showMessageDialog(this, "Turno registrado correctamente!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El DNI y el ID deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener formato DD-MM-AAAA (ej: 21-06-2026).", "Error", JOptionPane.ERROR_MESSAGE);
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
}