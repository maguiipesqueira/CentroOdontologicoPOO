package repository;

import entity.Factura;

import java.time.LocalDate;
import java.util.*;

public class RepositorioFactura implements IRepositorio<Factura> {

    private final Map<Long, Factura> almacenamiento = new HashMap<>();

    // guarda la factura con su id como clave
    @Override
    public void guardar(Factura factura) {
        almacenamiento.put(factura.getId(), factura);
    }

    // trae la factura o null
    @Override
    public Factura buscarPorId(Long id) {
        return almacenamiento.get(id);
    }

    // copia de todas en una lista
    @Override
    public List<Factura> listarTodos() {
        return new ArrayList<>(almacenamiento.values());
    }

    // pisa solo si el id ya estaba
    @Override
    public void actualizar(Factura factura) {
        if (almacenamiento.containsKey(factura.getId())) {
            almacenamiento.put(factura.getId(), factura);
        }
    }

    // borra por id
    @Override
    public void eliminar(Long id) {
        almacenamiento.remove(id);
    }

    // true si ese turno ya tiene factura (un turno se cobra una sola vez)
    public boolean existePorTurno(long turnoId) {
        for (Factura f : almacenamiento.values()) {
            if (f.getTurnoId() == turnoId) {
                return true;
            }
        }
        return false;
    }

    // facturas emitidas en una fecha (para el cierre del dia)
    public List<Factura> listarPorFecha(LocalDate fecha) {
        List<Factura> resultado = new ArrayList<>();

        for (Factura f : almacenamiento.values()) {
            if (f.getFecha().equals(fecha)) {
                resultado.add(f);
            }
        }

        return resultado;
    }

    // el proximo numero de factura es el maximo + 1
    public long siguienteId() {
        long max = 0L;

        for (Long id : almacenamiento.keySet()) {
            if (id > max) {
                max = id;
            }
        }

        return max + 1;
    }
}