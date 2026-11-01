package pruebas;

import entity.*;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;
import exception.PacienteNoEncontradoException;
import exception.TurnoYaReservadoException;
import pago.Efectivo;
import pago.MedioPago;
import pago.MercadoPago;
import pago.Tarjeta;
import repository.RepositorioFactura;
import repository.RepositorioOdontologo;
import repository.RepositorioPaciente;
import repository.RepositorioTurno;
import service.CalculadorMonto;
import service.CierreDelDia;
import service.ServicioFactura;
import service.ServicioOdontologo;
import service.ServicioPaciente;
import service.ServicioTurno;
import service.Tarifario;
import uiSwing.PersistenciaFactura;
import uiSwing.PersistenciaOdontologo;
import uiSwing.PersistenciaPaciente;
import uiSwing.PersistenciaTurno;
import uiSwing.TextoMotivo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ETAPA 14 - Red de seguridad (se actualiza en cada etapa junto con el codigo que cambia).
 * Fija el comportamiento ACTUAL del sistema (sin Swing ni consola) para poder refactorizar con confianza.
 * Las lineas marcadas con "(actual)" describen comportamientos que la bitacora marca como defectos:
 * se prueban tal como funcionan hoy y se actualizan en la etapa que los corrige.
 *
 * IMPORTANTE: las pruebas de persistencia usan los archivos pacientes.txt, odontologos.txt, turnos.txt y
 * facturas.txt de la carpeta de trabajo. Se hace un respaldo al empezar y se restauran al terminar.
 */
public class PruebasBase {

    private static final Verificador v = new Verificador();
    private static final String[] ARCHIVOS = {"pacientes.txt", "odontologos.txt", "turnos.txt", "facturas.txt"};

    public static void main(String[] args) throws Exception {
        Path respaldo = respaldarArchivos();
        try {
            v.seccion("Entidades: enums y Domicilio (Etapa 15)", PruebasBase::enumsYDomicilio);
            v.seccion("Entidades: personas (Etapa 16)", PruebasBase::personas);
            v.seccion("Entidades: turno y factura (Etapa 17)", PruebasBase::turnoYFactura);
            v.seccion("Pacientes (servicio)", PruebasBase::pacientes);
            v.seccion("Odontologos (servicio)", PruebasBase::odontologos);
            v.seccion("Turnos (servicio)", PruebasBase::turnos);
            v.seccion("Tarifario", PruebasBase::tarifario);
            v.seccion("Medios de pago", PruebasBase::mediosDePago);
            v.seccion("CalculadorMonto", PruebasBase::calculador);
            v.seccion("Facturacion (servicio)", PruebasBase::facturacion);
            v.seccion("Repositorios", PruebasBase::repositorios);
            v.seccion("Persistencia de pacientes", PruebasBase::persistenciaPacientes);
            v.seccion("Persistencia de odontologos", PruebasBase::persistenciaOdontologos);
            v.seccion("Persistencia de turnos", PruebasBase::persistenciaTurnos);
            v.seccion("Persistencia de facturas", PruebasBase::persistenciaFacturas);
        } finally {
            restaurarArchivos(respaldo);
        }
        boolean ok = v.resumen();
        System.exit(ok ? 0 : 1);
    }

    // ------------------------------------------------------------------ enums y Domicilio (etapa 15)

    private static void enumsYDomicilio() {
        // los tres enums de tratamiento comparten la interfaz MotivoConsulta
        List<MotivoConsulta> motivos = new ArrayList<>();
        motivos.addAll(List.of(TipoConsulta.values()));
        motivos.addAll(List.of(TipoCirugia.values()));
        motivos.addAll(List.of(TipoOrtodoncia.values()));
        v.igual("hay 10 motivos de consulta", 10, motivos.size());

        Set<String> legibles = motivos.stream().map(MotivoConsulta::getNombreLegible).collect(Collectors.toSet());
        v.igual("cada motivo tiene un nombre legible distinto", 10, legibles.size());
        v.verdadero("ningun nombre legible esta vacio",
                motivos.stream().noneMatch(m -> m.getNombreLegible() == null || m.getNombreLegible().isBlank()));
        v.verdadero("el nombre legible se muestra con mayuscula inicial y sin guiones bajos",
                motivos.stream().allMatch(m -> !m.getNombreLegible().contains("_")
                        && Character.isUpperCase(m.getNombreLegible().charAt(0))));
        v.igual("legible: EXTRACCION_MUELA", "Extracción de muela", TipoCirugia.EXTRACCION_MUELA.getNombreLegible());
        v.igual("legible: BRACKETS_CERAMICOS", "Brackets cerámicos", TipoOrtodoncia.BRACKETS_CERAMICOS.getNombreLegible());
        v.igual("legible: LIMPIEZA", "Limpieza", TipoConsulta.LIMPIEZA.getNombreLegible());

        // el nombre tecnico no cambia: lo usan los archivos .txt y el Tarifario
        v.igual("toString de un motivo sigue siendo el nombre tecnico", "EXTRACCION_MUELA", TipoCirugia.EXTRACCION_MUELA.toString());
        v.igual("name() desde la interfaz", "IMPLANTE", ((MotivoConsulta) TipoCirugia.IMPLANTE).name());

        // tipo de hogar
        v.igual("legible: CASA", "Casa", TipoHogar.CASA.getNombreLegible());
        v.igual("legible: DEPARTAMENTO", "Departamento", TipoHogar.DEPARTAMENTO.getNombreLegible());
        v.igual("legible: PH", "PH", TipoHogar.PH.getNombreLegible());
        v.igual("TipoHogar mantiene sus 3 valores", 3, TipoHogar.values().length);

        // estados del turno: la urgencia ya no es un estado
        v.igual("EstadoTurno: valores del ciclo de vida",
                List.of("PENDIENTE", "CONFIRMADO", "CANCELADO", "COMPLETADO", "AUSENTE"),
                List.of(EstadoTurno.values()).stream().map(Enum::name).collect(Collectors.toList()));
        v.igual("legible: PENDIENTE", "Pendiente", EstadoTurno.PENDIENTE.getNombreLegible());
        v.igual("legible: COMPLETADO", "Completado", EstadoTurno.COMPLETADO.getNombreLegible());
        v.igual("la urgencia sigue existiendo como motivo", "URGENCIA", TipoConsulta.URGENCIA.name());

        // domicilio
        Domicilio d = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
        v.igual("direccion completa", "Necochea 581, Lobos, Buenos Aires", d.obtenerDireccionCompleta());
        v.igual("toString", "Necochea 581 (Lobos, Buenos Aires)", d.toString());
        v.verdadero("esDeProvincia ignora mayusculas", d.esDeProvincia("buenos aires"));
        v.verdadero("esDeProvincia distingue provincias", !d.esDeProvincia("Cordoba"));
        v.verdadero("esDeProvincia con null da false", !d.esDeProvincia(null));
        v.igual("dos domicilios con los mismos datos son iguales", d,
                new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA));
        v.igual("y tienen el mismo hashCode", d.hashCode(),
                new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA).hashCode());
        v.verdadero("distinto numero: no son iguales",
                !d.equals(new Domicilio("Necochea", 582, "Lobos", "Buenos Aires", TipoHogar.CASA)));
        v.verdadero("distinto tipo de hogar: no son iguales",
                !d.equals(new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.PH)));
        v.lanza("calle nula", NullPointerException.class,
                () -> new Domicilio(null, 1, "L", "P", TipoHogar.CASA));
        v.lanza("localidad nula", NullPointerException.class,
                () -> new Domicilio("C", 1, null, "P", TipoHogar.CASA));
        v.lanza("provincia nula", NullPointerException.class,
                () -> new Domicilio("C", 1, "L", null, TipoHogar.CASA));
        v.lanza("tipo de hogar nulo", NullPointerException.class,
                () -> new Domicilio("C", 1, "L", "P", null));
        v.verdadero("no hay forma de crear un domicilio vacio ni de modificarlo",
                Domicilio.class.getConstructors().length == 1
                        && java.util.Arrays.stream(Domicilio.class.getMethods()).noneMatch(m -> m.getName().startsWith("set")));
    }

    // ------------------------------------------------------------------ personas (etapa 16)

    private static boolean tieneMetodo(Class<?> clase, String nombre) {
        return java.util.Arrays.stream(clase.getMethods()).anyMatch(m -> m.getName().equals(nombre));
    }

    private static void personas() throws Exception {
        // clase base comun
        Paciente fatima = new Paciente(1L, "Fatima", "Persi", 46113226, "f@x.com", LocalDate.of(2026, 1, 15), null, true);
        Odontologo magali = new General(2L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        v.verdadero("Paciente es una Persona", fatima instanceof Persona);
        v.verdadero("Odontologo es una Persona", magali instanceof Persona);
        v.verdadero("Persona es abstracta", java.lang.reflect.Modifier.isAbstract(Persona.class.getModifiers()));
        v.igual("nombre completo del paciente", "Fatima Persi", fatima.getNombreCompleto());
        v.igual("nombre completo del odontologo", "Magali Pesqueira", magali.getNombreCompleto());
        v.igual("getNombreCompleto se declara una sola vez (en Persona)", Persona.class,
                Paciente.class.getMethod("getNombreCompleto").getDeclaringClass());
        v.igual("getNombreCompleto de Odontologo tambien viene de Persona", Persona.class,
                Odontologo.class.getMethod("getNombreCompleto").getDeclaringClass());
        v.igual("id del paciente", 1L, fatima.getId());
        v.igual("id del odontologo", 2L, magali.getId());

        // paciente: constructor unico, sin setters de identidad
        v.igual("Paciente tiene un unico constructor", 1, Paciente.class.getConstructors().length);
        v.verdadero("Paciente sin setId", !tieneMetodo(Paciente.class, "setId"));
        v.verdadero("Paciente sin setDni", !tieneMetodo(Paciente.class, "setDni"));
        v.verdadero("Paciente sin setFechaAlta", !tieneMetodo(Paciente.class, "setFechaAlta"));
        v.verdadero("sin domicilio", !fatima.tieneDomicilio());
        v.igual("toString de un paciente sin domicilio",
                "Paciente ID: 1 | Fatima Persi | DNI: 46113226 | Email: f@x.com | Domicilio: Sin domicilio | Obra social: Sí",
                fatima.toString());
        Domicilio dom = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
        Paciente conDom = new Paciente(3L, "Tomas", "Barea", 46443494, "t@x.com", LocalDate.of(2026, 2, 1), dom, false);
        v.verdadero("con domicilio", conDom.tieneDomicilio());
        v.igual("toString de un paciente con domicilio",
                "Paciente ID: 3 | Tomas Barea | DNI: 46443494 | Email: t@x.com | Domicilio: Necochea 581 (Lobos, Buenos Aires) | Obra social: No",
                conDom.toString());
        v.igual("fecha de alta", LocalDate.of(2026, 2, 1), conDom.getFechaAlta());

        // paciente: la identidad natural es el DNI
        Paciente mismoDni = new Paciente(9L, "Otro", "Nombre", 46113226, "otro@x.com", LocalDate.now(), null, false);
        v.verdadero("mismo DNI: son el mismo paciente (aunque cambie el resto)", fatima.equals(mismoDni));
        v.igual("mismo DNI: mismo hashCode", fatima.hashCode(), mismoDni.hashCode());
        v.verdadero("distinto DNI: no son iguales", !fatima.equals(conDom));
        v.verdadero("un paciente no es igual a null", !fatima.equals(null));
        v.verdadero("un paciente no es igual a un odontologo con el mismo numero",
                !new Paciente(1L, "A", "B", 5, "a@b.com", LocalDate.now(), null, false)
                        .equals(new General(1L, "A", "B", 5, TipoConsulta.CONTROL)));

        // odontologo: abstracto, sin setters de identidad ni de tipo
        v.verdadero("Odontologo es abstracta", java.lang.reflect.Modifier.isAbstract(Odontologo.class.getModifiers()));
        v.verdadero("Odontologo sin setId", !tieneMetodo(Odontologo.class, "setId"));
        v.verdadero("Odontologo sin setMatricula", !tieneMetodo(Odontologo.class, "setMatricula"));
        v.verdadero("General sin setTipoConsulta", !tieneMetodo(General.class, "setTipoConsulta"));
        v.verdadero("Cirujano sin setTipoCirugia", !tieneMetodo(Cirujano.class, "setTipoCirugia"));
        v.verdadero("Ortodoncista sin setTipoOrtodoncia", !tieneMetodo(Ortodoncista.class, "setTipoOrtodoncia"));
        v.igual("General tiene un unico constructor", 1, General.class.getConstructors().length);
        v.igual("Cirujano tiene un unico constructor", 1, Cirujano.class.getConstructors().length);
        v.igual("Ortodoncista tiene un unico constructor", 1, Ortodoncista.class.getConstructors().length);

        // cada subclase dice su especialidad y sus motivos (no hace falta instanceof)
        Odontologo cirujano = new Cirujano(5L, "Blas", "Pesce", 3367, TipoCirugia.IMPLANTE);
        Odontologo orto = new Ortodoncista(6L, "Nahuel", "Torres", 2245, TipoOrtodoncia.BRACKETS_CERAMICOS);
        v.igual("especialidad General", Especialidad.GENERAL, magali.getEspecialidad());
        v.igual("especialidad Cirujano", Especialidad.CIRUJANO, cirujano.getEspecialidad());
        v.igual("especialidad Ortodoncista", Especialidad.ORTODONCISTA, orto.getEspecialidad());
        v.igual("subespecialidad General", TipoConsulta.LIMPIEZA, magali.getSubespecialidad());
        v.igual("subespecialidad Cirujano", TipoCirugia.IMPLANTE, cirujano.getSubespecialidad());
        v.igual("subespecialidad Ortodoncista", TipoOrtodoncia.BRACKETS_CERAMICOS, orto.getSubespecialidad());
        v.igual("motivos posibles de General", List.<MotivoConsulta>of(TipoConsulta.values()), magali.getMotivosPosibles());
        v.igual("motivos posibles de Cirujano", List.<MotivoConsulta>of(TipoCirugia.values()), cirujano.getMotivosPosibles());
        v.igual("motivos posibles de Ortodoncista", List.<MotivoConsulta>of(TipoOrtodoncia.values()), orto.getMotivosPosibles());
        v.lanza("la lista de motivos no se puede modificar", UnsupportedOperationException.class,
                () -> magali.getMotivosPosibles().clear());
        v.igual("getTipoConsulta sigue disponible", TipoConsulta.LIMPIEZA, ((General) magali).getTipoConsulta());
        v.lanza("General sin tipo", NullPointerException.class, () -> new General(1L, "A", "B", 1, null));
        v.lanza("Cirujano sin tipo", NullPointerException.class, () -> new Cirujano(1L, "A", "B", 1, null));
        v.lanza("Ortodoncista sin tipo", NullPointerException.class, () -> new Ortodoncista(1L, "A", "B", 1, null));

        // toString: mismo texto que antes, escrito una sola vez en Odontologo
        v.igual("toString General", "Especialidad: General (LIMPIEZA) | ID: 2 | Magali Pesqueira | Matrícula: 1131", magali.toString());
        v.igual("toString Cirujano", "Especialidad: Cirujano (IMPLANTE) | ID: 5 | Blas Pesce | Matrícula: 3367", cirujano.toString());
        v.igual("toString Ortodoncista", "Especialidad: Ortodoncista (BRACKETS_CERAMICOS) | ID: 6 | Nahuel Torres | Matrícula: 2245", orto.toString());
        v.igual("las subclases no repiten toString", Odontologo.class, General.class.getMethod("toString").getDeclaringClass());

        // odontologo: la identidad natural es la matricula, sea cual sea la especialidad
        Odontologo otraEspecialidad = new Cirujano(77L, "Otro", "Nombre", 1131, TipoCirugia.EXTRACCION_MUELA);
        v.verdadero("misma matricula: es el mismo odontologo", magali.equals(otraEspecialidad));
        v.igual("misma matricula: mismo hashCode", magali.hashCode(), otraEspecialidad.hashCode());
        v.verdadero("distinta matricula: no son iguales", !magali.equals(cirujano));

        // conId: copia con otro id, el original no cambia
        Odontologo copia = cirujano.conId(50L);
        v.igual("conId: el original conserva su id", 5L, cirujano.getId());
        v.igual("conId: la copia tiene el id nuevo", 50L, copia.getId());
        v.verdadero("conId: es otro objeto", copia != cirujano);
        v.verdadero("conId: mismo tipo y mismos datos",
                copia instanceof Cirujano && copia.getMatricula() == 3367
                        && copia.getSubespecialidad() == TipoCirugia.IMPLANTE
                        && copia.getNombreCompleto().equals("Blas Pesce"));
        v.verdadero("conId: sigue siendo el mismo odontologo (misma matricula)", copia.equals(cirujano));

        // Especialidad
        v.igual("hay 3 especialidades", 3, Especialidad.values().length);
        v.igual("nombre guardado: General", "General", Especialidad.GENERAL.getNombre());
        v.igual("nombre guardado: Cirujano", "Cirujano", Especialidad.CIRUJANO.getNombre());
        v.igual("nombre guardado: Ortodoncista", "Ortodoncista", Especialidad.ORTODONCISTA.getNombre());
        v.igual("toString para el combo", "Cirujano", Especialidad.CIRUJANO.toString());
        v.igual("desdeNombre General", java.util.Optional.of(Especialidad.GENERAL), Especialidad.desdeNombre("General"));
        v.verdadero("desdeNombre distingue mayusculas", Especialidad.desdeNombre("general").isEmpty());
        v.verdadero("desdeNombre de un tipo desconocido", Especialidad.desdeNombre("Odontologo").isEmpty());
        v.verdadero("desdeNombre null", Especialidad.desdeNombre(null).isEmpty());
        v.igual("subespecialidad por defecto General", TipoConsulta.LIMPIEZA, Especialidad.GENERAL.getSubespecialidadPorDefecto());
        v.igual("subespecialidad por defecto Cirujano", TipoCirugia.EXTRACCION_MUELA, Especialidad.CIRUJANO.getSubespecialidadPorDefecto());
        v.igual("subespecialidad por defecto Ortodoncista", TipoOrtodoncia.BRACKETS_METALICOS, Especialidad.ORTODONCISTA.getSubespecialidadPorDefecto());
        v.igual("subespecialidadPorNombre", TipoCirugia.IMPLANTE, Especialidad.CIRUJANO.subespecialidadPorNombre("IMPLANTE"));
        v.lanza("subespecialidadPorNombre inexistente", IllegalArgumentException.class,
                () -> Especialidad.CIRUJANO.subespecialidadPorNombre("LIMPIEZA"));
        Odontologo creado = Especialidad.ORTODONCISTA.crearConSubespecialidadPorDefecto(8L, "Ana", "Paz", 99);
        v.verdadero("crear por defecto: Ortodoncista con brackets metalicos",
                creado instanceof Ortodoncista o && o.getTipoOrtodoncia() == TipoOrtodoncia.BRACKETS_METALICOS
                        && creado.getId() == 8L && creado.getMatricula() == 99);
        v.lanza("crear con un motivo de otra especialidad", IllegalArgumentException.class,
                () -> Especialidad.GENERAL.crear(1L, "A", "B", 1, TipoCirugia.IMPLANTE));
        v.lanza("crear sin motivo", NullPointerException.class,
                () -> Especialidad.GENERAL.crear(1L, "A", "B", 1, null));
    }

    // ------------------------------------------------------------------ turno y factura (etapa 17)

    private static void turnoYFactura() throws Exception {
        Paciente pac = new Paciente(1L, "Ana", "Perez", 100, "a@x.com", LocalDate.of(2026, 1, 1), null, true);
        Odontologo od = new General(2L, "Luis", "Gomez", 200, TipoConsulta.CONTROL);
        LocalDate dia = LocalDate.of(2026, 12, 1);
        LocalTime nueve = LocalTime.of(9, 0);

        // ---- Turno: construccion y datos
        v.igual("Turno tiene un unico constructor", 1, Turno.class.getConstructors().length);
        Turno t = new Turno(7L, pac, od, dia, nueve, EstadoTurno.PENDIENTE, TipoCirugia.IMPLANTE);
        v.igual("id", 7L, t.getId());
        v.igual("paciente", pac, t.getPaciente());
        v.igual("odontologo", od, t.getOdontologo());
        v.igual("fecha", dia, t.getFecha());
        v.igual("hora", nueve, t.getHora());
        v.igual("estado inicial", EstadoTurno.PENDIENTE, t.getEstado());
        v.igual("motivo presente", Optional.of(TipoCirugia.IMPLANTE), t.getMotivoConsulta());
        v.verdadero("sin motivo: Optional vacio",
                new Turno(8L, pac, od, dia, nueve, EstadoTurno.PENDIENTE, null).getMotivoConsulta().isEmpty());
        v.igual("toString",
                "TURNO #7 | Paciente: Ana Perez | Odontólogo: Luis Gomez | Fecha: 2026-12-01 Hora: 09:00 | Estado: PENDIENTE",
                t.toString());

        // ---- Turno: no se puede armar a medias ni modificar desde afuera
        v.lanza("paciente null", NullPointerException.class,
                () -> new Turno(1L, null, od, dia, nueve, EstadoTurno.PENDIENTE, null));
        v.lanza("odontologo null", NullPointerException.class,
                () -> new Turno(1L, pac, null, dia, nueve, EstadoTurno.PENDIENTE, null));
        v.lanza("fecha null", NullPointerException.class,
                () -> new Turno(1L, pac, od, null, nueve, EstadoTurno.PENDIENTE, null));
        v.lanza("hora null", NullPointerException.class,
                () -> new Turno(1L, pac, od, dia, null, EstadoTurno.PENDIENTE, null));
        v.lanza("estado null", NullPointerException.class,
                () -> new Turno(1L, pac, od, dia, nueve, null, null));
        for (String metodo : List.of("setId", "setPaciente", "setOdontologo", "setFecha", "setHora",
                "setEstado", "setMotivoConsulta", "getMotivoConsultaTexto")) {
            v.verdadero("Turno sin " + metodo, !tieneMetodo(Turno.class, metodo));
        }

        // ---- Turno: igualdad por id
        Turno mismoId = new Turno(7L, pac, od, dia.plusDays(3), LocalTime.of(15, 0), EstadoTurno.COMPLETADO, null);
        v.verdadero("mismo id: es el mismo turno", t.equals(mismoId));
        v.igual("mismo id: mismo hashCode", t.hashCode(), mismoId.hashCode());
        v.verdadero("otro id: no son iguales",
                !t.equals(new Turno(9L, pac, od, dia, nueve, EstadoTurno.PENDIENTE, TipoCirugia.IMPLANTE)));
        v.verdadero("un turno no es igual a null", !t.equals(null));
        v.verdadero("un turno no es igual a un texto", !t.equals("TURNO #7"));

        // ---- Estados: tabla completa de transiciones (null = no permitida)
        EstadoTurno P = EstadoTurno.PENDIENTE, C = EstadoTurno.CONFIRMADO, X = EstadoTurno.CANCELADO,
                F = EstadoTurno.COMPLETADO, A = EstadoTurno.AUSENTE;
        EstadoTurno[][] esperado = {
                // confirmar, cancelar, completar, marcarAusente
                {C,    X,    F,    A},     // desde PENDIENTE
                {null, X,    F,    A},     // desde CONFIRMADO
                {null, null, null, null},  // desde CANCELADO
                {null, null, null, null},  // desde COMPLETADO
                {null, null, null, null}   // desde AUSENTE
        };
        String[] operaciones = {"confirmar", "cancelar", "completar", "marcarAusente"};
        v.igual("hay 5 estados", 5, EstadoTurno.values().length);
        v.igual("el orden de los estados coincide con la tabla de la prueba",
                List.of(P, C, X, F, A), List.of(EstadoTurno.values()));
        for (EstadoTurno desde : EstadoTurno.values()) {
            for (int op = 0; op < operaciones.length; op++) {
                EstadoTurno destino = esperado[desde.ordinal()][op];
                String nombre = desde + "." + operaciones[op];
                Turno turno = new Turno(1L, pac, od, dia, nueve, desde, null);
                int operacion = op;
                Verificador.Ejecutable transicion = () -> {
                    switch (operacion) {
                        case 0 -> turno.confirmar();
                        case 1 -> turno.cancelar();
                        case 2 -> turno.completar();
                        default -> turno.marcarAusente();
                    }
                };
                if (destino == null) {
                    v.lanza(nombre + " no esta permitido", DatoInvalidoException.class, transicion);
                    v.igual(nombre + " rechazado: el estado no cambia", desde, turno.getEstado());
                } else {
                    transicion.ejecutar();
                    v.igual(nombre + " -> " + destino, destino, turno.getEstado());
                }
            }
        }

        // ---- Estados: mensajes (los de siempre + los nuevos)
        Turno cancelado = new Turno(1L, pac, od, dia, nueve, EstadoTurno.CANCELADO, null);
        Turno completado = new Turno(2L, pac, od, dia, nueve, EstadoTurno.COMPLETADO, null);
        Turno confirmado = new Turno(3L, pac, od, dia, nueve, EstadoTurno.CONFIRMADO, null);
        Turno ausente = new Turno(4L, pac, od, dia, nueve, EstadoTurno.AUSENTE, null);
        v.igual("mensaje: cancelar un cancelado", "Dato inválido en 'Estado': Este turno ya fue cancelado.",
                mensajeDe(cancelado::cancelar));
        v.igual("mensaje: cancelar un completado", "Dato inválido en 'Estado': No se puede cancelar un turno completado.",
                mensajeDe(completado::cancelar));
        v.igual("mensaje: confirmar un confirmado", "Dato inválido en 'Estado': Solo se pueden confirmar turnos pendientes.",
                mensajeDe(confirmado::confirmar));
        v.igual("mensaje: completar un cancelado", "Dato inválido en 'Estado': No se puede completar un turno que fue cancelado.",
                mensajeDe(cancelado::completar));
        v.igual("mensaje: completar un completado", "Dato inválido en 'Estado': Este turno ya fue completado.",
                mensajeDe(completado::completar));
        v.igual("mensaje: completar un ausente",
                "Dato inválido en 'Estado': No se puede completar un turno en el que el paciente no asistió.",
                mensajeDe(ausente::completar));
        v.igual("mensaje: cancelar un ausente",
                "Dato inválido en 'Estado': No se puede cancelar un turno en el que el paciente no asistió.",
                mensajeDe(ausente::cancelar));
        v.igual("mensaje: ausente sobre un ausente", "Dato inválido en 'Estado': Este turno ya fue marcado como ausente.",
                mensajeDe(ausente::marcarAusente));

        // ---- Estados: AUSENTE y cobro
        v.igual("legible: AUSENTE", "Ausente", EstadoTurno.AUSENTE.getNombreLegible());
        v.verdadero("PENDIENTE se puede cobrar", EstadoTurno.PENDIENTE.esCobrable());
        v.verdadero("CONFIRMADO se puede cobrar", EstadoTurno.CONFIRMADO.esCobrable());
        v.verdadero("COMPLETADO se puede cobrar", EstadoTurno.COMPLETADO.esCobrable());
        v.verdadero("CANCELADO no se puede cobrar", !EstadoTurno.CANCELADO.esCobrable());
        v.verdadero("AUSENTE no se puede cobrar", !EstadoTurno.AUSENTE.esCobrable());
        v.igual("valueOf de un estado guardado", EstadoTurno.AUSENTE, EstadoTurno.valueOf("AUSENTE"));

        // ---- MotivoConsulta.desdeNombre
        List<MotivoConsulta> motivos = new ArrayList<>();
        motivos.addAll(List.of(TipoConsulta.values()));
        motivos.addAll(List.of(TipoCirugia.values()));
        motivos.addAll(List.of(TipoOrtodoncia.values()));
        for (MotivoConsulta m : motivos) {
            v.igual("desdeNombre(" + m.name() + ")", Optional.of(m), MotivoConsulta.desdeNombre(m.name()));
        }
        v.igual("desdeNombre de un texto que no es un motivo", Optional.empty(), MotivoConsulta.desdeNombre("Consulta"));
        v.igual("desdeNombre distingue mayusculas", Optional.empty(), MotivoConsulta.desdeNombre("limpieza"));
        v.igual("desdeNombre(null)", Optional.empty(), MotivoConsulta.desdeNombre(null));

        // ---- Servicio de turnos: motivo tipado y transiciones de la entidad
        v.verdadero("ServicioTurno.registrarTurno recibe MotivoConsulta (no Object)",
                ServicioTurno.class.getMethod("registrarTurno", Paciente.class, Odontologo.class, LocalDate.class,
                        LocalTime.class, MotivoConsulta.class) != null);
        ServicioTurno st = new ServicioTurno(new RepositorioTurno());
        Turno nuevo = st.registrarTurno(pac, od, LocalDate.now().plusDays(1), LocalTime.of(10, 0), TipoConsulta.CONTROL);
        v.igual("el servicio crea el turno PENDIENTE", EstadoTurno.PENDIENTE, nuevo.getEstado());
        v.igual("cancelar desde el servicio", EstadoTurno.CANCELADO, st.cancelarTurno(nuevo.getId()).getEstado());
        v.igual("lo guardado en el repositorio tiene el estado nuevo", EstadoTurno.CANCELADO,
                st.buscarPorId(nuevo.getId()).getEstado());

        // ---- Factura: sin constructor publico, con Builder
        v.igual("Factura sin constructores publicos", 0, Factura.class.getConstructors().length);
        DesgloseMonto desglose = new DesgloseMonto(100, 0, 10, 90);
        LocalDate hoy = LocalDate.now();
        Factura f = Factura.builder().id(1L).turnoId(10L).pacienteId(5L).pacienteNombre("A B").concepto("LIMPIEZA")
                .fecha(hoy).metodoPago("Efectivo").desglose(desglose).build();
        v.igual("factura: id", 1L, f.getId());
        v.igual("factura: turno", 10L, f.getTurnoId());
        v.igual("factura: paciente", 5L, f.getPacienteId());
        v.igual("factura: nombre", "A B", f.getPacienteNombre());
        v.igual("factura: concepto", "LIMPIEZA", f.getConcepto());
        v.igual("factura: fecha", hoy, f.getFecha());
        v.igual("factura: medio", "Efectivo", f.getMetodoPago());
        v.verdadero("factura: contiene el desglose recibido", f.getDesglose() == desglose);
        v.casi("factura: base (atajo al desglose)", 100, f.getMontoBase());
        v.casi("factura: desc. obra social (atajo)", 0, f.getDescuentoObraSocial());
        v.casi("factura: desc. efectivo (atajo)", 10, f.getDescuentoEfectivo());
        v.casi("factura: total (atajo)", 90, f.getMontoTotal());
        v.igual("DesgloseMonto ahora vive en entity", "entity", DesgloseMonto.class.getPackageName());
        v.igual("toString de la factura",
                "FACTURA #1 | Turno: 10 | Paciente: A B | Concepto: LIMPIEZA | Fecha: " + hoy
                        + " | Medio: Efectivo | Base: $100.0 | Desc. obra social: $0.0 | Desc. efectivo: $10.0 | Total: $90.0",
                f.toString());
        v.lanza("factura sin desglose", NullPointerException.class,
                () -> Factura.builder().id(1L).pacienteNombre("A").concepto("C").fecha(hoy).metodoPago("Efectivo").build());
        v.lanza("factura sin medio de pago", NullPointerException.class,
                () -> Factura.builder().id(1L).pacienteNombre("A").concepto("C").fecha(hoy).desglose(desglose).build());
        v.lanza("factura sin fecha", NullPointerException.class,
                () -> Factura.builder().id(1L).pacienteNombre("A").concepto("C").metodoPago("Efectivo").desglose(desglose).build());
        v.lanza("factura sin nombre de paciente", NullPointerException.class,
                () -> Factura.builder().id(1L).concepto("C").fecha(hoy).metodoPago("Efectivo").desglose(desglose).build());
        v.lanza("factura sin concepto", NullPointerException.class,
                () -> Factura.builder().id(1L).pacienteNombre("A").fecha(hoy).metodoPago("Efectivo").desglose(desglose).build());
        v.verdadero("Factura sigue sin setters",
                java.util.Arrays.stream(Factura.class.getMethods()).noneMatch(m -> m.getName().startsWith("set")));

        // ---- Facturacion con turnos ausentes y sin motivo
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo());
        ServicioTurno stFac = new ServicioTurno(new RepositorioTurno());
        ServicioFactura sf = new ServicioFactura(new RepositorioFactura(), stFac, new Tarifario(), new CalculadorMonto(),
                List.of(new Efectivo()));
        Paciente p = sp.registrarPaciente("Fatima", "Persi", 3001, "f@x.com", null, false);
        Odontologo o = so.registrarOdontologo(new General(0L, "Magali", "Pesqueira", 31, TipoConsulta.LIMPIEZA));
        LocalDate manana = LocalDate.now().plusDays(1);
        Turno aus = stFac.registrarTurno(p, o, manana, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        stFac.getRepositorio().guardar(new Turno(aus.getId(), p, o, manana, LocalTime.of(9, 0), EstadoTurno.AUSENTE, TipoConsulta.LIMPIEZA));
        Turno cobrable = stFac.registrarTurno(p, o, manana, LocalTime.of(10, 0), TipoConsulta.CONTROL);
        v.igual("un turno ausente no aparece para cobrar", 1, sf.listarTurnosPorCobrar().size());
        v.igual("el unico por cobrar es el otro", cobrable.getId(), sf.listarTurnosPorCobrar().get(0).getId());
        v.igual("mensaje: cobrar un ausente", "Dato inválido en 'Turno': No se puede cobrar un turno ausente.",
                mensajeDe(() -> sf.cobrarTurno(aus.getId(), new Efectivo())));
        v.lanza("vista previa de un ausente", DatoInvalidoException.class, () -> sf.calcularVistaPrevia(aus.getId()));
        stFac.cancelarTurno(cobrable.getId());
        v.igual("mensaje: cobrar un cancelado (sin cambios)", "Dato inválido en 'Turno': No se puede cobrar un turno cancelado.",
                mensajeDe(() -> sf.cobrarTurno(cobrable.getId(), new Efectivo())));
        Turno urgencia = stFac.registrarTurno(p, o, manana, LocalTime.of(11, 0), TipoConsulta.URGENCIA);
        Factura emitida = sf.cobrarTurno(urgencia.getId(), new Efectivo());
        v.igual("la factura emitida toma el concepto del motivo", "URGENCIA", emitida.getConcepto());
        v.casi("la factura emitida tiene el total del desglose", 36000, emitida.getMontoTotal());
        Turno sinMotivo = stFac.registrarTurno(p, o, manana, LocalTime.of(12, 0));
        v.igual("mensaje: cobrar un turno sin motivo",
                "Dato inválido en 'Tratamiento': El turno no tiene un tratamiento cargado.",
                mensajeDe(() -> sf.cobrarTurno(sinMotivo.getId(), new Efectivo())));
    }

    // ------------------------------------------------------------------ pacientes

    private static void pacientes() throws Exception {
        ServicioPaciente s = new ServicioPaciente(new RepositorioPaciente());
        Domicilio d = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);

        Paciente persi = s.registrarPaciente("Fatima", "Persi", 46113226, "fatimap@gmail.com", d, true);
        v.igual("el primer paciente tiene id 1", 1L, persi.getId());
        v.igual("la fecha de alta es hoy", LocalDate.now(), persi.getFechaAlta());
        v.verdadero("guarda la obra social", persi.tieneObraSocial());

        Paciente barea = s.registrarPaciente("Tomas", "Barea", 46443494, "tomy@gmail.com", null, false);
        v.igual("el segundo paciente tiene id 2", 2L, barea.getId());
        v.verdadero("un paciente sin domicilio no tiene domicilio", !barea.tieneDomicilio());

        v.lanza("nombre vacio", DatoInvalidoException.class,
                () -> s.registrarPaciente(" ", "X", 1, "a@b.com", null, false));
        v.lanza("apellido nulo", DatoInvalidoException.class,
                () -> s.registrarPaciente("X", null, 1, "a@b.com", null, false));
        v.lanza("dni cero", DatoInvalidoException.class,
                () -> s.registrarPaciente("X", "Y", 0, "a@b.com", null, false));
        v.lanza("email sin arroba", DatoInvalidoException.class,
                () -> s.registrarPaciente("X", "Y", 5, "ab.com", null, false));
        v.lanza("dni repetido", DatoInvalidoException.class,
                () -> s.registrarPaciente("X", "Y", 46113226, "a@b.com", null, false));
        v.igual("los alta fallidos no guardan nada", 2, s.listarTodos().size());

        v.igual("buscarPorDni", 1L, s.buscarPorDni(46113226).getId());
        v.igual("buscarPorId", 2L, s.buscarPorId(2).getId());
        v.lanza("buscarPorId inexistente", PacienteNoEncontradoException.class, () -> s.buscarPorId(99));
        v.lanza("buscarPorDni inexistente", PacienteNoEncontradoException.class, () -> s.buscarPorDni(7));
        v.lanza("buscarPorDni invalido", DatoInvalidoException.class, () -> s.buscarPorDni(-1));

        // ordenados por apellido: Barea antes que Persi
        List<Paciente> ordenados = s.listarPacientesOrdenadosPorApellido();
        v.igual("orden por apellido (1ro)", "Barea", ordenados.get(0).getApellido());
        v.igual("orden por apellido (2do)", "Persi", ordenados.get(1).getApellido());

        // actualizar: con domicilio null conserva el que tenia
        Paciente act = s.actualizarPaciente(1, "Fati", "Persi", "nuevo@gmail.com", null, false);
        v.igual("actualiza el nombre", "Fati", act.getNombre());
        v.igual("actualiza el email", "nuevo@gmail.com", act.getEmail());
        v.verdadero("actualiza la obra social", !act.tieneObraSocial());
        v.verdadero("domicilio null conserva el anterior", act.getDomicilio() == d);
        Domicilio otro = new Domicilio("Gaboto", 849, "La Tablada", "Buenos Aires", TipoHogar.PH);
        v.verdadero("un domicilio nuevo reemplaza al anterior",
                s.actualizarPaciente(1, "Fati", "Persi", "nuevo@gmail.com", otro, false).getDomicilio() == otro);
        v.lanza("actualizar con email invalido", DatoInvalidoException.class,
                () -> s.actualizarPaciente(1, "Fati", "Persi", "sinarroba", null, false));
        v.lanza("actualizar inexistente", PacienteNoEncontradoException.class,
                () -> s.actualizarPaciente(99, "A", "B", "a@b.com", null, false));

        // eliminar
        s.eliminarPaciente(2);
        v.igual("eliminar saca al paciente", 1, s.listarTodos().size());
        v.lanza("eliminar inexistente", PacienteNoEncontradoException.class, () -> s.eliminarPaciente(2));

        // el id nuevo sigue al maximo existente
        Paciente nuevo = s.registrarPaciente("Maria", "Garcia", 38221456, "m@g.com", null, true);
        v.igual("id nuevo = maximo + 1", 2L, nuevo.getId());
    }

    // ------------------------------------------------------------------ odontologos

    private static void odontologos() throws Exception {
        ServicioOdontologo s = new ServicioOdontologo(new RepositorioOdontologo());

        Odontologo magali = new General(99L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        Odontologo registrado = s.registrarOdontologo(magali);
        v.verdadero("devuelve una copia con el id asignado, no el objeto recibido", registrado != magali);
        v.igual("el servicio asigna el id (1)", 1L, registrado.getId());
        v.igual("el objeto recibido no se modifica (conserva su id)", 99L, magali.getId());
        v.verdadero("la copia es del mismo tipo y tiene los mismos datos",
                registrado instanceof General && registrado.getMatricula() == 1131
                        && registrado.getSubespecialidad() == TipoConsulta.LIMPIEZA
                        && registrado.getNombreCompleto().equals("Magali Pesqueira"));

        Odontologo blas = s.registrarOdontologo(new Cirujano(0L, "Blas", "Pesce", 3367, TipoCirugia.EXTRACCION_MUELA));
        v.igual("segundo id", 2L, blas.getId());

        v.lanza("matricula repetida", DatoInvalidoException.class,
                () -> s.registrarOdontologo(new General(0L, "X", "Y", 1131, TipoConsulta.CONTROL)));
        v.lanza("odontologo nulo", DatoInvalidoException.class, () -> s.registrarOdontologo(null));
        v.lanza("matricula cero", DatoInvalidoException.class,
                () -> s.registrarOdontologo(new General(0L, "X", "Y", 0, TipoConsulta.CONTROL)));
        v.lanza("nombre vacio", DatoInvalidoException.class,
                () -> s.registrarOdontologo(new General(0L, " ", "Y", 55, TipoConsulta.CONTROL)));

        v.igual("buscarPorMatricula", 2L, s.buscarPorMatricula(3367).getId());
        v.lanza("buscarPorMatricula inexistente", OdontologoNoEncontradoException.class,
                () -> s.buscarPorMatricula(9999));
        v.lanza("buscarPorId inexistente", OdontologoNoEncontradoException.class, () -> s.buscarPorId(50));

        Odontologo act = s.actualizarOdontologo(1, "Magali B", "Pesqueira");
        v.igual("actualiza el nombre", "Magali B", act.getNombre());
        v.lanza("actualizar con apellido vacio", DatoInvalidoException.class,
                () -> s.actualizarOdontologo(1, "Magali", ""));

        s.eliminarOdontologo(2);
        v.igual("eliminar", 1, s.listarTodos().size());
        v.lanza("eliminar inexistente", OdontologoNoEncontradoException.class, () -> s.eliminarOdontologo(2));
    }

    // ------------------------------------------------------------------ turnos

    private static void turnos() throws Exception {
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo());
        ServicioTurno st = new ServicioTurno(new RepositorioTurno());
        Paciente p1 = sp.registrarPaciente("Fatima", "Persi", 1001, "f@x.com", null, true);
        Paciente p2 = sp.registrarPaciente("Tomas", "Barea", 1002, "t@x.com", null, false);
        Odontologo o1 = so.registrarOdontologo(new General(0L, "Magali", "Pesqueira", 11, TipoConsulta.LIMPIEZA));
        Odontologo o2 = so.registrarOdontologo(new Ortodoncista(0L, "Nahuel", "Torres", 22, TipoOrtodoncia.BRACKETS_METALICOS));
        LocalDate manana = LocalDate.now().plusDays(1);

        Turno t1 = st.registrarTurno(p1, o1, manana, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        v.igual("primer turno id 1", 1L, t1.getId());
        v.igual("nace PENDIENTE", EstadoTurno.PENDIENTE, t1.getEstado());
        v.igual("guarda el motivo", TipoConsulta.LIMPIEZA, t1.getMotivoConsulta().orElse(null));

        Turno sinMotivo = st.registrarTurno(p2, o1, manana, LocalTime.of(10, 0));
        v.verdadero("la version sin motivo deja el motivo vacio", sinMotivo.getMotivoConsulta().isEmpty());
        v.igual("la pantalla muestra 'Consulta' cuando no hay motivo", "Consulta", TextoMotivo.de(sinMotivo));
        v.igual("la pantalla muestra el nombre del motivo", "LIMPIEZA", TextoMotivo.de(t1));

        v.lanza("mismo odontologo, misma fecha y hora", TurnoYaReservadoException.class,
                () -> st.registrarTurno(p2, o1, manana, LocalTime.of(9, 0), TipoConsulta.CONTROL));
        v.igual("otro odontologo en el mismo horario si se puede", 3L,
                st.registrarTurno(p2, o2, manana, LocalTime.of(9, 0), TipoOrtodoncia.BRACKETS_METALICOS).getId());

        v.lanza("hora antes de la apertura", DatoInvalidoException.class,
                () -> st.registrarTurno(p1, o1, manana, LocalTime.of(7, 59)));
        v.lanza("hora despues del cierre", DatoInvalidoException.class,
                () -> st.registrarTurno(p1, o1, manana, LocalTime.of(20, 1)));
        v.verdadero("08:00 es valida", st.registrarTurno(p1, o1, manana, LocalTime.of(8, 0)) != null);
        v.verdadero("20:00 es valida (actual: se puede agendar a la hora de cierre)",
                st.registrarTurno(p1, o1, manana, LocalTime.of(20, 0)) != null);
        v.lanza("fecha pasada", DatoInvalidoException.class,
                () -> st.registrarTurno(p1, o1, LocalDate.now().minusDays(1), LocalTime.of(12, 0)));
        v.lanza("paciente nulo", DatoInvalidoException.class,
                () -> st.registrarTurno(null, o1, manana, LocalTime.of(12, 0)));
        v.lanza("odontologo nulo", DatoInvalidoException.class,
                () -> st.registrarTurno(p1, null, manana, LocalTime.of(12, 0)));
        v.lanza("fecha nula", DatoInvalidoException.class,
                () -> st.registrarTurno(p1, o1, null, LocalTime.of(12, 0)));
        v.lanza("hora nula", DatoInvalidoException.class,
                () -> st.registrarTurno(p1, o1, manana, null));

        // ---- estados
        Turno t = st.registrarTurno(p1, o1, manana, LocalTime.of(13, 0), TipoConsulta.CONTROL);
        v.igual("confirmar", EstadoTurno.CONFIRMADO, st.confirmarTurno(t.getId()).getEstado());
        v.lanza("confirmar un turno ya confirmado", DatoInvalidoException.class, () -> st.confirmarTurno(t.getId()));
        v.igual("completar un confirmado", EstadoTurno.COMPLETADO, st.completarTurno(t.getId()).getEstado());
        v.lanza("completar un completado (corregido en etapa 17)", DatoInvalidoException.class, () -> st.completarTurno(t.getId()));
        v.lanza("cancelar un completado", DatoInvalidoException.class, () -> st.cancelarTurno(t.getId()));

        Turno directo = st.registrarTurno(p1, o1, manana, LocalTime.of(14, 0), TipoConsulta.CONTROL);
        v.igual("completar un pendiente sin confirmar se permite (actual)", EstadoTurno.COMPLETADO,
                st.completarTurno(directo.getId()).getEstado());

        Turno c = st.registrarTurno(p1, o1, manana, LocalTime.of(15, 0), TipoConsulta.CONTROL);
        v.lanza("eliminar un turno no cancelado", DatoInvalidoException.class, () -> st.eliminarTurno(c.getId()));
        v.igual("cancelar", EstadoTurno.CANCELADO, st.cancelarTurno(c.getId()).getEstado());
        v.lanza("cancelar un cancelado", DatoInvalidoException.class, () -> st.cancelarTurno(c.getId()));
        v.lanza("confirmar un cancelado", DatoInvalidoException.class, () -> st.confirmarTurno(c.getId()));
        v.lanza("completar un cancelado", DatoInvalidoException.class, () -> st.completarTurno(c.getId()));
        v.lanza("un turno cancelado sigue bloqueando el horario (actual)", TurnoYaReservadoException.class,
                () -> st.registrarTurno(p2, o1, manana, LocalTime.of(15, 0), TipoConsulta.CONTROL));
        st.eliminarTurno(c.getId());
        v.lanza("buscar un turno eliminado", DatoInvalidoException.class, () -> st.buscarPorId(c.getId()));
        v.lanza("buscar un turno inexistente", DatoInvalidoException.class, () -> st.buscarPorId(12345));

        // ---- listados
        long deP1 = st.listarTodos().stream().filter(x -> x.getPaciente().getId() == p1.getId()).count();
        v.igual("listarPorPaciente", (int) deP1, st.listarPorPaciente(p1.getId()).size());
        v.igual("listarPorPaciente de un id inexistente da lista vacia", 0, st.listarPorPaciente(777).size());
        long deO2 = st.listarTodos().stream().filter(x -> x.getOdontologo().getId() == o2.getId()).count();
        v.igual("listarPorOdontologo", (int) deO2, st.listarPorOdontologo(o2.getId()).size());
        v.igual("rango que incluye todo", st.listarTodos().size(),
                st.listarPorRangoFechas(manana, manana).size());
        v.igual("rango sin turnos", 0, st.listarPorRangoFechas(manana.plusDays(5), manana.plusDays(6)).size());
        v.lanza("rango con inicio posterior al fin", DatoInvalidoException.class,
                () -> st.listarPorRangoFechas(manana.plusDays(1), manana));
        v.lanza("rango con fecha nula", DatoInvalidoException.class,
                () -> st.listarPorRangoFechas(null, manana));
    }

    // ------------------------------------------------------------------ tarifario

    private static void tarifario() throws Exception {
        Tarifario t = new Tarifario();
        v.casi("CONTROL", 15000, t.precioDe(TipoConsulta.CONTROL));
        v.casi("LIMPIEZA", 25000, t.precioDe(TipoConsulta.LIMPIEZA));
        v.casi("URGENCIA", 40000, t.precioDe(TipoConsulta.URGENCIA));
        v.casi("BLANQUEAMIENTO", 90000, t.precioDe(TipoConsulta.BLANQUEAMIENTO));
        v.casi("EXTRACCION_MUELA", 60000, t.precioDe(TipoCirugia.EXTRACCION_MUELA));
        v.casi("CIRUGIA_ENCIA", 120000, t.precioDe(TipoCirugia.CIRUGIA_ENCIA));
        v.casi("IMPLANTE", 450000, t.precioDe(TipoCirugia.IMPLANTE));
        v.casi("CIRUGIA_MAXILOFACIAL", 600000, t.precioDe(TipoCirugia.CIRUGIA_MAXILOFACIAL));
        v.casi("BRACKETS_METALICOS", 150000, t.precioDe(TipoOrtodoncia.BRACKETS_METALICOS));
        v.casi("BRACKETS_CERAMICOS", 250000, t.precioDe(TipoOrtodoncia.BRACKETS_CERAMICOS));
        v.casi("acepta el nombre en texto (asi queda el motivo al recargar)", 25000, t.precioDe("LIMPIEZA"));
        v.lanza("motivo nulo", DatoInvalidoException.class, () -> t.precioDe(null));
        v.lanza("motivo sin precio ('Consulta')", DatoInvalidoException.class, () -> t.precioDe("Consulta"));
    }

    // ------------------------------------------------------------------ medios de pago

    private static void mediosDePago() {
        MedioPago ef = new Efectivo();
        MedioPago mp = new MercadoPago();
        MedioPago tj = new Tarjeta();
        v.igual("nombre Efectivo", "Efectivo", ef.getNombre());
        v.igual("nombre Mercado Pago", "Mercado Pago", mp.getNombre());
        v.igual("nombre Tarjeta", "Tarjeta", tj.getNombre());
        v.casi("Efectivo descuenta 10%", 0.10, ef.getPorcentajeDescuento());
        v.casi("Mercado Pago no descuenta", 0.0, mp.getPorcentajeDescuento());
        v.casi("Tarjeta no descuenta", 0.0, tj.getPorcentajeDescuento());
        v.igual("toString de Efectivo", "Efectivo", ef.toString());
        v.igual("toString de Mercado Pago", "Mercado Pago", mp.toString());
        v.igual("toString de Tarjeta", "Tarjeta", tj.toString());
    }

    // ------------------------------------------------------------------ calculador

    private static double redondear(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

    private static void calculador() throws Exception {
        CalculadorMonto calc = new CalculadorMonto();
        Tarifario tarifario = new Tarifario();
        Object[] tratamientos = {
                TipoConsulta.CONTROL, TipoConsulta.LIMPIEZA, TipoConsulta.URGENCIA, TipoConsulta.BLANQUEAMIENTO,
                TipoCirugia.EXTRACCION_MUELA, TipoCirugia.CIRUGIA_ENCIA, TipoCirugia.IMPLANTE, TipoCirugia.CIRUGIA_MAXILOFACIAL,
                TipoOrtodoncia.BRACKETS_METALICOS, TipoOrtodoncia.BRACKETS_CERAMICOS};

        int combinacionesOk = 0;
        int combinaciones = 0;
        for (Object tratamiento : tratamientos) {
            double base = tarifario.precioDe(tratamiento);
            for (boolean os : new boolean[]{false, true}) {
                for (MedioPago medio : new MedioPago[]{new Tarjeta(), new Efectivo()}) {
                    double descOs = os ? redondear(base * 0.30) : 0.0;
                    double tras = base - descOs;
                    double descMedio = redondear(tras * medio.getPorcentajeDescuento());
                    double total = redondear(base - descOs - descMedio);

                    DesgloseMonto d = calc.calcular(base, os, medio);
                    combinaciones++;
                    if (Math.abs(d.getDescuentoObraSocial() - descOs) < 0.005
                            && Math.abs(d.getDescuentoEfectivo() - descMedio) < 0.005
                            && Math.abs(d.getMontoTotal() - total) < 0.005
                            && Math.abs(d.getMontoBase() - base) < 0.005) {
                        combinacionesOk++;
                    } else {
                        v.verdadero("combinacion " + tratamiento + " os=" + os + " medio=" + medio.getNombre(), false);
                    }
                }
            }
        }
        v.igual("las 40 combinaciones (10 tratamientos x obra social x medio) coinciden", combinaciones, combinacionesOk);

        DesgloseMonto implante = calc.calcular(450000, true, new Efectivo());
        v.casi("implante + obra social + efectivo: desc. obra social", 135000, implante.getDescuentoObraSocial());
        v.casi("implante + obra social + efectivo: desc. efectivo", 31500, implante.getDescuentoEfectivo());
        v.casi("implante + obra social + efectivo: total", 283500, implante.getMontoTotal());

        DesgloseMonto sinMedio = calc.calcular(25000, true);
        v.casi("vista previa sin medio: total con obra social", 17500, sinMedio.getMontoTotal());
        v.casi("vista previa sin medio: sin descuento de medio", 0, sinMedio.getDescuentoEfectivo());

        v.lanza("base en cero", DatoInvalidoException.class, () -> calc.calcular(0, false, new Efectivo()));
        v.lanza("base negativa", DatoInvalidoException.class, () -> calc.calcular(-5, false));
        v.lanza("medio de pago nulo", DatoInvalidoException.class, () -> calc.calcular(1000, false, null));
    }

    // ------------------------------------------------------------------ facturacion

    private static void facturacion() throws Exception {
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo());
        ServicioTurno st = new ServicioTurno(new RepositorioTurno());
        MedioPago mp = new MercadoPago();
        MedioPago tj = new Tarjeta();
        MedioPago ef = new Efectivo();
        ServicioFactura sf = new ServicioFactura(new RepositorioFactura(), st, new Tarifario(),
                new CalculadorMonto(), List.of(mp, tj, ef));

        Paciente conOs = sp.registrarPaciente("Fatima", "Persi", 2001, "f@x.com", null, true);
        Paciente sinOs = sp.registrarPaciente("Tomas", "Barea", 2002, "t@x.com", null, false);
        Odontologo o = so.registrarOdontologo(new Cirujano(0L, "Blas", "Pesce", 33, TipoCirugia.IMPLANTE));
        LocalDate manana = LocalDate.now().plusDays(1);

        Turno tA = st.registrarTurno(conOs, o, manana, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        Turno tB = st.registrarTurno(sinOs, o, manana, LocalTime.of(10, 0), TipoCirugia.IMPLANTE);
        Turno tD = st.registrarTurno(sinOs, o, manana, LocalTime.of(11, 0)); // sin motivo
        Turno tC = st.registrarTurno(sinOs, o, manana, LocalTime.of(12, 0), TipoConsulta.CONTROL);
        st.cancelarTurno(tC.getId());

        List<Turno> porCobrar = sf.listarTurnosPorCobrar();
        v.igual("por cobrar: no incluye al cancelado", 3, porCobrar.size());
        v.igual("por cobrar: ordenados por fecha y hora (1ro)", tA.getId(), porCobrar.get(0).getId());
        v.igual("por cobrar: ordenados por fecha y hora (3ro)", tD.getId(), porCobrar.get(2).getId());

        List<MedioPago> medios = sf.listarMediosPago();
        v.igual("3 medios de pago", 3, medios.size());
        medios.clear();
        v.igual("la lista de medios es una copia", 3, sf.listarMediosPago().size());

        DesgloseMonto previa = sf.calcularVistaPrevia(tA.getId());
        v.casi("vista previa: base", 25000, previa.getMontoBase());
        v.casi("vista previa: desc. obra social", 7500, previa.getDescuentoObraSocial());
        v.casi("vista previa: total sin medio", 17500, previa.getMontoTotal());
        v.casi("vista previa con efectivo", 15750, sf.calcularVistaPrevia(tA.getId(), ef).getMontoTotal());
        v.lanza("vista previa de un turno sin tratamiento", DatoInvalidoException.class,
                () -> sf.calcularVistaPrevia(tD.getId()));
        v.lanza("vista previa de un turno cancelado", DatoInvalidoException.class,
                () -> sf.calcularVistaPrevia(tC.getId()));

        Factura f1 = sf.cobrarTurno(tA.getId(), ef);
        v.igual("factura 1: numero", 1L, f1.getId());
        v.igual("factura 1: turno", tA.getId(), f1.getTurnoId());
        v.igual("factura 1: paciente", conOs.getId(), f1.getPacienteId());
        v.igual("factura 1: nombre del paciente (texto)", conOs.getNombreCompleto(), f1.getPacienteNombre());
        v.igual("factura 1: concepto (texto)", "LIMPIEZA", f1.getConcepto());
        v.igual("factura 1: fecha de hoy", LocalDate.now(), f1.getFecha());
        v.igual("factura 1: medio (nombre)", "Efectivo", f1.getMetodoPago());
        v.casi("factura 1: base", 25000, f1.getMontoBase());
        v.casi("factura 1: desc. obra social", 7500, f1.getDescuentoObraSocial());
        v.casi("factura 1: desc. efectivo", 1750, f1.getDescuentoEfectivo());
        v.casi("factura 1: total", 15750, f1.getMontoTotal());
        v.igual("despues de cobrar quedan 2 por cobrar", 2, sf.listarTurnosPorCobrar().size());

        v.lanza("cobrar de nuevo el mismo turno", DatoInvalidoException.class, () -> sf.cobrarTurno(tA.getId(), ef));
        v.lanza("cobrar sin medio de pago", DatoInvalidoException.class, () -> sf.cobrarTurno(tB.getId(), null));
        v.lanza("cobrar un turno cancelado", DatoInvalidoException.class, () -> sf.cobrarTurno(tC.getId(), ef));
        v.lanza("cobrar un turno sin tratamiento", DatoInvalidoException.class, () -> sf.cobrarTurno(tD.getId(), ef));
        v.lanza("cobrar un turno inexistente", DatoInvalidoException.class, () -> sf.cobrarTurno(999, ef));
        v.igual("los cobros fallidos no generan facturas", 1, sf.listarTodos().size());

        Factura f2 = sf.cobrarTurno(tB.getId(), tj);
        v.igual("factura 2: numero", 2L, f2.getId());
        v.casi("factura 2: total (implante sin obra social, tarjeta)", 450000, f2.getMontoTotal());

        // ---- cierre del dia
        CierreDelDia hoy = sf.cerrarDia(LocalDate.now());
        v.igual("cierre de hoy: cantidad", 2, hoy.getCantidadFacturas());
        v.casi("cierre de hoy: total", 465750, hoy.getMontoTotal());
        Map<String, Double> porMedio = hoy.getTotalPorMedioPago();
        v.casi("cierre de hoy: Mercado Pago", 0, porMedio.get("Mercado Pago"));
        v.casi("cierre de hoy: Tarjeta", 450000, porMedio.get("Tarjeta"));
        v.casi("cierre de hoy: Efectivo", 15750, porMedio.get("Efectivo"));
        v.igual("cierre de hoy: orden de los medios", List.of("Mercado Pago", "Tarjeta", "Efectivo"),
                List.copyOf(porMedio.keySet()));
        CierreDelDia vacio = sf.cerrarDia(LocalDate.now().plusDays(1));
        v.igual("cierre sin facturas: cantidad", 0, vacio.getCantidadFacturas());
        v.casi("cierre sin facturas: total", 0, vacio.getMontoTotal());
        v.igual("cierre sin facturas: igual lista todos los medios", 3, vacio.getTotalPorMedioPago().size());
        v.lanza("cierre con fecha nula", DatoInvalidoException.class, () -> sf.cerrarDia(null));
        v.lanza("el mapa del cierre no se puede modificar", UnsupportedOperationException.class,
                () -> hoy.getTotalPorMedioPago().put("X", 1.0));

        List<Factura> todas = sf.listarTodos();
        v.igual("listarTodos ordenadas por numero (1ra)", 1L, todas.get(0).getId());
        v.igual("listarTodos ordenadas por numero (2da)", 2L, todas.get(1).getId());
    }

    // ------------------------------------------------------------------ repositorios

    private static void repositorios() throws Exception {
        RepositorioPaciente rp = new RepositorioPaciente();
        v.verdadero("buscarPorId inexistente devuelve null", rp.buscarPorId(1L) == null);
        v.verdadero("buscarPorDni inexistente devuelve null", rp.buscarPorDni(5) == null);
        Paciente p = new Paciente(1L, "A", "B", 5, "a@b.com", LocalDate.now(), null, false);
        rp.guardar(p);
        v.verdadero("buscarPorId", rp.buscarPorId(1L) == p);
        v.verdadero("buscarPorDni", rp.buscarPorDni(5) == p);
        v.verdadero("existeDni", rp.existeDni(5));
        v.verdadero("no existeDni", !rp.existeDni(6));
        rp.actualizar(new Paciente(77L, "Z", "Z", 9, "z@z.com", LocalDate.now(), null, false));
        v.igual("actualizar un id inexistente no agrega nada", 1, rp.listarTodos().size());
        rp.eliminar(1L);
        v.igual("eliminar", 0, rp.listarTodos().size());

        RepositorioTurno rt = new RepositorioTurno();
        v.igual("siguienteId de turnos vacio", 1L, rt.siguienteId());
        Paciente pac = new Paciente(1L, "A", "B", 5, "a@b.com", LocalDate.now(), null, false);
        Odontologo od = new General(4L, "O", "D", 44, TipoConsulta.CONTROL);
        LocalDate dia = LocalDate.now().plusDays(1);
        Turno t = new Turno(7L, pac, od, dia, LocalTime.of(9, 0), EstadoTurno.PENDIENTE, TipoConsulta.CONTROL);
        rt.guardar(t);
        v.igual("siguienteId de turnos = maximo + 1", 8L, rt.siguienteId());
        v.verdadero("existeTurnoSolapado", rt.existeTurnoSolapado(4L, dia, LocalTime.of(9, 0)));
        v.verdadero("no hay solapado en otro horario", !rt.existeTurnoSolapado(4L, dia, LocalTime.of(10, 0)));
        v.verdadero("no hay solapado con otro odontologo", !rt.existeTurnoSolapado(5L, dia, LocalTime.of(9, 0)));
        t.cancelar();
        v.verdadero("el repositorio ignora los cancelados al buscar solapados", !rt.existeTurnoSolapado(4L, dia, LocalTime.of(9, 0)));
        v.igual("buscarPorPaciente", 1, rt.buscarPorPaciente(1L).size());
        v.igual("buscarPorOdontologo", 1, rt.buscarPorOdontologo(4L).size());

        RepositorioFactura rf = new RepositorioFactura();
        v.igual("siguienteId de facturas vacio", 1L, rf.siguienteId());
        LocalDate hoy = LocalDate.now();
        rf.guardar(factura(1L, 10L, 1L, "A B", "LIMPIEZA", hoy, "Efectivo", 100, 0, 10, 90));
        rf.guardar(factura(2L, 11L, 1L, "A B", "CONTROL", hoy.minusDays(1), "Tarjeta", 100, 0, 0, 100));
        v.igual("siguienteId de facturas", 3L, rf.siguienteId());
        v.verdadero("existePorTurno", rf.existePorTurno(10L));
        v.verdadero("no existePorTurno", !rf.existePorTurno(99L));
        v.igual("listarPorFecha hoy", 1, rf.listarPorFecha(hoy).size());
        v.igual("listarPorFecha ayer", 1, rf.listarPorFecha(hoy.minusDays(1)).size());
        v.igual("listarPorFecha sin facturas", 0, rf.listarPorFecha(hoy.plusDays(3)).size());
    }

    // ------------------------------------------------------------------ persistencia

    private static void persistenciaPacientes() throws Exception {
        ServicioPaciente origen = new ServicioPaciente(new RepositorioPaciente());
        Domicilio dom = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
        Domicilio dom2 = new Domicilio("San Martin", 1200, "Moron", "Buenos Aires", TipoHogar.DEPARTAMENTO);
        origen.registrarPaciente("Fatima", "Persi", 100, "f@x.com", dom, true);
        origen.registrarPaciente("Tomas", "Barea", 200, "t@x.com", null, false);
        origen.registrarPaciente("Maria", "Garcia", 300, "m@x.com", dom2, true);

        PersistenciaPaciente.guardar(origen.listarTodos());

        ServicioPaciente destino = new ServicioPaciente(new RepositorioPaciente());
        PersistenciaPaciente.cargar(destino);
        v.igual("se cargan los 3 pacientes", 3, destino.listarTodos().size());
        for (Paciente esperado : origen.listarTodos()) {
            Paciente real = destino.buscarPorId(esperado.getId());
            String quien = "paciente " + esperado.getId();
            v.igual(quien + ": nombre", esperado.getNombre(), real.getNombre());
            v.igual(quien + ": apellido", esperado.getApellido(), real.getApellido());
            v.igual(quien + ": dni", esperado.getDni(), real.getDni());
            v.igual(quien + ": email", esperado.getEmail(), real.getEmail());
            v.igual(quien + ": fecha de alta", esperado.getFechaAlta(), real.getFechaAlta());
            v.igual(quien + ": obra social", esperado.tieneObraSocial(), real.tieneObraSocial());
            v.igual(quien + ": tiene domicilio", esperado.tieneDomicilio(), real.tieneDomicilio());
            if (esperado.tieneDomicilio()) {
                v.igual(quien + ": calle", esperado.getDomicilio().getCalle(), real.getDomicilio().getCalle());
                v.igual(quien + ": numero", esperado.getDomicilio().getNumero(), real.getDomicilio().getNumero());
                v.igual(quien + ": localidad", esperado.getDomicilio().getLocalidad(), real.getDomicilio().getLocalidad());
                v.igual(quien + ": provincia", esperado.getDomicilio().getProvincia(), real.getDomicilio().getProvincia());
                v.igual(quien + ": hogar", esperado.getDomicilio().getHogar(), real.getDomicilio().getHogar());
            }
        }

        // formato viejo (sin obra social, 6 y 11 columnas) y una linea rota
        escribir("pacientes.txt",
                "1,Ana,Lopez,111,a@x.com,2024-01-01",
                "2,Luis,Perez,222,l@x.com,2024-02-02,Belgrano,430,San Justo,Buenos Aires,CASA",
                "esto,no,es,un,paciente");
        ServicioPaciente viejo = new ServicioPaciente(new RepositorioPaciente());
        String salida = capturarSalida(() -> PersistenciaPaciente.cargar(viejo));
        v.igual("formato viejo: se cargan las 2 lineas validas", 2, viejo.listarTodos().size());
        v.verdadero("formato viejo: obra social en false", !viejo.buscarPorId(1).tieneObraSocial());
        v.verdadero("formato viejo de 6 columnas: sin domicilio", !viejo.buscarPorId(1).tieneDomicilio());
        v.igual("formato viejo de 11 columnas: calle", "Belgrano", viejo.buscarPorId(2).getDomicilio().getCalle());
        v.verdadero("la linea rota se informa", salida.contains("Linea corrupta ignorada"));

        borrar("pacientes.txt");
        ServicioPaciente sinArchivo = new ServicioPaciente(new RepositorioPaciente());
        PersistenciaPaciente.cargar(sinArchivo);
        v.igual("sin archivo no carga nada ni falla", 0, sinArchivo.listarTodos().size());
    }

    private static void persistenciaOdontologos() throws Exception {
        ServicioOdontologo origen = new ServicioOdontologo(new RepositorioOdontologo());
        origen.registrarOdontologo(new General(0L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA));
        origen.registrarOdontologo(new Ortodoncista(0L, "Nahuel", "Torres", 2245, TipoOrtodoncia.BRACKETS_METALICOS));
        origen.registrarOdontologo(new Cirujano(0L, "Blas", "Pesce", 3367, TipoCirugia.EXTRACCION_MUELA));

        PersistenciaOdontologo.guardar(origen.listarTodos());

        ServicioOdontologo destino = new ServicioOdontologo(new RepositorioOdontologo());
        PersistenciaOdontologo.cargar(destino);
        v.igual("se cargan los 3 odontologos", 3, destino.listarTodos().size());

        Odontologo o1 = destino.buscarPorId(1);
        Odontologo o2 = destino.buscarPorId(2);
        Odontologo o3 = destino.buscarPorId(3);
        v.verdadero("el 1 vuelve como General", o1 instanceof General);
        v.verdadero("el 2 vuelve como Ortodoncista", o2 instanceof Ortodoncista);
        v.verdadero("el 3 vuelve como Cirujano", o3 instanceof Cirujano);
        v.igual("General: especialidad", TipoConsulta.LIMPIEZA, ((General) o1).getTipoConsulta());
        v.igual("Ortodoncista: especialidad", TipoOrtodoncia.BRACKETS_METALICOS, ((Ortodoncista) o2).getTipoOrtodoncia());
        v.igual("Cirujano: especialidad", TipoCirugia.EXTRACCION_MUELA, ((Cirujano) o3).getTipoCirugia());
        v.igual("nombre", "Magali", o1.getNombre());
        v.igual("apellido", "Pesqueira", o1.getApellido());
        v.igual("matricula", 1131, o1.getMatricula());

        // el formato del archivo no cambio: se leen lineas guardadas antes de la etapa 16
        escribir("odontologos.txt",
                "1,General,Ana,Paz,10,LIMPIEZA",
                "2,Odontologo,Base,Vieja,20,",
                "3,Cirujano,Luis,Gil,30,IMPLANTE");
        ServicioOdontologo viejo = new ServicioOdontologo(new RepositorioOdontologo());
        PersistenciaOdontologo.cargar(viejo);
        v.igual("un tipo desconocido se saltea y se cargan los demas (actual)", 2, viejo.listarTodos().size());
        v.verdadero("la linea de Cirujano se carga con su motivo",
                viejo.buscarPorId(3) instanceof Cirujano c && c.getTipoCirugia() == TipoCirugia.IMPLANTE);

        // lo que se guarda conserva los nombres de especialidad de siempre
        PersistenciaOdontologo.guardar(viejo.listarTodos());
        List<String> lineas = Files.readAllLines(Path.of("odontologos.txt"));
        v.verdadero("se escribe General, Cirujano u Ortodoncista (sin cambios en el formato)",
                lineas.contains("1,General,Ana,Paz,10,LIMPIEZA") && lineas.contains("3,Cirujano,Luis,Gil,30,IMPLANTE"));
    }

    private static void persistenciaTurnos() throws Exception {
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo());
        ServicioTurno st = new ServicioTurno(new RepositorioTurno());
        Paciente p = sp.registrarPaciente("Fatima", "Persi", 100, "f@x.com", null, true);
        Odontologo o = so.registrarOdontologo(new General(0L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA));
        LocalDate dia = LocalDate.now().plusDays(2);

        Turno a = st.registrarTurno(p, o, dia, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        Turno b = st.registrarTurno(p, o, dia, LocalTime.of(10, 0), TipoConsulta.CONTROL);
        st.confirmarTurno(b.getId());
        Turno c = st.registrarTurno(p, o, dia, LocalTime.of(11, 0)); // sin motivo
        st.cancelarTurno(c.getId());

        PersistenciaTurno.guardar(st.listarTodos());

        ServicioTurno stNuevo = new ServicioTurno(new RepositorioTurno());
        PersistenciaTurno.cargar(stNuevo, sp, so);
        v.igual("se cargan los 3 turnos", 3, stNuevo.listarTodos().size());

        Turno ra = stNuevo.buscarPorId(a.getId());
        v.igual("turno a: fecha", dia, ra.getFecha());
        v.igual("turno a: hora", LocalTime.of(9, 0), ra.getHora());
        v.igual("turno a: estado", EstadoTurno.PENDIENTE, ra.getEstado());
        v.igual("turno a: paciente", p.getId(), ra.getPaciente().getId());
        v.igual("turno a: odontologo", o.getId(), ra.getOdontologo().getId());
        v.igual("turno a: el motivo vuelve tipado", TipoConsulta.LIMPIEZA, ra.getMotivoConsulta().orElse(null));
        v.igual("turno b: estado confirmado", EstadoTurno.CONFIRMADO, stNuevo.buscarPorId(b.getId()).getEstado());
        v.igual("turno c: estado cancelado", EstadoTurno.CANCELADO, stNuevo.buscarPorId(c.getId()).getEstado());
        v.verdadero("turno c: sin motivo vuelve sin motivo", stNuevo.buscarPorId(c.getId()).getMotivoConsulta().isEmpty());
        v.verdadero("turno c: en el archivo sigue guardado como 'Consulta' (formato sin cambios)",
                Files.readAllLines(Path.of("turnos.txt")).stream().anyMatch(l -> l.endsWith(",CANCELADO,Consulta")));

        // un turno ausente y un completado tambien hacen el viaje de ida y vuelta
        Turno d = st.registrarTurno(p, o, dia, LocalTime.of(12, 0), TipoConsulta.URGENCIA);
        st.getRepositorio().guardar(new Turno(d.getId(), p, o, dia, LocalTime.of(12, 0), EstadoTurno.AUSENTE, TipoConsulta.URGENCIA));
        Turno e = st.registrarTurno(p, o, dia, LocalTime.of(13, 0), TipoConsulta.CONTROL);
        st.completarTurno(e.getId());
        PersistenciaTurno.guardar(st.listarTodos());
        ServicioTurno stEstados = new ServicioTurno(new RepositorioTurno());
        PersistenciaTurno.cargar(stEstados, sp, so);
        v.igual("turno ausente: estado", EstadoTurno.AUSENTE, stEstados.buscarPorId(d.getId()).getEstado());
        v.igual("turno completado: estado", EstadoTurno.COMPLETADO, stEstados.buscarPorId(e.getId()).getEstado());
        v.igual("al recargar, todos los turnos siguen siendo iguales a los guardados", 5, stEstados.listarTodos().size());

        // un motivo que no es de ningun tratamiento deja el turno sin motivo (antes quedaba como texto suelto)
        escribir("turnos.txt", "1,1,1," + dia + ",09:00,PENDIENTE,XYZ");
        ServicioTurno desconocido = new ServicioTurno(new RepositorioTurno());
        PersistenciaTurno.cargar(desconocido, sp, so);
        v.igual("motivo desconocido: el turno se carga", 1, desconocido.listarTodos().size());
        v.verdadero("motivo desconocido: queda sin motivo", desconocido.buscarPorId(1).getMotivoConsulta().isEmpty());

        // un turno con un paciente inexistente se ignora, el resto se carga
        escribir("turnos.txt",
                "1,1,1," + dia + ",09:00,PENDIENTE,LIMPIEZA",
                "2,99,1," + dia + ",10:00,PENDIENTE,CONTROL",
                "basura");
        ServicioTurno parcial = new ServicioTurno(new RepositorioTurno());
        String salida = capturarSalida(() -> PersistenciaTurno.cargar(parcial, sp, so));
        v.igual("las lineas invalidas se saltean", 1, parcial.listarTodos().size());
        v.verdadero("se informa la linea corrupta", salida.contains("Linea corrupta ignorada"));
    }

    private static void persistenciaFacturas() throws Exception {
        LocalDate hoy = LocalDate.now();
        List<Factura> facturas = List.of(
                factura(1L, 10L, 1L, "Fatima Persi", "LIMPIEZA", hoy, "Efectivo", 25000, 7500, 1750, 15750),
                factura(2L, 11L, 2L, "Barea, Tomas", "IMPLANTE", hoy.minusDays(1), "Tarjeta", 450000, 0, 0, 450000));
        PersistenciaFactura.guardar(facturas);

        // le agrega una linea rota al final
        java.nio.file.Files.writeString(Path.of("facturas.txt"), "esto,no,es,una,factura\n",
                java.nio.file.StandardOpenOption.APPEND);

        ServicioFactura destino = new ServicioFactura(new RepositorioFactura(),
                new ServicioTurno(new RepositorioTurno()), new Tarifario(), new CalculadorMonto(),
                List.of(new MercadoPago(), new Tarjeta(), new Efectivo()));
        String salida = capturarSalida(() -> PersistenciaFactura.cargar(destino));
        List<Factura> cargadas = destino.listarTodos();
        v.igual("se cargan las 2 facturas validas", 2, cargadas.size());
        v.verdadero("se informa la linea rota", salida.contains("Linea corrupta ignorada"));

        Factura f1 = cargadas.get(0);
        v.igual("factura 1: turno", 10L, f1.getTurnoId());
        v.igual("factura 1: paciente", "Fatima Persi", f1.getPacienteNombre());
        v.igual("factura 1: concepto", "LIMPIEZA", f1.getConcepto());
        v.igual("factura 1: fecha", hoy, f1.getFecha());
        v.igual("factura 1: medio", "Efectivo", f1.getMetodoPago());
        v.casi("factura 1: base", 25000, f1.getMontoBase());
        v.casi("factura 1: desc. obra social", 7500, f1.getDescuentoObraSocial());
        v.casi("factura 1: desc. efectivo", 1750, f1.getDescuentoEfectivo());
        v.casi("factura 1: total", 15750, f1.getMontoTotal());
        v.igual("la coma del nombre se reemplaza por un espacio (actual)", "Barea  Tomas", cargadas.get(1).getPacienteNombre());
        v.igual("el proximo numero de factura sigue al maximo", 3L, destino.getRepositorio().siguienteId());
    }

    // ------------------------------------------------------------------ utilidades de prueba

    // arma una factura con la misma lista de datos que usaba el constructor viejo (solo para escribir menos en las pruebas)
    private static Factura factura(long id, long turnoId, long pacienteId, String paciente, String concepto,
                                   LocalDate fecha, String medio, double base, double descOs, double descMedio, double total) {
        return Factura.builder().id(id).turnoId(turnoId).pacienteId(pacienteId).pacienteNombre(paciente)
                .concepto(concepto).fecha(fecha).metodoPago(medio)
                .desglose(new DesgloseMonto(base, descOs, descMedio, total)).build();
    }

    // mensaje de la excepcion que lanza la accion ("" si no lanza nada)
    private static String mensajeDe(Verificador.Ejecutable accion) {
        try {
            accion.ejecutar();
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static void escribir(String archivo, String... lineas) throws IOException {
        Files.writeString(Path.of(archivo), String.join("\n", lineas) + "\n");
    }

    private static void borrar(String archivo) throws IOException {
        Files.deleteIfExists(Path.of(archivo));
    }

    // ejecuta algo y devuelve lo que imprimio por consola (despues vuelve a imprimir normal)
    private static String capturarSalida(Verificador.Ejecutable accion) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, "UTF-8"));
        try {
            accion.ejecutar();
        } finally {
            System.setOut(original);
        }
        return buffer.toString("UTF-8");
    }

    // mueve los .txt reales a una carpeta temporal para que las pruebas no los pisen
    private static Path respaldarArchivos() throws IOException {
        Path carpeta = Files.createTempDirectory("respaldo-pruebas-");
        for (String nombre : ARCHIVOS) {
            Path origen = Path.of(nombre);
            if (Files.exists(origen)) {
                Files.move(origen, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return carpeta;
    }

    // borra lo que dejaron las pruebas y devuelve los .txt reales a su lugar
    private static void restaurarArchivos(Path carpeta) throws IOException {
        for (String nombre : ARCHIVOS) {
            Files.deleteIfExists(Path.of(nombre));
            Path guardado = carpeta.resolve(nombre);
            if (Files.exists(guardado)) {
                Files.move(guardado, Path.of(nombre), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.deleteIfExists(carpeta);
    }
}