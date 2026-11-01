package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Implementación CSV no estática; la ruta se define al construirla. */
public class RepositorioCsv<T> implements Persistencia<T> {
    private final Path archivo;
    private final CsvMapper<T> mapper;

    public RepositorioCsv(Path archivo, CsvMapper<T> mapper) {
        this.archivo = Objects.requireNonNull(archivo, "La ruta del archivo es obligatoria.").toAbsolutePath().normalize();
        this.mapper = Objects.requireNonNull(mapper, "El mapper CSV es obligatorio.");
    }

    public Path getArchivo() { return archivo; }

    @Override
    public void guardar(List<T> entidades) throws IOException {
        Objects.requireNonNull(entidades, "La lista de entidades es obligatoria.");
        Path padre = archivo.getParent();
        if (padre != null) Files.createDirectories(padre);
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            writer.write(Csv.escribir(mapper.columnas()));
            writer.newLine();
            for (T entidad : entidades) {
                Map<String, String> datos = mapper.aColumnas(entidad);
                List<String> valores = mapper.columnas().stream().map(datos::get).toList();
                writer.write(Csv.escribir(valores));
                writer.newLine();
            }
        }
    }

    @Override
    public ResultadoCarga<T> cargar() throws IOException {
        if (!Files.exists(archivo)) return new ResultadoCarga<>(List.of(), List.of());
        List<T> registros = new ArrayList<>();
        List<ErrorRegistro> errores = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            long numeroLinea = 0;
            boolean primera = true;
            List<String> encabezados = List.of();
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) continue;
                try {
                    List<String> valores = Csv.leer(linea);
                    Map<String, String> datos;
                    if (primera && valores.containsAll(mapper.columnas())) {
                        encabezados = valores;
                        primera = false;
                        continue;
                    }
                    primera = false;
                    if (!encabezados.isEmpty()) {
                        if (valores.size() != encabezados.size())
                            throw new IllegalArgumentException("cantidad de columnas distinta del encabezado");
                        datos = new LinkedHashMap<>();
                        for (int i = 0; i < valores.size(); i++)
                            datos.put(encabezados.get(i), Csv.desescapar(valores.get(i)));
                    } else if (valores.size() == mapper.columnas().size()) {
                        datos = new LinkedHashMap<>();
                        for (int i = 0; i < valores.size(); i++) datos.put(mapper.columnas().get(i), valores.get(i));
                    } else {
                        datos = mapper.desdeFormatoLegado(valores);
                    }
                    registros.add(mapper.desdeColumnas(datos));
                } catch (RuntimeException ex) {
                    errores.add(new ErrorRegistro(numeroLinea,
                            ex.getMessage() == null ? "registro inválido" : ex.getMessage(), linea));
                }
            }
        }
        return new ResultadoCarga<>(registros, errores);
    }
}
