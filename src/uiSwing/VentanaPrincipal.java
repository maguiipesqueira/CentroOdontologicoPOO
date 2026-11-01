package uiSwing;

import controller.ControladorFactura;
import controller.ControladorTurno;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import controller.ControladorPaciente;
import persistence.PersistenciaClinica;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {

private Sidebar sidebar;
private TopBar topBar;
private PanelContenido contenido;
private ControladorTurno controladorTurno;
private ControladorPaciente controladorPaciente;
private controller.ControladorOdontologo controladorOdontologo;
private ControladorFactura controladorFactura;
private final PersistenciaClinica persistencia;

public VentanaPrincipal(ControladorTurno controladorTurno, ControladorPaciente controladorPaciente, controller.ControladorOdontologo controladorOdontologo, ControladorFactura controladorFactura, PersistenciaClinica persistencia) {
    this.controladorTurno = controladorTurno;
    this.controladorPaciente = controladorPaciente;
    this.controladorOdontologo = controladorOdontologo;
    this.controladorFactura = controladorFactura;
    this.persistencia = java.util.Objects.requireNonNull(persistencia);

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
                try {
                    persistencia.guardar(controladorPaciente.listarTodos(), controladorTurno.listarTodos(),
                            controladorOdontologo.listarTodos(), controladorFactura.listarTodos());
                    System.exit(0);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "No se pudieron guardar los datos: " + ex.getMessage(),
                            "Error de persistencia", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    });

    sidebar   = new Sidebar();
    topBar    = new TopBar();
    contenido = new PanelContenido(controladorTurno, controladorPaciente, controladorOdontologo, controladorFactura);

    add(sidebar,   BorderLayout.WEST);
    add(topBar,    BorderLayout.NORTH);
    add(contenido, BorderLayout.CENTER);

    sidebar.getBtnAgenda().addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            contenido.mostrar(PanelContenido.AGENDA);
            topBar.setTitulo("Agenda semanal");
            sidebar.marcarActivo(sidebar.getBtnAgenda());
            contenido.getPanelAgenda().refrescar();
            contenido.getPanelLateral().refrescar(); // Actualizamos lateral al entrar
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

    sidebar.getBtnFacturacion().addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            contenido.mostrar(PanelContenido.FACTURACION);
            topBar.setTitulo("Facturación");
            sidebar.marcarActivo(sidebar.getBtnFacturacion());
            contenido.getPanelFacturacion().refrescar(); // actualizamos los turnos por cobrar al entrar
        }
    });

    // Al crear un turno nuevo, forzamos a refrescar toda la pantalla
    topBar.getBtnNuevo().addActionListener(e -> {
        BotonNuevoTurno dialog = new BotonNuevoTurno(this, controladorTurno);
        dialog.setVisible(true);

        // Cuando se cierra el modal, repintamos todo para que aparezca el nuevo dato
        contenido.getPanelAgenda().refrescar();
        contenido.getPanelTurnos().refrescarTabla();
        contenido.getPanelLateral().refrescar();
    });
}

public void iniciar() {
    setVisible(true);
}
}
