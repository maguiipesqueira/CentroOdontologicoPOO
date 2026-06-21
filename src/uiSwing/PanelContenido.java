package uiSwing;
import controller.ControladorTurno;
import javax.swing.*;
import java.awt.*;
import controller.ControladorPaciente;

public class PanelContenido extends JPanel {

    private CardLayout cardLayout;

    // nombres de cada panel (como etiquetas)
    public static final String AGENDA       = "AGENDA";
    public static final String PACIENTES    = "PACIENTES";
    public static final String ODONTOLOGOS  = "ODONTOLOGOS";
    public static final String TURNOS       = "TURNOS";

    private PanelTurnos panelTurnos;
    private PanelAgenda panelAgenda;
    private PanelPaciente panelPaciente;


    public PanelContenido(ControladorTurno controladorTurno, ControladorPaciente controladorPaciente) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(new Color(248, 247, 244));

        // por ahora agregamos paneles vacíos de placeholder
        // Fati y Tomi van a reemplazar estos con los suyos
        JPanel panelAgendaContenedor = new JPanel(new BorderLayout());
        panelAgendaContenedor.setBackground(new Color(248, 247, 244));

        PanelEstadistica estadisticas = new PanelEstadistica();
        // los valores ya no se cargan a mano: PanelAgenda los calcula solo, en base a los turnos reales

        panelAgenda = new PanelAgenda(controladorTurno, estadisticas);
        PanelLateral lateral = new PanelLateral();
        panelAgendaContenedor.add(lateral, BorderLayout.EAST);

        panelAgendaContenedor.add(estadisticas, BorderLayout.NORTH);
        panelAgendaContenedor.add(panelAgenda, BorderLayout.CENTER); // esto también
        add(panelAgendaContenedor, AGENDA);

        panelAgendaContenedor.add(estadisticas, BorderLayout.NORTH);
        add(panelAgendaContenedor, AGENDA);
        panelPaciente = new PanelPaciente(controladorPaciente);
        add(panelPaciente, PACIENTES);
        add(crearPlaceholder("Odontologos - Magui"),      ODONTOLOGOS);

        panelTurnos = new PanelTurnos(controladorTurno);
        add(panelTurnos, TURNOS);
    }

    // crea un panel gris con un texto en el centro (temporal)
    private JPanel crearPlaceholder(String texto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 247, 244));

        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lbl.setForeground(new Color(148, 163, 184));

        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    // este método lo llama VentanaPrincipal para cambiar de panel
    public void mostrar(String nombre) {
        cardLayout.show(this, nombre);
    }

    // expone el panel de turnos por si hace falta refrescarlo al navegar
    public PanelTurnos getPanelTurnos() {
        return panelTurnos;
    }

    // expone el panel de agenda por si hace falta refrescarlo al navegar
    public PanelAgenda getPanelAgenda() {
        return panelAgenda;
    }
}