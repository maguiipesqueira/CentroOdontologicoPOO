package persistence;

import entity.EstadoTurno;
import entity.MotivoConsulta;
import entity.Odontologo;
import entity.Paciente;
import entity.Turno;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class TurnoCsvMapper implements CsvMapper<Turno> {
    private static final List<String> COLUMNAS = List.of("id", "pacienteId", "odontologoId", "fecha", "hora", "estado", "motivo");
    private final Function<Long, Paciente> pacientes;
    private final Function<Long, Odontologo> odontologos;
    public TurnoCsvMapper(Function<Long, Paciente> pacientes, Function<Long, Odontologo> odontologos) {
        this.pacientes = java.util.Objects.requireNonNull(pacientes);
        this.odontologos = java.util.Objects.requireNonNull(odontologos);
    }
    @Override public List<String> columnas() { return COLUMNAS; }
    @Override public Map<String, String> aColumnas(Turno t) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", Long.toString(t.getId())); m.put("pacienteId", Long.toString(t.getPaciente().getId()));
        m.put("odontologoId", Long.toString(t.getOdontologo().getId())); m.put("fecha", t.getFecha().toString());
        m.put("hora", t.getHora().toString()); m.put("estado", t.getEstado().name());
        m.put("motivo", t.getMotivoConsulta().map(MotivoConsulta::name).orElse("Consulta"));
        return m;
    }
    @Override public Turno desdeColumnas(Map<String, String> m) {
        long pacienteId = Long.parseLong(req(m, "pacienteId"));
        long odontologoId = Long.parseLong(req(m, "odontologoId"));
        Paciente p = pacientes.apply(pacienteId);
        Odontologo o = odontologos.apply(odontologoId);
        if (p == null) throw new IllegalArgumentException("no existe el paciente " + pacienteId);
        if (o == null) throw new IllegalArgumentException("no existe el odontólogo " + odontologoId);
        MotivoConsulta motivo = MotivoConsulta.desdeNombre(m.get("motivo")).orElse(null);
        return new Turno(Long.parseLong(req(m, "id")), p, o, LocalDate.parse(req(m, "fecha")),
                LocalTime.parse(req(m, "hora")), EstadoTurno.valueOf(req(m, "estado")), motivo);
    }
    @Override public Map<String, String> desdeFormatoLegado(List<String> v) {
        if (v.size() != COLUMNAS.size()) throw new IllegalArgumentException("cantidad de columnas de turno inválida: " + v.size());
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < COLUMNAS.size(); i++) m.put(COLUMNAS.get(i), v.get(i));
        return m;
    }
    private static String req(Map<String, String> m, String k) {
        String v = m.get(k); if (v == null || v.isBlank()) throw new IllegalArgumentException("falta la columna " + k); return v;
    }
}
