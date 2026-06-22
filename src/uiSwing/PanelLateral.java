package uiSwing;

import controller.ControladorTurno;
import entity.Turno;
import exception.ClinicaException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PanelLateral extends JPanel {

    private ControladorTurno controlador;
    private Runnable alActualizar; // Función para avisarle a la Agenda que se repinte
    private JPanel listaPacientes;
    private JPanel panelDetalle;
    private Turno turnoSeleccionado;

    private JLabel valPaciente, valDni, valTipo, valOdontologo, valEstado;
    private JButton btnAsistio, btnNoAsistio;

    public PanelLateral(ControladorTurno controlador, Runnable alActualizar) {
        this.controlador = controlador;
        this.alActualizar = alActualizar;

        setPreferredSize(new Dimension(230, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)));

        // Título
        JLabel titulo = new JLabel("  Pacientes de hoy");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setPreferredSize(new Dimension(0, 40));
        titulo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Contenedor de la lista de turnos
        listaPacientes = new JPanel();
        listaPacientes.setLayout(new BoxLayout(listaPacientes, BoxLayout.Y_AXIS));
        listaPacientes.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(listaPacientes);
        scroll.setBorder(null);

        // Inicializamos la parte de abajo
        crearPanelDetalle();

        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelDetalle, BorderLayout.SOUTH);

        refrescar();
    }

    // Busca los turnos del día y los dibuja
    public void refrescar() {
        listaPacientes.removeAll();
        LocalDate hoy = LocalDate.now();

        // Trae todos los turnos y filtra los que sean de hoy
        List<Turno> turnosHoy = controlador.listarTodos().stream()
                .filter(t -> t.getFecha().equals(hoy))
                .collect(Collectors.toList());

        if (turnosHoy.isEmpty()) {
            JLabel lblVacio = new JLabel("Sin turnos pendientes");
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblVacio.setForeground(new Color(148, 163, 184));
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            listaPacientes.add(Box.createVerticalStrut(20));
            listaPacientes.add(lblVacio);
            turnoSeleccionado = null;
        } else {
            // Selecciona el primero por defecto si no hay ninguno seleccionado
            if (turnoSeleccionado == null || !turnosHoy.contains(turnoSeleccionado)) {
                turnoSeleccionado = turnosHoy.get(0);
            }
            // Crea un minipanel por cada turno
            for (Turno t : turnosHoy) {
                listaPacientes.add(crearFilaPaciente(t));
            }
        }

        actualizarDetalle();
        listaPacientes.revalidate();
        listaPacientes.repaint();
    }

    // Crea la tarjetita individual de cada paciente en la lista
    private JPanel crearFilaPaciente(Turno turno) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(turno.equals(turnoSeleccionado) ? new Color(241, 245, 249) : Color.WHITE);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(248, 250, 252)));
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Iniciales del paciente para el avatar
        String nombreCompleto = turno.getPaciente().getNombreCompleto();
        String iniciales = nombreCompleto.length() >= 2 ? nombreCompleto.substring(0, 2).toUpperCase() : "PA";

        JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        avatar.setForeground(new Color(12, 74, 110));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(224, 242, 254));
        avatar.setPreferredSize(new Dimension(34, 34));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        izquierda.setOpaque(false);
        izquierda.add(avatar);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(Box.createVerticalStrut(10));

        JLabel lblNombre = new JLabel(turno.getPaciente().getApellido());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblHorario = new JLabel(turno.getHora().toString() + " - " + turno.getMotivoConsultaTexto());
        lblHorario.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHorario.setForeground(new Color(148, 163, 184));

        textos.add(lblNombre);
        textos.add(lblHorario);
        izquierda.add(textos);

        fila.add(izquierda, BorderLayout.CENTER);

        // Evento de click para seleccionarlo
        fila.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                turnoSeleccionado = turno;
                refrescar(); // Redibuja para pintar el fondo del seleccionado
            }
        });

        return fila;
    }

    // Inicializa los textos de abajo vacíos
    private void crearPanelDetalle() {
        panelDetalle = new JPanel();
        panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
        panelDetalle.setBackground(Color.WHITE);
        panelDetalle.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        panelDetalle.add(Box.createVerticalStrut(10));

        valPaciente = new JLabel("-");
        valDni = new JLabel("-");
        valTipo = new JLabel("-");
        valOdontologo = new JLabel("-");
        valEstado = new JLabel("-");

        panelDetalle.add(crearFilaDetalle("Paciente", valPaciente));
        panelDetalle.add(crearFilaDetalle("DNI", valDni));
        panelDetalle.add(crearFilaDetalle("Tipo", valTipo));
        panelDetalle.add(crearFilaDetalle("Odontólogo", valOdontologo));
        panelDetalle.add(crearFilaDetalle("Estado", valEstado));
        panelDetalle.add(Box.createVerticalStrut(10));

        JPanel botones = new JPanel(new GridLayout(1, 2, 6, 0));
        botones.setBackground(Color.WHITE);
        botones.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        btnAsistio = new JButton("Asistió");
        btnAsistio.setBackground(new Color(14, 165, 233));
        btnAsistio.setForeground(Color.WHITE);
        btnAsistio.setFocusPainted(false);

        btnNoAsistio = new JButton("No asistió");
        btnNoAsistio.setBackground(new Color(255, 228, 230));
        btnNoAsistio.setForeground(new Color(136, 19, 55));
        btnNoAsistio.setFocusPainted(false);

        // Lógica de los botones
        btnAsistio.addActionListener(e -> accionTurno(true));
        btnNoAsistio.addActionListener(e -> accionTurno(false));

        botones.add(btnAsistio);
        botones.add(btnNoAsistio);
        panelDetalle.add(botones);
    }

    private JPanel crearFilaDetalle(String clave, JLabel lblValor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        fila.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel lblClave = new JLabel(clave);
        lblClave.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblClave.setForeground(new Color(100, 116, 139));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(new Color(15, 23, 42));

        fila.add(lblClave, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.EAST);
        return fila;
    }

    // Actualiza los textos de detalle según el turno seleccionado
    private void actualizarDetalle() {
        if (turnoSeleccionado != null) {
            valPaciente.setText(turnoSeleccionado.getPaciente().getApellido());
            valDni.setText(String.valueOf(turnoSeleccionado.getPaciente().getDni()));
            valTipo.setText(turnoSeleccionado.getMotivoConsultaTexto());
            valOdontologo.setText(turnoSeleccionado.getOdontologo().getApellido());
            valEstado.setText(turnoSeleccionado.getEstado().name());

            btnAsistio.setEnabled(true);
            btnNoAsistio.setEnabled(true);
        } else {
            valPaciente.setText("-"); valDni.setText("-"); valTipo.setText("-");
            valOdontologo.setText("-"); valEstado.setText("-");
            btnAsistio.setEnabled(false);
            btnNoAsistio.setEnabled(false);
        }
    }

    // Ejecuta Completar o Cancelar en el controlador y actualiza visualmente
    private void accionTurno(boolean asistio) {
        if (turnoSeleccionado == null) return;
        try {
            if (asistio) controlador.completarTurno(turnoSeleccionado.getId());
            else controlador.cancelarTurno(turnoSeleccionado.getId());

            refrescar(); // Repinta esta barra lateral
            if (alActualizar != null) alActualizar.run(); // Le avisa a la agenda central que se repinte
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }
}