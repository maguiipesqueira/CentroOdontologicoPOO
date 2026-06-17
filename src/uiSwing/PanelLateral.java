package uiSwing;

import javax.swing.*;
import java.awt.*;

public class PanelLateral extends JPanel {

    public PanelLateral() {
        setPreferredSize(new Dimension(220, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)));

        // titulo del panel
        JLabel titulo = new JLabel("  Pacientes del dia");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(new Color(15, 23, 42));
        titulo.setPreferredSize(new Dimension(0, 40));
        titulo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // lista de pacientes
        JPanel listaPacientes = new JPanel();
        listaPacientes.setLayout(new BoxLayout(listaPacientes, BoxLayout.Y_AXIS));
        listaPacientes.setBackground(Color.WHITE);

        listaPacientes.add(crearFilaPaciente("FP", "Fatima Persi",  "09:00 - Limpieza", new Color(224, 242, 254), new Color(12, 74, 110), "Hoy"));
        listaPacientes.add(crearFilaPaciente("TB", "Tomas Barea",   "11:00 - Control",  new Color(237, 233, 254), new Color(59, 7, 100),  "Hoy"));
        listaPacientes.add(crearFilaPaciente("MG", "M. Garcia",     "Mar 09:30",        new Color(209, 250, 229), new Color(6, 78, 59),   "Prox."));

        // panel de detalle abajo
        JPanel detalle = crearDetalle();

        add(titulo,          BorderLayout.NORTH);
        add(listaPacientes,  BorderLayout.CENTER);
        add(detalle,         BorderLayout.SOUTH);
    }

    private JPanel crearFilaPaciente(String iniciales, String nombre, String horario, Color fondoAv, Color textoAv, String badge) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(248, 250, 252)));

        // avatar con iniciales
        JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        avatar.setForeground(textoAv);
        avatar.setOpaque(true);
        avatar.setBackground(fondoAv);
        avatar.setPreferredSize(new Dimension(34, 34));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        izquierda.setBackground(Color.WHITE);
        izquierda.add(avatar);

        // nombre y horario
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(Color.WHITE);
        textos.add(Box.createVerticalStrut(10));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNombre.setForeground(new Color(15, 23, 42));

        JLabel lblHorario = new JLabel(horario);
        lblHorario.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHorario.setForeground(new Color(148, 163, 184));

        textos.add(lblNombre);
        textos.add(lblHorario);
        izquierda.add(textos);

        // badge Hoy / Prox.
        JLabel lblBadge = new JLabel(badge, SwingConstants.CENTER);
        lblBadge.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblBadge.setOpaque(true);
        if (badge.equals("Hoy")) {
            lblBadge.setBackground(new Color(219, 234, 254));
            lblBadge.setForeground(new Color(30, 64, 175));
        } else {
            lblBadge.setBackground(new Color(241, 245, 249));
            lblBadge.setForeground(new Color(100, 116, 139));
        }
        lblBadge.setPreferredSize(new Dimension(40, 20));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 17));
        derecha.setBackground(Color.WHITE);
        derecha.add(lblBadge);

        fila.add(izquierda, BorderLayout.CENTER);
        fila.add(derecha,   BorderLayout.EAST);

        return fila;
    }

    private JPanel crearDetalle() {
        JPanel detalle = new JPanel();
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));
        detalle.setBackground(Color.WHITE);
        detalle.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        detalle.add(Box.createVerticalStrut(10));
        detalle.add(crearFilaDetalle("Paciente",   "Fatima Persi"));
        detalle.add(crearFilaDetalle("DNI",        "46.113.226"));
        detalle.add(crearFilaDetalle("Tipo",       "Limpieza"));
        detalle.add(crearFilaDetalle("Odontologa", "M. Pesqueira"));
        detalle.add(crearFilaDetalle("Estado",     "Confirmado"));
        detalle.add(Box.createVerticalStrut(10));

        // botones
        JPanel botones = new JPanel(new GridLayout(1, 2, 6, 0));
        botones.setBackground(Color.WHITE);
        botones.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnCompletar = new JButton("Completar");
        btnCompletar.setBackground(new Color(14, 165, 233));
        btnCompletar.setForeground(Color.WHITE);
        btnCompletar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCompletar.setBorderPainted(false);
        btnCompletar.setFocusPainted(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(241, 245, 249));
        btnCancelar.setForeground(new Color(100, 116, 139));
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);

        botones.add(btnCompletar);
        botones.add(btnCancelar);

        detalle.add(botones);

        return detalle;
    }

    private JPanel crearFilaDetalle(String clave, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        fila.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel lblClave = new JLabel(clave);
        lblClave.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblClave.setForeground(new Color(100, 116, 139));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValor.setForeground(new Color(15, 23, 42));

        fila.add(lblClave, BorderLayout.WEST);
        fila.add(lblValor, BorderLayout.EAST);

        return fila;
    }
}
