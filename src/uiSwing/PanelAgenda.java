package uiSwing;

import controller.ControladorTurno;
import entity.EstadoTurno;
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
    private static final String[] HORAS = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00"};
    private static final String[] NOMBRES_DIA = {"Lun", "Mar", "Mie", "Jue", "Vie"};
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd");

    private final ControladorTurno controlador;
    private final PanelEstadistica estadisticas;
    private JPanel grilla;
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
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(DayOfWeek.MONDAY);

        if (hoy.getDayOfWeek() == DayOfWeek.SATURDAY || hoy.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lunes = lunes.plusWeeks(1);
        }

        diasSemana = new LocalDate[5];
        for (int i = 0; i < 5; i++) {
            diasSemana[i] = lunes.plusDays(i);
        }
    }

    // arma la grilla completa en base a diasSemana y a los turnos reales del controlador
    private void construirGrilla() {
        if (grilla != null) {
            remove(grilla);
        }

        grilla = new JPanel(new GridLayout(HORAS.length + 1, diasSemana.length + 1));
        grilla.setBackground(Color.WHITE);
        grilla.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        List<Turno> turnos = controlador.listarTodos();

        // primera fila: encabezados de días (vacio + 5 dias con nombre y fecha)
        grilla.add(crearEncabezado(""));
        for (int i = 0; i < diasSemana.length; i++) {
            LocalDate dia = diasSemana[i];
            String texto = NOMBRES_DIA[i] + " " + dia.format(FORMATO_DIA);
            grilla.add(crearEncabezado(texto));
        }

        // filas de horas
        for (String horaTexto : HORAS) {
            JLabel lblHora = new JLabel(horaTexto, SwingConstants.CENTER);
            lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblHora.setForeground(new Color(148, 163, 184));
            lblHora.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));
            lblHora.setOpaque(true);
            lblHora.setBackground(new Color(248, 250, 252));
            grilla.add(lblHora);

            LocalTime hora = LocalTime.parse(horaTexto);

            for (LocalDate dia : diasSemana) {
                JPanel celda = new JPanel(new BorderLayout());
                celda.setBackground(Color.WHITE);
                celda.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(226, 232, 240)));

                Turno turnoEnCelda = buscarTurno(turnos, dia, hora);
                if (turnoEnCelda != null) {
                    celda.add(crearBloque(turnoEnCelda));
                }

                grilla.add(celda);
            }
        }

        add(grilla, BorderLayout.CENTER);
    }

    // busca, entre los turnos cargados, el que coincide exactamente con esa fecha y hora
    private Turno buscarTurno(List<Turno> turnos, LocalDate dia, LocalTime hora) {
        for (Turno t : turnos) {
            if (t.getFecha().equals(dia) && t.getHora().equals(hora)) {
                return t;
            }
        }
        return null;
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
        Color fondo = obtenerColorFondo(turno.getEstado());
        Color borde = obtenerColorBorde(turno.getEstado());

        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setBackground(fondo);
        bloque.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, borde));

        String nombre = turno.getPaciente().getApellido();

        JLabel lblNombre = new JLabel(" " + nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNombre.setForeground(new Color(15, 23, 42));

        JLabel lblTipo = new JLabel(" " + turno.getMotivoConsultaTexto());
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTipo.setForeground(new Color(71, 85, 105));

        bloque.add(lblNombre);
        bloque.add(lblTipo);

        return bloque;
    }

    // color de fondo del bloque segun el estado del turno (misma paleta que EstadoTurnoCellRenderer)
    private Color obtenerColorFondo(EstadoTurno estado) {
        if (estado == EstadoTurno.PENDIENTE) {
            return new Color(254, 243, 199);
        } else if (estado == EstadoTurno.CONFIRMADO) {
            return new Color(224, 242, 254);
        } else if (estado == EstadoTurno.COMPLETADO) {
            return new Color(209, 250, 229);
        } else if (estado == EstadoTurno.CANCELADO) {
            return new Color(255, 228, 230);
        } else {
            // URGENTE
            return new Color(237, 233, 254);
        }
    }

    // color del borde izquierdo del bloque segun el estado del turno
    private Color obtenerColorBorde(EstadoTurno estado) {
        if (estado == EstadoTurno.PENDIENTE) {
            return new Color(245, 158, 11);
        } else if (estado == EstadoTurno.CONFIRMADO) {
            return new Color(14, 165, 233);
        } else if (estado == EstadoTurno.COMPLETADO) {
            return new Color(5, 150, 105);
        } else if (estado == EstadoTurno.CANCELADO) {
            return new Color(190, 18, 60);
        } else {
            // URGENTE
            return new Color(124, 58, 237);
        }
    }

    // cuenta los turnos de la semana visible y actualiza las tarjetas de arriba (Turnos hoy / Pendientes / Confirmados / Completados / Cancelados)
    private void actualizarEstadisticas() {
        List<Turno> turnos = controlador.listarTodos();

        int totalEnSemana = 0;
        int pendientesEnSemana = 0;
        int confirmadosEnSemana = 0;
        int completadosEnSemana = 0;
        int canceladosEnSemana = 0;

        for (Turno t : turnos) {
            if (esFechaDeLaSemanaVisible(t.getFecha())) {
                totalEnSemana = totalEnSemana + 1;

                if (t.getEstado() == EstadoTurno.PENDIENTE) {
                    pendientesEnSemana = pendientesEnSemana + 1;
                } else if (t.getEstado() == EstadoTurno.CONFIRMADO) {
                    confirmadosEnSemana = confirmadosEnSemana + 1;
                } else if (t.getEstado() == EstadoTurno.COMPLETADO) {
                    completadosEnSemana = completadosEnSemana + 1;
                } else if (t.getEstado() == EstadoTurno.CANCELADO) {
                    canceladosEnSemana = canceladosEnSemana + 1;
                }
            }
        }

        if (estadisticas != null) {
            estadisticas.setTurnos(totalEnSemana);
            estadisticas.setPendientes(pendientesEnSemana);
            estadisticas.setConfirmados(confirmadosEnSemana);
            estadisticas.setCompletados(completadosEnSemana);
            estadisticas.setCancelados(canceladosEnSemana);
        }
    }

    // true si la fecha pasada coincide con alguno de los 5 dias mostrados en la grilla
    private boolean esFechaDeLaSemanaVisible(LocalDate fecha) {
        for (LocalDate dia : diasSemana) {
            if (dia.equals(fecha)) {
                return true;
            }
        }
        return false;
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