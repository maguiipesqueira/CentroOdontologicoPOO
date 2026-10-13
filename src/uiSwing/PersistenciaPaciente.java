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
                        p.getFechaAlta() + "," +
                        p.tieneObraSocial();

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

                    // formato nuevo: ...,fechaAlta,tieneObraSocial[,calle,numero,localidad,provincia,hogar]
                    // formato viejo (6 u 11 columnas): sin obra social, el domicilio arranca en la columna 6
                    boolean formatoViejo = partes.length == 6 || partes.length == 11;
                    boolean tieneObraSocial = false;
                    int inicioDomicilio = 6;

                    if (!formatoViejo) {
                        tieneObraSocial = Boolean.parseBoolean(partes[6]);
                        inicioDomicilio = 7;
                    }

                    Domicilio domicilio = null;
                    if (partes.length > inicioDomicilio) {
                        String calle = partes[inicioDomicilio];
                        int numero = Integer.parseInt(partes[inicioDomicilio + 1]);
                        String localidad = partes[inicioDomicilio + 2];
                        String provincia = partes[inicioDomicilio + 3];
                        TipoHogar hogar = TipoHogar.valueOf(partes[inicioDomicilio + 4]);
                        domicilio = new Domicilio(calle, numero, localidad, provincia, hogar);
                    }

                    Paciente p = new Paciente(id, nombre, apellido, dni, email, fechaAlta, domicilio, tieneObraSocial);
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