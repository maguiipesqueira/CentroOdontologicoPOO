package repository;

import entity.Factura;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Repositorio de facturas: se pueden emitir y consultar, pero no editar ni borrar. */
public class RepositorioFactura extends RepositorioEnMemoria<Factura> {

    private final Map<Long, Long> idsPorTurno = new HashMap<>();

    @Override
    protected long idDe(Factura factura) {
        return factura.getId();
    }

    @Override
    protected void alGuardar(Factura factura) {
        idsPorTurno.put(factura.getTurnoId(), factura.getId());
    }

    @Override
    protected void alEliminar(Factura factura) {
        Long idIndexado = idsPorTurno.get(factura.getTurnoId());
        if (idIndexado != null && idIndexado == factura.getId()) {
            idsPorTurno.remove(factura.getTurnoId());
        }
    }

    public boolean existePorTurno(long turnoId) {
        return idsPorTurno.containsKey(turnoId);
    }

    public List<Factura> listarPorFecha(LocalDate fecha) {
        return listarTodos().stream()
                .filter(factura -> factura.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }
}
