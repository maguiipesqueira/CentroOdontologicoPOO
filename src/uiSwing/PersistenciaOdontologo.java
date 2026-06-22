package uiSwing;

import entity.*;
import service.ServicioOdontologo;
import java.io.*;
import java.util.List;

public class PersistenciaOdontologo {
    private static final String ARCHIVO = "odontologos.txt";

    // Escribe la lista de odontólogos en el archivo, separado por comas
    public static void guardar(List<Odontologo> odontologos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Odontologo o : odontologos) {
                String tipo = o.getClass().getSimpleName(); // Obtiene si es General, Cirujano, etc.
                String extra = "";

                // Extrae el valor del enum según la especialidad
                if (o instanceof General g) extra = g.getTipoConsulta().name();
                else if (o instanceof Cirujano c) extra = c.getTipoCirugia().name();
                else if (o instanceof Ortodoncista or) extra = or.getTipoOrtodoncia().name();

                writer.println(o.getId() + "," + tipo + "," + o.getNombre() + "," +
                        o.getApellido() + "," + o.getMatricula() + "," + extra);
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

                Odontologo o = null;
                // Instancia la clase correcta según lo guardado
                if (tipo.equals("General")) o = new General(id, nombre, apellido, mat, TipoConsulta.valueOf(p[5]));
                else if (tipo.equals("Cirujano")) o = new Cirujano(id, nombre, apellido, mat, TipoCirugia.valueOf(p[5]));
                else if (tipo.equals("Ortodoncista")) o = new Ortodoncista(id, nombre, apellido, mat, TipoOrtodoncia.valueOf(p[5]));

                if (o != null) servicio.getRepositorio().guardar(o);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar: " + e.getMessage());
        }
    }
}
