package uiSwing;
import controller.ControladorTurno;
import exception.ClinicaException;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class BotonNuevoTurno extends JDialog{

    private JTextField txtPacienteId;
    private JTextField txtOdontologoId;
    private JTextField txtFecha;
    private JTextField txtHora;
    private ControladorTurno controlador;

    public BotonNuevoTurno(JFrame padre, ControladorTurno controlador) {
        super(padre, "Nuevo Turno", true);
        this.controlador = controlador;

        setSize(350, 280);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        setResizable(false);

        // panel del formulario
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(crearLabel("ID Paciente:"));
        txtPacienteId = crearCampo("Ej: 1");
        form.add(txtPacienteId);

        form.add(crearLabel("ID Odontologo:"));
        txtOdontologoId = crearCampo("Ej: 1");
        form.add(txtOdontologoId);

        form.add(crearLabel("Fecha:"));
        txtFecha = crearCampo("Ej: 2026-06-20");
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

        add(form,    BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void guardarTurno() {
        try {
            long pacienteId    = Long.parseLong(txtPacienteId.getText().trim());
            long odontologoId  = Long.parseLong(txtOdontologoId.getText().trim());
            LocalDate fecha    = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora     = LocalTime.parse(txtHora.getText().trim());

            controlador.registrarTurno(pacienteId, odontologoId, fecha, hora);

            JOptionPane.showMessageDialog(this, "Turno registrado correctamente!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los IDs deben ser numeros.", "Error", JOptionPane.ERROR_MESSAGE);
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
}
