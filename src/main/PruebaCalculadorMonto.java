package main;

import entity.*;
import exception.DatoInvalidoException;
import pago.*;
import service.CalculadorMonto;
import service.DesgloseMonto;
import service.Tarifario;

// PRUEBA TEMPORAL de la Etapa 6: compara el calculo contra la tabla de la consigna. Se corre y se borra
public class PruebaCalculadorMonto {

    public static void main(String[] args) throws DatoInvalidoException {
        Tarifario tarifario = new Tarifario();
        CalculadorMonto calculador = new CalculadorMonto();
        MedioPago tarjeta = new Tarjeta();
        MedioPago efectivo = new Efectivo();

        // tratamiento, base, con obra social, efectivo, obra social + efectivo (tabla de la consigna)
        Object[] tratamientos = {
                TipoConsulta.CONTROL, TipoConsulta.LIMPIEZA, TipoConsulta.URGENCIA, TipoConsulta.BLANQUEAMIENTO,
                TipoCirugia.EXTRACCION_MUELA, TipoCirugia.CIRUGIA_ENCIA, TipoCirugia.IMPLANTE, TipoCirugia.CIRUGIA_MAXILOFACIAL,
                TipoOrtodoncia.BRACKETS_METALICOS, TipoOrtodoncia.BRACKETS_CERAMICOS
        };
        double[][] esperado = {
                {15000, 10500, 13500, 9450},
                {25000, 17500, 22500, 15750},
                {40000, 28000, 36000, 25200},
                {90000, 63000, 81000, 56700},
                {60000, 42000, 54000, 37800},
                {120000, 84000, 108000, 75600},
                {450000, 315000, 405000, 283500},
                {600000, 420000, 540000, 378000},
                {150000, 105000, 135000, 94500},
                {250000, 175000, 225000, 157500}
        };

        int errores = 0;
        for (int i = 0; i < tratamientos.length; i++) {
            double base = tarifario.precioDe(tratamientos[i]);
            double[] obtenido = {
                    calculador.calcular(base, false, tarjeta).getMontoTotal(),
                    calculador.calcular(base, true, tarjeta).getMontoTotal(),
                    calculador.calcular(base, false, efectivo).getMontoTotal(),
                    calculador.calcular(base, true, efectivo).getMontoTotal()
            };
            for (int j = 0; j < 4; j++) {
                if (Math.abs(obtenido[j] - esperado[i][j]) > 0.001) {
                    errores++;
                    System.out.println("MAL: " + tratamientos[i] + " columna " + j + " esperado " + esperado[i][j] + " obtenido " + obtenido[j]);
                }
            }
        }
        System.out.println(errores == 0 ? "OK: las 40 combinaciones coinciden con la tabla de la consigna" : "HAY " + errores + " DIFERENCIAS");

        System.out.println("Desglose Implante + obra social + efectivo: " + calculador.calcular(450000, true, efectivo));

        try {
            calculador.calcular(25000, true, null);
        } catch (DatoInvalidoException e) {
            System.out.println("Sin medio de pago -> ERROR: " + e.getMessage());
        }
        try {
            calculador.calcular(0, true, efectivo);
        } catch (DatoInvalidoException e) {
            System.out.println("Base en cero -> ERROR: " + e.getMessage());
        }
    }
}