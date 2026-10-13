package main;

import entity.*;
import exception.DatoInvalidoException;
import service.Tarifario;

// PRUEBA TEMPORAL de la Etapa 3: se corre, se mira la salida y se borra
public class PruebaTarifario {
    public static void main(String[] args) {
        Tarifario t = new Tarifario();
        Object[] motivos = {
                TipoConsulta.CONTROL, TipoConsulta.LIMPIEZA, TipoConsulta.URGENCIA, TipoConsulta.BLANQUEAMIENTO,
                TipoCirugia.EXTRACCION_MUELA, TipoCirugia.CIRUGIA_ENCIA, TipoCirugia.IMPLANTE, TipoCirugia.CIRUGIA_MAXILOFACIAL,
                TipoOrtodoncia.BRACKETS_METALICOS, TipoOrtodoncia.BRACKETS_CERAMICOS,
                "LIMPIEZA", "IMPLANTE", "Consulta", null
        };
        for (Object m : motivos) {
            try {
                System.out.println(m + " -> $" + t.precioDe(m));
            } catch (DatoInvalidoException e) {
                System.out.println(m + " -> ERROR: " + e.getMessage());
            }
        }
    }
}