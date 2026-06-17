package uiSwing;
import javax.swing.*;
import java.awt.*;

public class PanelContenido extends JPanel {

    private CardLayout cardLayout;

    // nombres de cada panel (como etiquetas)
    public static final String AGENDA       = "AGENDA";
    public static final String PACIENTES    = "PACIENTES";
    public static final String ODONTOLOGOS  = "ODONTOLOGOS";

    public PanelContenido() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(new Color(248, 247, 244));

        // por ahora agregamos paneles vacíos de placeholder
        // Fati y Tomi van a reemplazar estos con los suyos
        JPanel panelAgenda = new JPanel(new BorderLayout());
        panelAgenda.setBackground(new Color(248, 247, 244));

        PanelEstadistica estadisticas = new PanelEstadistica();
        estadisticas.setTurnos(8);
        estadisticas.setConfirmados(5);
        estadisticas.setCancelados(1);

        PanelAgenda agenda = new PanelAgenda();
        PanelLateral lateral = new PanelLateral();
        panelAgenda.add(lateral, BorderLayout.EAST);

        panelAgenda.add(estadisticas, BorderLayout.NORTH);
        panelAgenda.add(agenda, BorderLayout.CENTER); // esto también
        add(panelAgenda, AGENDA);

        panelAgenda.add(estadisticas, BorderLayout.NORTH);
        add(panelAgenda, AGENDA);
        add(crearPlaceholder("Pacientes - Fati"),         PACIENTES);
        add(crearPlaceholder("Odontologos - Magui"),      ODONTOLOGOS);
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
}
