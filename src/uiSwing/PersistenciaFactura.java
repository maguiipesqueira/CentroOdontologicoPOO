package uiSwing;

import entity.Factura;
import service.ServicioFactura;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class PersistenciaFactura {

    // nombre del archivo donde se guardan las facturas
    private static final String ARCHIVO = "facturas.txt";

    // cantidad de columnas que tiene cada linea: id,turnoId,pacienteId,pacienteNombre,concepto,fecha,metodo,base,descOS,descEfectivo,total
    private static final int COLUMNAS = 11;

    // guarda todas las facturas en el archivo, una por linea
    public static void guardar(List<Factura> facturas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Factura f : facturas) {
                // cada campo separado por coma; los textos libres se limpian para no romper la linea
                String linea = f.getId() + "," +
                        f.getTurnoId() + "," +
                        f.getPacienteId() + "," +
                        limpiar(f.getPacienteNombre()) + "," +
                        limpiar(f.getConcepto()) + "," +
                        f.getFecha() + "," +
                        limpiar(f.getMetodoPago()) + "," +
                        f.getMontoBase() + "," +
                        f.getDescuentoObraSocial() + "," +
                        f.getDescuentoEfectivo() + "," +
                        f.getMontoTotal();

                writer.println(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar facturas: " + e.getMessage());
        }
    }

    // carga las facturas desde el archivo y las registra en el servicio
    public static void cargar(ServicioFactura servicio) {
        File archivo = new File(ARCHIVO);

        // si no existe el archivo no hace nada
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    // el -1 conserva las columnas vacias del final para detectar lineas incompletas
                    String[] partes = linea.split(",", -1);

                    if (partes.length != COLUMNAS) {
                        throw new IllegalArgumentException("cantidad de columnas incorrecta");
                    }

                    long id = Long.parseLong(partes[0]);
                    long turnoId = Long.parseLong(partes[1]);
                    long pacienteId = Long.parseLong(partes[2]);
                    String pacienteNombre = partes[3];
                    String concepto = partes[4];
                    LocalDate fecha = LocalDate.parse(partes[5]);
                    String metodoPago = partes[6];
                    double montoBase = Double.parseDouble(partes[7]);
                    double descuentoObraSocial = Double.parseDouble(partes[8]);
                    double descuentoEfectivo = Double.parseDouble(partes[9]);
                    double montoTotal = Double.parseDouble(partes[10]);

                    Factura f = new Factura(id, turnoId, pacienteId, pacienteNombre, concepto, fecha,
                            metodoPago, montoBase, descuentoObraSocial, descuentoEfectivo, montoTotal);
                    servicio.getRepositorio().guardar(f);

                } catch (Exception e) {
                    // si una linea esta corrupta la saltea y sigue
                    System.out.println("Linea corrupta ignorada: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar facturas: " + e.getMessage());
        }
    }

    // saca de un texto lo que rompe el formato: la coma (separador) y los saltos de linea se reemplazan por un espacio
    private static String limpiar(String texto) {
        if (texto == null) return "";
        return texto.replace(",", " ").replace("\r", " ").replace("\n", " ").trim();
    }
}