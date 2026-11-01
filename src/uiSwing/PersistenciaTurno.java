package uiSwing;

import entity.*;
import service.ServicioPaciente;
import service.ServicioOdontologo;
import service.ServicioTurno;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PersistenciaTurno {

    private static final String ARCHIVO = "turnos.txt";

    // lo que se escribe en la ultima columna cuando el turno no tiene motivo (formato de siempre)
    private static final String SIN_MOTIVO = "Consulta";

    // guarda todos los turnos en el archivo, uno por linea
    public static void guardar(List<Turno> turnos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Turno t : turnos) {
                String linea = t.getId() + "," +
                        t.getPaciente().getId() + "," +
                        t.getOdontologo().getId() + "," +
                        t.getFecha() + "," +
                        t.getHora() + "," +
                        t.getEstado() + "," +
                        t.getMotivoConsulta().map(MotivoConsulta::name).orElse(SIN_MOTIVO);
                writer.println(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar turnos: " + e.getMessage());
        }
    }

    // carga los turnos desde el archivo
    public static void cargar(ServicioTurno servTurno, ServicioPaciente servPaciente, ServicioOdontologo servOdontologo) {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    String[] partes = linea.split(",");

                    long id = Long.parseLong(partes[0]);
                    long idPaciente = Long.parseLong(partes[1]);
                    long idOdontologo = Long.parseLong(partes[2]);
                    LocalDate fecha = LocalDate.parse(partes[3]);
                    LocalTime hora = LocalTime.parse(partes[4]);
                    EstadoTurno estado = EstadoTurno.valueOf(partes[5]);
                    // un nombre que no es de ningun motivo (ej. "Consulta") deja el turno sin motivo
                    MotivoConsulta motivo = MotivoConsulta.desdeNombre(partes[6]).orElse(null);

                    Paciente paciente = servPaciente.buscarPorId(idPaciente);
                    Odontologo odontologo = servOdontologo.buscarPorId(idOdontologo);

                    Turno turno = new Turno(id, paciente, odontologo, fecha, hora, estado, motivo);

                    servTurno.guardarDesdePersistencia(turno);

                } catch (Exception e) {
                    System.out.println("Linea corrupta ignorada: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar turnos: " + e.getMessage());
        }
    }
}
