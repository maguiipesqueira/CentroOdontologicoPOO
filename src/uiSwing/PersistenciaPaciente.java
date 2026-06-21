package uiSwing;

import entity.Domicilio;
import entity.Paciente;
import entity.TipoHogar;
import service.ServicioPaciente;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class PersistenciaPaciente {

    // nombre del archivo donde se guardan los pacientes
    private static final String ARCHIVO = "pacientes.txt";

    // guarda todos los pacientes en el archivo, un paciente por linea
    public static void guardar(List<Paciente> pacientes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Paciente p : pacientes) {
                // cada campo separado por coma
                String linea = p.getId() + "," +
                        p.getNombre() + "," +
                        p.getApellido() + "," +
                        p.getDni() + "," +
                        p.getEmail() + "," +
                        p.getFechaAlta();

                // si tiene domicilio lo agrega al final
                if (p.tieneDomicilio()) {
                    linea += "," + p.getDomicilio().getCalle() + "," +
                            p.getDomicilio().getNumero() + "," +
                            p.getDomicilio().getLocalidad() + "," +
                            p.getDomicilio().getProvincia() + "," +
                            p.getDomicilio().getHogar();
                }

                writer.println(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar pacientes: " + e.getMessage());
        }
    }

    // carga los pacientes desde el archivo y los registra en el servicio
    public static void cargar(ServicioPaciente servicio) {
        File archivo = new File(ARCHIVO);

        // si no existe el archivo no hace nada
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    String[] partes = linea.split(",");

                    long id = Long.parseLong(partes[0]);
                    String nombre = partes[1];
                    String apellido = partes[2];
                    int dni = Integer.parseInt(partes[3]);
                    String email = partes[4];
                    LocalDate fechaAlta = LocalDate.parse(partes[5]);

                    Domicilio domicilio = null;
                    if (partes.length > 6) {
                        String calle = partes[6];
                        int numero = Integer.parseInt(partes[7]);
                        String localidad = partes[8];
                        String provincia = partes[9];
                        TipoHogar hogar = TipoHogar.valueOf(partes[10]);
                        domicilio = new Domicilio(calle, numero, localidad, provincia, hogar);
                    }

                    Paciente p = new Paciente(id, nombre, apellido, dni, email, fechaAlta, domicilio);
                    servicio.getRepositorio().guardar(p);

                } catch (Exception e) {
                    // si una linea esta corrupta la saltea y sigue
                    System.out.println("Linea corrupta ignorada: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar pacientes: " + e.getMessage());
        }
    }
}