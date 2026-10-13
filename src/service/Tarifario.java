package service;

import entity.TipoCirugia;
import entity.TipoConsulta;
import entity.TipoOrtodoncia;
import exception.DatoInvalidoException;

import java.util.HashMap;
import java.util.Map;

// Lista de precios de la clinica: sabe cuanto cuesta cada tratamiento (Information Expert de los precios)
public class Tarifario {

    // clave: nombre del tratamiento (ej "LIMPIEZA"), valor: precio base en pesos
    // se usa el nombre en texto porque Turno guarda el motivo como Object y al recargar desde el archivo queda como String
    private final Map<String, Double> precios = new HashMap<>();

    public Tarifario() {
        cargarPrecios();
    }

    // para agregar un tratamiento nuevo solo se agrega una linea aca
    private void cargarPrecios() {
        precios.put(TipoConsulta.CONTROL.name(), 15000.0);
        precios.put(TipoConsulta.LIMPIEZA.name(), 25000.0);
        precios.put(TipoConsulta.URGENCIA.name(), 40000.0);
        precios.put(TipoConsulta.BLANQUEAMIENTO.name(), 90000.0);

        precios.put(TipoCirugia.EXTRACCION_MUELA.name(), 60000.0);
        precios.put(TipoCirugia.CIRUGIA_ENCIA.name(), 120000.0);
        precios.put(TipoCirugia.IMPLANTE.name(), 450000.0);
        precios.put(TipoCirugia.CIRUGIA_MAXILOFACIAL.name(), 600000.0);

        precios.put(TipoOrtodoncia.BRACKETS_METALICOS.name(), 150000.0);
        precios.put(TipoOrtodoncia.BRACKETS_CERAMICOS.name(), 250000.0);
    }

    // devuelve el precio base del tratamiento, acepta el enum o el texto que guarda el turno
    public double precioDe(Object motivoConsulta) throws DatoInvalidoException {
        if (motivoConsulta == null) {
            throw new DatoInvalidoException("Tratamiento", "El turno no tiene un tratamiento cargado.");
        }

        String tratamiento = motivoConsulta.toString();
        Double precio = precios.get(tratamiento);

        if (precio == null) {
            throw new DatoInvalidoException("Tratamiento", "No hay un precio cargado para '" + tratamiento + "'.");
        }

        return precio;
    }
}