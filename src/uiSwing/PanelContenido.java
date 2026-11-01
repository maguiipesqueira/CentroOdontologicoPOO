package uiSwing;

import controller.ControladorFactura;
import controller.ControladorOdontologo;
import controller.ControladorPaciente;
import controller.ControladorTurno;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** Contenedor de secciones; coordina actualizaciones mediante eventos Observer. */
public class PanelContenido extends JPanel {

    private final CardLayout cardLayout;
    private final PanelTurnos panelTurnos;
    private final PanelAgenda panelAgenda;
    private final PanelLateral panelLateral;
    private final PanelFacturacion panelFacturacion;

    private final PropertyChangeListener listenerActualizacionTurnos = this::alActualizarTurnos;

    public PanelContenido(ControladorTurno controladorTurno,
                          ControladorPaciente controladorPaciente,
                          ControladorOdontologo controladorOdontologo,
                          ControladorFactura controladorFactura) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(new Color(248, 247, 244));

        panelTurnos = new PanelTurnos(controladorTurno);

        JPanel panelAgendaContenedor = new JPanel(new BorderLayout());
        panelAgendaContenedor.setBackground(new Color(248, 247, 244));

        PanelEstadistica estadisticas = new PanelEstadistica();
        panelAgenda = new PanelAgenda(controladorTurno, estadisticas);
        panelLateral = new PanelLateral(controladorTurno);
        panelLateral.addPropertyChangeListener("turnosActualizados", listenerActualizacionTurnos);

        panelAgendaContenedor.add(panelLateral, BorderLayout.EAST);
        panelAgendaContenedor.add(estadisticas, BorderLayout.NORTH);
        panelAgendaContenedor.add(panelAgenda, BorderLayout.CENTER);
        add(panelAgendaContenedor, SeccionContenido.AGENDA.claveTarjeta());

        add(new PanelPaciente(controladorPaciente), SeccionContenido.PACIENTES.claveTarjeta());
        add(new PanelOdontologo(controladorOdontologo), SeccionContenido.ODONTOLOGOS.claveTarjeta());
        add(panelTurnos, SeccionContenido.TURNOS.claveTarjeta());

        panelFacturacion = new PanelFacturacion(controladorFactura);
        add(panelFacturacion, SeccionContenido.FACTURACION.claveTarjeta());
    }

    private void alActualizarTurnos(PropertyChangeEvent evento) {
        panelAgenda.refrescar();
        panelTurnos.refrescarTabla();
        firePropertyChange("datosActualizados", false, true);
    }

    public void mostrar(SeccionContenido seccion) {
        if (seccion == null) throw new IllegalArgumentException("La sección es obligatoria.");
        cardLayout.show(this, seccion.claveTarjeta());
    }

    public PanelTurnos getPanelTurnos() { return panelTurnos; }
    public PanelAgenda getPanelAgenda() { return panelAgenda; }
    public PanelFacturacion getPanelFacturacion() { return panelFacturacion; }
    public PanelLateral getPanelLateral() { return panelLateral; }
}
