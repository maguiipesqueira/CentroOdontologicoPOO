package uiSwing;

import controller.ControladorTurno;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import controller.ControladorPaciente;

public class VentanaPrincipal extends JFrame {

    private Sidebar sidebar;
    private TopBar topBar;
    private PanelContenido contenido;
    private ControladorTurno controladorTurno;
    private ControladorPaciente controladorPaciente;

    public VentanaPrincipal(ControladorTurno controladorTurno,  ControladorPaciente controladorPaciente) {
        this.controladorTurno = controladorTurno;
        this.controladorPaciente = controladorPaciente;

        setTitle("Centro Odontológico - Sistema de Gestión");
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                int respuesta = JOptionPane.showConfirmDialog(
                        null,
                        "Los datos se guardarán antes de cerrar. ¿Desea salir?",
                        "Cerrar aplicación",
                        JOptionPane.YES_NO_OPTION
                );
                if (respuesta == JOptionPane.YES_OPTION) {
                    PersistenciaPaciente.guardar(controladorPaciente.listarTodos());
                    System.exit(0);
                }
            }
        });

        sidebar   = new Sidebar();
        topBar    = new TopBar();
        contenido = new PanelContenido(controladorTurno, controladorPaciente);

        add(sidebar,   BorderLayout.WEST);
        add(topBar,    BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);

        // navegación sidebar
        sidebar.getBtnAgenda().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                contenido.mostrar(PanelContenido.AGENDA);
                topBar.setTitulo("Agenda semanal");
                sidebar.marcarActivo(sidebar.getBtnAgenda());
                contenido.getPanelAgenda().refrescar();
            }
        });

        sidebar.getBtnPacientes().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                contenido.mostrar(PanelContenido.PACIENTES);
                topBar.setTitulo("Pacientes");
                sidebar.marcarActivo(sidebar.getBtnPacientes());
            }
        });

        sidebar.getBtnOdontologos().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                contenido.mostrar(PanelContenido.ODONTOLOGOS);
                topBar.setTitulo("Odontologos");
                sidebar.marcarActivo(sidebar.getBtnOdontologos());
            }
        });

        sidebar.getBtnTurnos().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                contenido.mostrar(PanelContenido.TURNOS);
                topBar.setTitulo("Turnos");
                sidebar.marcarActivo(sidebar.getBtnTurnos());
                contenido.getPanelTurnos().refrescarTabla();
            }
        });

        // botón nuevo turno
        topBar.getBtnNuevo().addActionListener(e -> {
            BotonNuevoTurno dialog = new BotonNuevoTurno(this, controladorTurno);
            dialog.setVisible(true);
        });
    }

    public void iniciar() {
        setVisible(true);
    }
}