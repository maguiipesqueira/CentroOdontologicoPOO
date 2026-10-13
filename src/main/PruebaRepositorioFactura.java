package main;

import entity.Factura;
import repository.RepositorioFactura;

import java.time.LocalDate;

// PRUEBA TEMPORAL de la Etapa 5: se corre, se mira la salida y se borra
public class PruebaRepositorioFactura {
    public static void main(String[] args) {
        RepositorioFactura repo = new RepositorioFactura();
        LocalDate hoy = LocalDate.now();

        System.out.println("Siguiente id con repo vacio: " + repo.siguienteId());

        Factura f1 = new Factura(repo.siguienteId(), 1L, 1L, "Fatima Persi", "LIMPIEZA", hoy,
                "Efectivo", 25000.0, 7500.0, 1750.0, 15750.0);
        repo.guardar(f1);

        Factura f2 = new Factura(repo.siguienteId(), 2L, 2L, "Tomas Barea", "IMPLANTE", hoy.minusDays(1),
                "Tarjeta", 450000.0, 0.0, 0.0, 450000.0);
        repo.guardar(f2);

        System.out.println("Siguiente id con 2 facturas: " + repo.siguienteId());
        System.out.println("Turno 1 ya cobrado? " + repo.existePorTurno(1L));
        System.out.println("Turno 9 ya cobrado? " + repo.existePorTurno(9L));
        System.out.println("Facturas de hoy: " + repo.listarPorFecha(hoy).size());
        System.out.println("Facturas de ayer: " + repo.listarPorFecha(hoy.minusDays(1)).size());
        System.out.println("Total en repo: " + repo.listarTodos().size());
        System.out.println(repo.buscarPorId(1L));
        System.out.println("Buscar id inexistente: " + repo.buscarPorId(99L));
    }
}