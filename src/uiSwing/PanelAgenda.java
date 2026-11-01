package uiSwing;

import controller.ControladorTurno;
import entity.Turno;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelAgenda extends JPanel {

    private static final Color FONDO = new Color(248, 247, 244);
    private static final int ALTO_FILA = 46; // alto minimo de cada franja horaria (la agenda se desplaza si no entra)
    private static final String[] NOMBRES_DIA = {"Lun", "Mar", "Mie", "Jue", "Vie"};
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd");

    private final ControladorTurno controlador;
    private final PanelEstadistica estadisticas;
    private JPanel grilla;
    private JScrollPane desplazamiento;
    private LocalDate[] diasSemana; // lunes a viernes de la semana actual

    public PanelAgenda(ControladorTurno controlador, PanelEstadistica estadisticas) {
        this.controlador = controlador;
        this.estadisticas = estadisticas;

        setLayout(new BorderLayout());
        setBackground(FONDO);

        calcularSemanaActual();
        construirGrilla();
        actualizarEstadisticas();
    }

    // calcula las fechas de lunes a viernes a mostrar.
    // si hoy es sabado o domingo, salta directamente a la semana siguiente (lunes a viernes proximos),
    // porque la semana actual ya termino y no tendria turnos visibles (no se permiten turnos en fechas pasadas)
    private void calcularSemanaActual() {
        LocalDate hoy = controlador.hoy();
        LocalDate lunes = hoy.with(DayOfWeek.MONDAY);

        if (hoy.getDayOfWeek() == DayOfWeek.SATURDAY || hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lunes = lunes.plusWeeks(1);
        }

        diasSemana = new LocalDate[5];
        for (int i = 0; i < 5; i++) {
            diasSemana[i] = lunes.plusDays(i);
        }
    }

    // arma la grilla completa en base a diasSemana, al horario de atencion y a los turnos reales del controlador
    private void construirGrilla() {
        if (desplazamiento != null) {
            remove(desplazamiento);
        }
        List<LocalTime> horas = controlador.horasDeAgenda();
        grilla = new JPanel(new GridLayout(horas.size() + 1, diasSemana.length + 1));
        grilla.setBackground(Color.WHITE);
        grilla.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        grilla.setPreferredSize(new Dimension(0, (horas.size() + 1) * ALTO_FILA));
        List<Turno> turnos = controlador.listarTodos();

        // primera fila: encabezados de días (vacio + 5 dias con nombre y fecha)
        grilla.add(crearEncabezado(""));
        for (int i = 0; i < diasSemana.length; i++) {
            LocalDate dia = diasSemana[i];
            String texto = NOMBRES_DIA[i] + " " + dia.format(FORMATO_DIA);
            grilla.add(crearEncabezado(texto));
        }

        // una fila por cada hora en punto del horario de atencion
        for (LocalTime hora : horas) {
            JLabel lblHora = new JLabel(hora.toString(), SwingConstants.CENTER);
            lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblHora.setForeground(new Color(148, 163, 184));
            lblHora.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));
            lblHora.setOpaque(true);
            lblHora.setBackground(new Color(248, 250, 252));
            grilla.add(lblHora);

            for (LocalDate dia : diasSemana) {
                JPanel celda = new JPanel(new GridLayout(0, 1));
                celda.setBackground(Color.WHITE);
                celda.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));
                // todos los turnos de esa franja (puede haber mas de uno: distintos odontologos o minutos)
                for (Turno turno : buscarTurnos(turnos, dia, hora)) {
                    celda.add(crearBloque(turno));
                }
                grilla.add(celda);
            }
        }

        desplazamiento = new JScrollPane(grilla);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        add(desplazamiento, BorderLayout.CENTER);
    }

    // turnos de ese dia cuya hora cae dentro de la franja [hora, hora + 1h), ordenados por hora
    private List<Turno> buscarTurnos(List<Turno> turnos, LocalDate dia, LocalTime franja) {
        return turnos.stream()
                .filter(t -> t.getFecha().equals(dia) && t.getHora().getHour() == franja.getHour())
                .sorted(java.util.Comparator.comparing(Turno::getHora))
                .toList();
    }

    private JLabel crearEncabezado(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(248, 250, 252));
        return lbl;
    }

    // crea el bloquecito de color dentro de la celda, con apellido del paciente y motivo de consulta
    private JPanel crearBloque(Turno turno) {
        Color fondo = PaletaEstadoTurno.fondo(turno.getEstado());
        Color borde = PaletaEstadoTurno.borde(turno.getEstado());

        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setBackground(fondo);
        bloque.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, borde));

        // si el turno no empieza en punto (ej. 19:30) se muestra la hora para distinguirlo dentro de la franja
        String prefijoHora = turno.getHora().getMinute() == 0 ? "" : turno.getHora() + " ";
        String nombre = turno.getPaciente().getApellido();

        JLabel lblNombre = new JLabel(" " + prefijoHora + nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNombre.setForeground(new Color(15, 23, 42));

        JLabel lblTipo = new JLabel(" " + TextoMotivo.de(turno));
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTipo.setForeground(new Color(71, 85, 105));

        bloque.add(lblNombre);
        bloque.add(lblTipo);

        return bloque;
    }

    // El controlador calcula los indicadores; la vista solo los presenta.
    private void actualizarEstadisticas() {
        if (estadisticas == null || diasSemana.length == 0) return;
        try {
            controller.ResumenSemanalTurnos resumen = controlador.obtenerResumenSemanal(diasSemana[0]);
            estadisticas.setTurnos(resumen.total());
            estadisticas.setPendientes(resumen.pendientes());
            estadisticas.setConfirmados(resumen.confirmados());
            estadisticas.setCompletados(resumen.completados());
            estadisticas.setCancelados(resumen.cancelados());
            estadisticas.setAusentes(resumen.ausentes());
        } catch (exception.DatoInvalidoException ex) {
            throw new IllegalStateException("No se pudo calcular el resumen de la agenda.", ex);
        }
    }

    // se llama al navegar a la seccion Agenda, para traer los turnos mas recientes
    public void refrescar() {
        calcularSemanaActual();
        construirGrilla();
        actualizarEstadisticas();
        revalidate();
        repaint();
    }
}