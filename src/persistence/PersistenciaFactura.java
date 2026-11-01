package persistence;

import entity.Factura;
import java.nio.file.Path;
import java.util.function.Function;

/** Persistencia CSV de facturas, con migración de nombres antiguos de medios de pago. */
public final class PersistenciaFactura extends RepositorioCsv<Factura> {
    public PersistenciaFactura(Path archivo, Function<String, String> resolverMedioPago) {
        super(archivo, new FacturaCsvMapper(resolverMedioPago));
    }
}
