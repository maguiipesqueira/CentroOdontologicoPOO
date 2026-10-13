package main;

import entity.Factura;
import pago.*;
import repository.*;
import service.*;
import uiSwing.PersistenciaFactura;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

// PRUEBA TEMPORAL de la Etapa 9: guarda facturas en facturas.txt, las vuelve a cargar y compara. Se corre y se borra
public class PruebaPersistenciaFactura {

    public static void main(String[] args) {
        File archivo = new File("facturas.txt");
        if (archivo.exists()) {
            System.out.println("AVISO: ya existe facturas.txt (esta prueba lo pisa y lo borra al final). Hacé una copia si tenés datos reales.");
        }

        List<MedioPago> medios = List.of(new MercadoPago(), new Tarjeta(), new Efectivo());

        // servicio "origen": de aca salen las facturas que se guardan
        ServicioFactura origen = armarServicio(medios);
        LocalDate hoy = LocalDate.now();
        origen.getRepositorio().guardar(new Factura(1L, 10L, 1L, "Fatima Persi", "LIMPIEZA", hoy, "Efectivo", 25000.0, 7500.0, 1750.0, 15750.0));
        origen.getRepositorio().guardar(new Factura(2L, 11L, 2L, "Barea, Tomas", "IMPLANTE", hoy, "Tarjeta", 450000.0, 0.0, 0.0, 450000.0)); // nombre con coma
        origen.getRepositorio().guardar(new Factura(3L, 12L, 3L, "Maria Garcia", "CONTROL", hoy.minusDays(1), "Mercado Pago", 15000.0, 4500.0, 0.0, 10500.0));

        PersistenciaFactura.guardar(origen.listarTodos());

        // le agrego una linea rota al archivo para probar que la saltea
        try (java.io.PrintWriter w = new java.io.PrintWriter(new java.io.FileWriter(archivo, true))) {
            w.println("esto,no,es,una,factura");
        } catch (java.io.IOException e) {
            System.out.println("No pude agregar la linea rota: " + e.getMessage());
        }

        // servicio "destino": vacio, como si se reiniciara la app
        ServicioFactura destino = armarServicio(medios);
        PersistenciaFactura.cargar(destino);

        System.out.println("Facturas cargadas (esperado: 3): " + destino.listarTodos().size());
        for (Factura f : destino.listarTodos()) {
            System.out.println("  " + f);
        }
        System.out.println("Nombre con coma (esperado: 'Barea  Tomas', la coma se reemplaza por espacio): '"
                + destino.getRepositorio().buscarPorId(2L).getPacienteNombre() + "'");
        System.out.println("Proximo numero de factura (esperado: 4): " + destino.getRepositorio().siguienteId());
        System.out.println("Cierre de hoy (esperado: 2 facturas, total 465750.0): " + cierre(destino, hoy));

        archivo.delete();
    }

    private static ServicioFactura armarServicio(List<MedioPago> medios) {
        return new ServicioFactura(new RepositorioFactura(), new ServicioTurno(new RepositorioTurno()),
                new Tarifario(), new CalculadorMonto(), medios);
    }

    private static String cierre(ServicioFactura servicio, LocalDate fecha) {
        try {
            return servicio.cerrarDia(fecha).toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}