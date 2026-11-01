package uiSwing;

import controller.ControladorFactura;
import controller.ControladorOdontologo;
import controller.ControladorPaciente;
import controller.ControladorTurno;
import persistence.PersistenciaClinica;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class VentanaPrincipal extends JFrame {

    private final Sidebar sidebar;
    private final TopBar topBar;
    private final PanelContenido contenido;
    private final ControladorTurno controladorTurno;
    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;
    private final ControladorFactura controladorFactura;
    private final PersistenciaClinica persistencia;

    public VentanaPrincipal(ControladorTurno controladorTurno,
                            ControladorPaciente controladorPaciente,
                            ControladorOdontologo controladorOdontologo,
                            ControladorFactura controladorFactura,
                            PersistenciaClinica persistencia) {
        this.controladorTurno = Objects.requireNonNull(controladorTurno);
        this.controladorPaciente = Objects.requireNonNull(controladorPaciente);
        this.controladorOdontologo = Objects.requireNonNull(controladorOdontologo);
        this.controladorFactura = Objects.requireNonNull(controladorFactura);
        this.persistencia = Objects.requireNonNull(persistencia);

        setTitle("Centro Odontológico - Sistema de Gestión");
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { confirmarCierre(); }
        });

        sidebar = new Sidebar();
        topBar = new TopBar();
        contenido = new PanelContenido(controladorTurno, controladorPaciente,
                controladorOdontologo, controladorFactura);

        add(sidebar, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);

        sidebar.addPropertyChangeListener("seccionSeleccionada", evento ->
                navegarA((SeccionContenido) evento.getNewValue()));
        topBar.getBtnNuevo().addActionListener(e -> abrirNuevoTurno());
        contenido.addPropertyChangeListener("datosActualizados", evento ->
                contenido.getPanelLateral().refrescar());

        navegarA(SeccionContenido.AGENDA);
    }

    private void navegarA(SeccionContenido seccion) {
        contenido.mostrar(seccion);
        topBar.setTitulo(seccion.titulo());
        sidebar.marcarActivo(seccion);
        switch (seccion) {
            case AGENDA -> {
                contenido.getPanelAgenda().refrescar();
                contenido.getPanelLateral().refrescar();
            }
            case TURNOS -> contenido.getPanelTurnos().refrescarTabla();
            case FACTURACION -> contenido.getPanelFacturacion().refrescar();
            case PACIENTES, ODONTOLOGOS -> { /* La sección se actualiza al entrar en su propia pantalla. */ }
        }
    }

    private void abrirNuevoTurno() {
        BotonNuevoTurno dialogo = new BotonNuevoTurno(this, controladorTurno);
        dialogo.setVisible(true);
        contenido.getPanelAgenda().refrescar();
        contenido.getPanelTurnos().refrescarTabla();
        contenido.getPanelLateral().refrescar();
    }

    private void confirmarCierre() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Los datos se guardarán antes de cerrar. ¿Desea salir?",
                "Cerrar aplicación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        try {
            persistencia.guardar(controladorPaciente.listarTodos(), controladorTurno.listarTodos(),
                    controladorOdontologo.listarTodos(), controladorFactura.listarTodos());
            dispose();
            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron guardar los datos: " + ex.getMessage(),
                    "Error de persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void iniciar() { setVisible(true); }
}
