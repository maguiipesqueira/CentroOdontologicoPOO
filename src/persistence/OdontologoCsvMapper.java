package persistence;

import entity.Especialidad;
import entity.Odontologo;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OdontologoCsvMapper implements CsvMapper<Odontologo> {
    private static final List<String> COLUMNAS = List.of("id", "especialidad", "nombre", "apellido", "matricula", "subespecialidad");
    @Override public List<String> columnas() { return COLUMNAS; }
    @Override public Map<String, String> aColumnas(Odontologo o) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", Long.toString(o.getId())); m.put("especialidad", o.getEspecialidad().getNombre());
        m.put("nombre", o.getNombre()); m.put("apellido", o.getApellido());
        m.put("matricula", Integer.toString(o.getMatricula())); m.put("subespecialidad", o.getSubespecialidad().name());
        return m;
    }
    @Override public Odontologo desdeColumnas(Map<String, String> m) {
        String nombreEspecialidad = requerido(m, "especialidad");
        Especialidad e = Especialidad.desdeNombre(nombreEspecialidad)
                .orElseThrow(() -> new IllegalArgumentException("especialidad desconocida: " + nombreEspecialidad));
        return e.crear(Long.parseLong(requerido(m, "id")), requerido(m, "nombre"), requerido(m, "apellido"),
                Integer.parseInt(requerido(m, "matricula")), e.subespecialidadPorNombre(requerido(m, "subespecialidad")));
    }
    @Override public Map<String, String> desdeFormatoLegado(List<String> v) {
        if (v.size() != COLUMNAS.size()) throw new IllegalArgumentException("cantidad de columnas de odontólogo inválida: " + v.size());
        Map<String, String> m = new LinkedHashMap<>();
        // El archivo histórico ya tenía seis campos, con el tipo de especialidad en la segunda columna.
        for (int i = 0; i < COLUMNAS.size(); i++) m.put(COLUMNAS.get(i), v.get(i));
        return m;
    }
    private static String requerido(Map<String, String> m, String clave) {
        String valor = m.get(clave);
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("falta la columna " + clave);
        return valor;
    }
}
