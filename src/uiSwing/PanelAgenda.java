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
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final int ALTO_FILA = 46; // alto minimo de cada franja horaria (la agenda se desplaza si no entra)
    private static final String[] NOMBRES_DIA = {"Lun", "Mar", "Mie", "Jue", "Vie"};
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_RANGO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ControladorTurno controlador;
    private final PanelEstadistica estadisticas;
    private JPanel grilla;
    private JScrollPane desplazamiento;
    private JLabel lblSemana;
    private LocalDate[] diasSemana; // lunes a viernes de la semana que se esta mostrando

    public PanelAgenda(ControladorTurno controlador, PanelEstadistica estadisticas) {
        this.controlador = controlador;
        this.estadisticas = estadisticas;

        setLayout(new BorderLayout());
        setBackground(FONDO);

        add(crearBarraNavegacion(), BorderLayout.NORTH);

        irAHoy();
    }

    // barra superior para navegar entre semanas: anterior, "Hoy" y siguiente, con el rango visible en el centro
    private JPanel crearBarraNavegacion() {
        JButton btnAnterior = crearBotonNavegacion("< Anterior");
        JButton btnHoy = crearBotonNavegacion("Hoy");
        JButton btnSiguiente = crearBotonNavegacion("Siguiente >");

        btnAnterior.addActionListener(e -> cambiarSemana(-1));
        btnSiguiente.addActionListener(e -> cambiarSemana(1));
        btnHoy.addActionListener(e -> irAHoy());

        lblSemana = new JLabel("", SwingConstants.CENTER);
        lblSemana.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSemana.setForeground(new Color(15, 23, 42));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izquierda.setOpaque(false);
        izquierda.add(btnAnterior);
        izquierda.add(btnHoy);
        izquierda.add(btnSiguiente);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        barra.add(izquierda, BorderLayout.WEST);
        barra.add(lblSemana, BorderLayout.CENTER);
        // espaciador del mismo ancho que los botones para que el rango quede realmente centrado
        barra.add(Box.createHorizontalStrut(izquierda.getPreferredSize().width), BorderLayout.EAST);
        return barra;
    }

    private JButton crearBotonNavegacion(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // lunes de la semana a mostrar "por defecto" (la de hoy).
    // si hoy es sabado o domingo, salta directamente a la semana siguiente (lunes a viernes proximos),
    // porque la semana actual ya termino y no tendria turnos visibles (no se permiten turnos en fechas pasadas)
    private LocalDate lunesPorDefecto() {
        LocalDate hoy = controlador.hoy();
        LocalDate lunes = hoy.with(DayOfWeek.MONDAY);

        if (hoy.getDayOfWeek() == DayOfWeek.SATURDAY || hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lunes = lunes.plusWeeks(1);
        }
        return lunes;
    }

    // fija los cinco dias (lunes a viernes) a partir del lunes indicado
    private void establecerSemana(LocalDate lunes) {
        diasSemana = new LocalDate[5];
        for (int i = 0; i < 5; i++) {
            diasSemana[i] = lunes.plusDays(i);
        }
    }

    // mueve la semana visible hacia adelante (+1) o hacia atras (-1) tantas semanas como se indique
    private void cambiarSemana(int semanas) {
        establecerSemana(diasSemana[0].plusWeeks(semanas));
        redibujar();
    }

    /** Vuelve a la semana actual (o a la proxima si hoy es fin de semana). */
    public void irAHoy() {
        establecerSemana(lunesPorDefecto());
        redibujar();
    }

    /** Muestra la semana que contiene la fecha indicada (por ejemplo, la de un turno recien creado). */
    public void irASemanaDe(LocalDate fecha) {
        if (fecha == null) return;
        establecerSemana(fecha.with(DayOfWeek.MONDAY));
        redibujar();
    }

    private void redibujar() {
        actualizarEtiquetaSemana();
        construirGrilla();
        actualizarEstadisticas();
        revalidate();
        repaint();
    }

    private void actualizarEtiquetaSemana() {
        lblSemana.setText("Semana del " + diasSemana[0].format(FORMATO_RANGO)
                + " al " + diasSemana[diasSemana.length - 1].format(FORMATO_RANGO));
    }

    // arma la grilla completa en base a diasSemana, al horario de atencion y a los turnos reales del controlador
    private void construirGrilla() {
        if (desplazamiento != null) {
            remove(desplazamiento);
        }
        List<LocalTime> horas = controlador.horasDeAgenda();
        grilla = new JPanel(new GridLayout(horas.size() + 1, diasSemana.length + 1));
        grilla.setBackground(Color.WHITE);
        grilla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
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
            lblHora.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, COLOR_BORDE));
            lblHora.setOpaque(true);
            lblHora.setBackground(new Color(248, 250, 252));
            grilla.add(lblHora);

            for (LocalDate dia : diasSemana) {
                JPanel celda = new JPanel(new GridLayout(0, 1));
                celda.setBackground(Color.WHITE);
                celda.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, COLOR_BORDE));
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
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, COLOR_BORDE));
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

    // se llama al navegar a la seccion Agenda o tras un cambio de turnos: vuelve a leer los turnos
    // pero MANTIENE la semana que el usuario esta viendo (antes la reseteaba a la semana actual)
    public void refrescar() {
        redibujar();
    }
}
