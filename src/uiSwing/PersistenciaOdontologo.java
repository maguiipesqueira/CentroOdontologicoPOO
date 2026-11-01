package uiSwing;

import entity.Especialidad;
import entity.Odontologo;
import service.ServicioOdontologo;
import java.io.*;
import java.util.List;
import java.util.Optional;

public class PersistenciaOdontologo {
    private static final String ARCHIVO = "odontologos.txt";

    // Escribe la lista de odontólogos en el archivo, separado por comas
    public static void guardar(List<Odontologo> odontologos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Odontologo o : odontologos) {
                // cada odontólogo dice su especialidad y su motivo, no hace falta preguntar por la clase
                writer.println(o.getId() + "," + o.getEspecialidad().getNombre() + "," + o.getNombre() + "," +
                        o.getApellido() + "," + o.getMatricula() + "," + o.getSubespecialidad().name());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    // Lee el archivo de texto y reconstruye los objetos
    public static void cargar(ServicioOdontologo servicio) {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] p = linea.split(",");
                long id = Long.parseLong(p[0]);
                String tipo = p[1];
                String nombre = p[2];
                String apellido = p[3];
                int mat = Integer.parseInt(p[4]);

                // una especialidad desconocida se saltea, igual que antes
                Optional<Especialidad> especialidad = Especialidad.desdeNombre(tipo);
                if (especialidad.isPresent()) {
                    Especialidad e = especialidad.get();
                    Odontologo o = e.crear(id, nombre, apellido, mat, e.subespecialidadPorNombre(p[5]));
                    servicio.guardarDesdePersistencia(o);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar: " + e.getMessage());
        }
    }
}
