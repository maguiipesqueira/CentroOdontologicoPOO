package persistence;

import entity.Domicilio;
import entity.Paciente;
import entity.TipoHogar;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PacienteCsvMapper implements CsvMapper<Paciente> {
    private static final List<String> COLUMNAS = List.of("id", "nombre", "apellido", "dni", "email", "fechaAlta",
            "tieneObraSocial", "calle", "numero", "localidad", "provincia", "hogar");

    @Override public List<String> columnas() { return COLUMNAS; }

    @Override public Map<String, String> aColumnas(Paciente p) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", Long.toString(p.getId())); m.put("nombre", p.getNombre()); m.put("apellido", p.getApellido());
        m.put("dni", Integer.toString(p.getDni())); m.put("email", p.getEmail());
        m.put("fechaAlta", p.getFechaAlta().toString()); m.put("tieneObraSocial", Boolean.toString(p.tieneObraSocial()));
        if (p.tieneDomicilio()) {
            Domicilio d = p.getDomicilio();
            m.put("calle", d.getCalle()); m.put("numero", Integer.toString(d.getNumero()));
            m.put("localidad", d.getLocalidad()); m.put("provincia", d.getProvincia()); m.put("hogar", d.getHogar().name());
        }
        return m;
    }

    @Override public Paciente desdeColumnas(Map<String, String> m) {
        Domicilio domicilio = null;
        if (noVacio(m.get("calle")) || noVacio(m.get("localidad")) || noVacio(m.get("hogar"))) {
            domicilio = new Domicilio(requerido(m, "calle"), Integer.parseInt(requerido(m, "numero")),
                    requerido(m, "localidad"), requerido(m, "provincia"), TipoHogar.valueOf(requerido(m, "hogar")));
        }
        return new Paciente(Long.parseLong(requerido(m, "id")), requerido(m, "nombre"), requerido(m, "apellido"),
                Integer.parseInt(requerido(m, "dni")), requerido(m, "email"), LocalDate.parse(requerido(m, "fechaAlta")),
                domicilio, Boolean.parseBoolean(m.getOrDefault("tieneObraSocial", "false")));
    }

    @Override public Map<String, String> desdeFormatoLegado(List<String> v) {
        Map<String, String> m = new LinkedHashMap<>();
        if (v.size() != 6 && v.size() != 7 && v.size() != 11 && v.size() != 12)
            throw new IllegalArgumentException("cantidad de columnas de paciente inválida: " + v.size());
        String[] base = {"id", "nombre", "apellido", "dni", "email", "fechaAlta"};
        for (int i = 0; i < base.length; i++) m.put(base[i], v.get(i));
        boolean tieneObraSocial = v.size() == 7 || v.size() == 12;
        int inicio = tieneObraSocial ? 7 : 6;
        m.put("tieneObraSocial", tieneObraSocial ? v.get(6) : "false");
        if (v.size() - inicio == 5) {
            String[] dom = {"calle", "numero", "localidad", "provincia", "hogar"};
            for (int i = 0; i < dom.length; i++) m.put(dom[i], v.get(inicio + i));
        }
        return m;
    }

    private static String requerido(Map<String, String> m, String clave) {
        String valor = m.get(clave);
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("falta la columna " + clave);
        return valor;
    }
    private static boolean noVacio(String s) { return s != null && !s.isBlank(); }
}
