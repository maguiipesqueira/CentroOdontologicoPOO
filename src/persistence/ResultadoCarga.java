package persistence;

import java.util.List;

public record ResultadoCarga<T>(List<T> registros, List<ErrorRegistro> errores) {
    public ResultadoCarga {
        registros = List.copyOf(registros);
        errores = List.copyOf(errores);
    }
}
