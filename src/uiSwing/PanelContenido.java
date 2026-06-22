package uiSwing;

import controller.ControladorTurno;
import javax.swing.*;
import java.awt.*;
import controller.ControladorPaciente;
import controller.ControladorOdontologo;

public class PanelContenido extends JPanel {

    private CardLayout cardLayout;

    public static final String AGENDA       = "AGENDA";
    public static final String PACIENTES    = "PACIENTES";
    public static final String ODONTOLOGOS  = "ODONTOLOGOS";
    public static final String TURNOS       = "TURNOS";

    private PanelTurnos panelTurnos;
    private PanelAgenda panelAgenda;
    private PanelPaciente panelPaciente;
    private PanelOdontologo panelOdontologo;
    private PanelLateral panelLateral; // Instanciado a nivel clase


    public PanelContenido(ControladorTurno controladorTurno, ControladorPaciente controladorPaciente, ControladorOdontologo controladorOdontologo) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(new Color(248, 247, 244));

        // Instanciamos el de turnos primero para que el callback lo pueda usar
        panelTurnos = new PanelTurnos(controladorTurno);

        JPanel panelAgendaContenedor = new JPanel(new BorderLayout());
        panelAgendaContenedor.setBackground(new Color(248, 247, 244));

        PanelEstadistica estadisticas = new PanelEstadistica();
        panelAgenda = new PanelAgenda(controladorTurno, estadisticas);

        // Ahora el PanelLateral recibe el controlador y un lambda que refresca todo cuando hay un cambio
        panelLateral = new PanelLateral(controladorTurno, () -> {
            panelAgenda.refrescar();
            panelTurnos.refrescarTabla();
        });

        panelAgendaContenedor.add(panelLateral, BorderLayout.EAST);
        panelAgendaContenedor.add(estadisticas, BorderLayout.NORTH);
        panelAgendaContenedor.add(panelAgenda, BorderLayout.CENTER);
        add(panelAgendaContenedor, AGENDA);

        panelPaciente = new PanelPaciente(controladorPaciente);
        add(panelPaciente, PACIENTES);

        panelOdontologo = new PanelOdontologo(controladorOdontologo);
        add(panelOdontologo, ODONTOLOGOS);

        add(panelTurnos, TURNOS);
    }

    public void mostrar(String nombre) { cardLayout.show(this, nombre); }
    public PanelTurnos getPanelTurnos() { return panelTurnos; }
    public PanelAgenda getPanelAgenda() { return panelAgenda; }

    // NUEVO: Permite acceder al lateral para refrescarlo
    public PanelLateral getPanelLateral() { return panelLateral; }
}