package pruebas;

import entity.*;
import controller.*;
import exception.DatoInvalidoException;
import exception.OdontologoNoEncontradoException;
import exception.PacienteNoEncontradoException;
import exception.TurnoYaReservadoException;
import exception.TurnoNoEncontradoException;
import pago.Efectivo;
import pago.MedioPago;
import pago.MercadoPago;
import pago.Tarjeta;
import repository.GeneradorIds;
import repository.IRepositorio;
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
import service.HorarioAtencion;
import service.Tarifario;
import persistence.*;
import uiSwing.TextoMotivo;
import uiSwing.FormatoPlata;
import uiSwing.PaletaEstadoTurno;
import uiSwing.ComprobanteFactura;
import uiSwing.TurnoTableModel;
import uiSwing.OpcionMotivo;
import uiSwing.SeccionContenido;
import uiSwing.Sidebar;
import ui.LectorConsola;
import ui.MenuConsola;
import ui.OpcionMenu;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
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
 * Las pruebas de persistencia trabajan con carpetas temporales aisladas y no modifican los datos reales.
 */
public class PruebasBase {

    private static final Verificador v = new Verificador();
    public static void main(String[] args) throws Exception {
        v.seccion("Entidades: enums y Domicilio (Etapa 15)", PruebasBase::enumsYDomicilio);
        v.seccion("Entidades: personas (Etapa 16)", PruebasBase::personas);
        v.seccion("Entidades: turno y factura (Etapa 17)", PruebasBase::turnoYFactura);
        v.seccion("Pacientes (servicio)", PruebasBase::pacientes);
        v.seccion("Odontologos (servicio)", PruebasBase::odontologos);
        v.seccion("Turnos (servicio)", PruebasBase::turnos);
        v.seccion("Horario configurable y reloj de turnos", PruebasBase::horarioConfigurableYClock);
        v.seccion("Tarifario", PruebasBase::tarifario);
        v.seccion("Medios de pago", PruebasBase::mediosDePago);
        v.seccion("CalculadorMonto", PruebasBase::calculador);
        v.seccion("Facturacion (servicio)", PruebasBase::facturacion);
        v.seccion("Controladores y DTO", PruebasBase::controladores);
        v.seccion("Repositorios", PruebasBase::repositorios);
        v.seccion("Persistencia de pacientes", PruebasBase::persistenciaPacientes);
        v.seccion("Persistencia de odontologos", PruebasBase::persistenciaOdontologos);
        v.seccion("Persistencia de turnos", PruebasBase::persistenciaTurnos);
        v.seccion("Persistencia de facturas", PruebasBase::persistenciaFacturas);
        v.seccion("Persistencia integrada y rutas configurables", PruebasBase::persistenciaIntegrada);
        v.seccion("UI consola: lecturas y comandos", PruebasBase::uiConsola);
        v.seccion("Swing: piezas compartidas", PruebasBase::uiSwingCompartida);
        v.seccion("Swing: formularios de personas", PruebasBase::uiSwingPersonas);
        v.seccion("Swing: tabla y opciones polimórficas de turnos", PruebasBase::uiSwingTurnos);
        boolean ok = v.resumen();
        System.exit(ok ? 0 : 1);
    }


    // ------------------------------------------------------------------ UI consola (etapa 25)

    private static void uiConsola() {
        String entradas = String.join("\n", "abc", "42", "nope", "9223372036854775807",
                "2026-99-99", "2026-10-30", "25:00", "09:30", "quiz", "s") + "\n";
        LectorConsola lector = new LectorConsola(new Scanner(
                new ByteArrayInputStream(entradas.getBytes(StandardCharsets.UTF_8))));
        v.igual("LectorConsola reintenta hasta leer un entero", 42, lector.leerEntero("entero: "));
        v.igual("LectorConsola reintenta hasta leer un long", Long.MAX_VALUE,
                lector.leerLong("long: "));
        v.igual("LectorConsola reintenta hasta leer una fecha válida", LocalDate.of(2026, 10, 30),
                lector.leerFecha("fecha: "));
        v.igual("LectorConsola reintenta hasta leer una hora válida", LocalTime.of(9, 30),
                lector.leerHora("hora: "));
        v.verdadero("LectorConsola acepta S como sí", lector.leerSiNo("sí/no: "));

        AtomicInteger ejecuciones = new AtomicInteger();
        String menuEntradas = "99\n1\n0\n";
        MenuConsola menu = new MenuConsola(new LectorConsola(new Scanner(
                new ByteArrayInputStream(menuEntradas.getBytes(StandardCharsets.UTF_8)))));
        menu.ejecutar("Prueba", List.of(new OpcionMenu(1, "Ejecutar", ejecuciones::incrementAndGet)));
        v.igual("MenuConsola ejecuta el comando seleccionado una sola vez", 1, ejecuciones.get());
        v.lanza("MenuConsola rechaza números de opción duplicados", IllegalArgumentException.class,
                () -> menu.ejecutar("Inválido", List.of(
                        new OpcionMenu(1, "A", () -> {}), new OpcionMenu(1, "B", () -> {}))));
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
                .fecha(hoy).metodoPago("EFECTIVO").desglose(desglose).build();
        v.igual("factura: id", 1L, f.getId());
        v.igual("factura: turno", 10L, f.getTurnoId());
        v.igual("factura: paciente", 5L, f.getPacienteId());
        v.igual("factura: nombre", "A B", f.getPacienteNombre());
        v.igual("factura: concepto", "LIMPIEZA", f.getConcepto());
        v.igual("factura: fecha", hoy, f.getFecha());
        v.igual("factura: medio (codigo)", "EFECTIVO", f.getMetodoPagoCodigo());
        v.verdadero("factura: contiene el desglose recibido", f.getDesglose() == desglose);
        v.casi("factura: base (atajo al desglose)", 100, f.getMontoBase());
        v.casi("factura: desc. obra social (atajo)", 0, f.getDescuentoObraSocial());
        v.casi("factura: desc. medio de pago (atajo)", 10, f.getDescuentoMedioPago());
        v.casi("factura: total (atajo)", 90, f.getMontoTotal());
        v.igual("DesgloseMonto ahora vive en entity", "entity", DesgloseMonto.class.getPackageName());
        v.igual("toString de la factura",
                "FACTURA #1 | Turno: 10 | Paciente: A B | Concepto: LIMPIEZA | Fecha: " + hoy
                        + " | Medio (codigo): EFECTIVO | Base: $100.0 | Desc. obra social: $0.0 | Desc. medio de pago: $10.0 | Total: $90.0",
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
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente(), new RepositorioTurno());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo(), new RepositorioTurno());
        RepositorioTurno repoTurnosFac = new RepositorioTurno();
        ServicioTurno stFac = new ServicioTurno(repoTurnosFac);
        ServicioFactura sf = new ServicioFactura(new RepositorioFactura(), stFac, new Tarifario(), new CalculadorMonto(),
                List.of(new Efectivo()));
        Paciente p = sp.registrarPaciente("Fatima", "Persi", 3001, "f@x.com", null, false);
        Odontologo o = so.registrarOdontologo(new General(0L, "Magali", "Pesqueira", 31, TipoConsulta.LIMPIEZA));
        LocalDate manana = LocalDate.now().plusDays(1);
        Turno aus = stFac.registrarTurno(p, o, manana, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA);
        repoTurnosFac.actualizar(new Turno(aus.getId(), p, o, manana, LocalTime.of(9, 0), EstadoTurno.AUSENTE, TipoConsulta.LIMPIEZA));
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
        RepositorioTurno repositorioTurnos = new RepositorioTurno();
        Clock relojFijo = Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC);
        ServicioPaciente s = new ServicioPaciente(new RepositorioPaciente(), repositorioTurnos, relojFijo);
        Domicilio d = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);

        Paciente persi = s.registrarPaciente("Fatima", "Persi", 46113226, "fatimap@gmail.com", d, true);
        v.igual("el primer paciente tiene id 1", 1L, persi.getId());
        v.igual("la fecha de alta usa el Clock inyectado", LocalDate.of(2026, 6, 15), persi.getFechaAlta());
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

        // integridad referencial: no se elimina una persona con turnos asociados
        ServicioOdontologo odontologosConTurnos = new ServicioOdontologo(new RepositorioOdontologo(), repositorioTurnos);
        Paciente pacienteConTurno = s.registrarPaciente("Laura", "Diaz", 38221457, "l@x.com", null, false);
        Odontologo odontologoConTurno = odontologosConTurnos.registrarOdontologo(
                new General(0L, "Sofia", "Luna", 7788, TipoConsulta.CONTROL));
        ServicioTurno turnosServicio = new ServicioTurno(repositorioTurnos);
        turnosServicio.registrarTurno(pacienteConTurno, odontologoConTurno,
                LocalDate.now().plusDays(2), LocalTime.of(9, 0), TipoConsulta.CONTROL);
        v.lanza("no elimina paciente con turnos asociados", DatoInvalidoException.class,
                () -> s.eliminarPaciente(pacienteConTurno.getId()));
        v.lanza("no elimina odontologo con turnos asociados", DatoInvalidoException.class,
                () -> odontologosConTurnos.eliminarOdontologo(odontologoConTurno.getId()));

    }

    // ------------------------------------------------------------------ odontologos

    private static void odontologos() throws Exception {
        ServicioOdontologo s = new ServicioOdontologo(new RepositorioOdontologo(), new RepositorioTurno());

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

        Odontologo act = s.actualizarOdontologo(1, "Magali B", "Pesqueira",
                1144, Especialidad.CIRUJANO, TipoCirugia.IMPLANTE);
        v.igual("actualiza el nombre", "Magali B", act.getNombre());
        v.igual("actualiza la matrícula", 1144, act.getMatricula());
        v.igual("actualiza la especialidad", Especialidad.CIRUJANO, act.getEspecialidad());
        v.igual("actualiza la subespecialidad", TipoCirugia.IMPLANTE, act.getSubespecialidad());
        v.lanza("no permite matrícula duplicada al actualizar", DatoInvalidoException.class,
                () -> s.actualizarOdontologo(1, "Magali", "Pesqueira", 3367,
                        Especialidad.GENERAL, TipoConsulta.CONTROL));
        v.lanza("no permite subespecialidad ajena a la especialidad", DatoInvalidoException.class,
                () -> s.actualizarOdontologo(1, "Magali", "Pesqueira", 1144,
                        Especialidad.GENERAL, TipoCirugia.IMPLANTE));
        v.lanza("actualizar con apellido vacio", DatoInvalidoException.class,
                () -> s.actualizarOdontologo(1, "Magali", "", 1144,
                        Especialidad.GENERAL, TipoConsulta.CONTROL));

        s.eliminarOdontologo(2);
        v.igual("eliminar", 1, s.listarTodos().size());
        v.lanza("eliminar inexistente", OdontologoNoEncontradoException.class, () -> s.eliminarOdontologo(2));
    }

    // ------------------------------------------------------------------ turnos

    private static void turnos() throws Exception {
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente(), new RepositorioTurno());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo(), new RepositorioTurno());
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
        Turno reemplazo = st.registrarTurno(p2, o1, manana, LocalTime.of(15, 0), TipoConsulta.CONTROL);
        v.verdadero("un turno cancelado libera el horario", reemplazo.getId() != c.getId());
        st.cancelarTurno(reemplazo.getId());
        st.eliminarTurno(reemplazo.getId());
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

    private static void horarioConfigurableYClock() throws Exception {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-01T10:00:00Z"), ZoneOffset.UTC);
        LocalDate hoy = LocalDate.now(reloj);
        RepositorioTurno repositorio = new RepositorioTurno();
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente(), new RepositorioTurno(), reloj);
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo(), new RepositorioTurno());
        Paciente paciente = sp.registrarPaciente("Prueba", "Reloj", 99001, "reloj@x.test", null, false);
        Odontologo odontologo = so.registrarOdontologo(
                new General(0L, "Odontologo", "Reloj", 99001, TipoConsulta.CONTROL));
        ServicioTurno servicio = new ServicioTurno(repositorio, reloj,
                new HorarioAtencion(LocalTime.of(9, 0), LocalTime.of(17, 0)));

        v.lanza("horario configurable rechaza antes de apertura", DatoInvalidoException.class,
                () -> servicio.registrarTurno(paciente, odontologo, hoy.plusDays(1), LocalTime.of(8, 59)));
        v.lanza("horario configurable rechaza después del cierre", DatoInvalidoException.class,
                () -> servicio.registrarTurno(paciente, odontologo, hoy.plusDays(1), LocalTime.of(17, 1)));
        v.verdadero("horario configurable acepta apertura", servicio.registrarTurno(
                paciente, odontologo, hoy.plusDays(1), LocalTime.of(9, 0)) != null);
        v.verdadero("horario configurable acepta cierre", servicio.registrarTurno(
                paciente, odontologo, hoy.plusDays(1), LocalTime.of(17, 0)) != null);
        v.lanza("no permite una hora anterior a la hora actual de hoy", DatoInvalidoException.class,
                () -> servicio.registrarTurno(paciente, odontologo, hoy, LocalTime.of(9, 30)));
        v.lanza("no permite la hora actual de hoy", DatoInvalidoException.class,
                () -> servicio.registrarTurno(paciente, odontologo, hoy, LocalTime.of(10, 0)));
        v.verdadero("permite una hora posterior a la actual de hoy", servicio.registrarTurno(
                paciente, odontologo, hoy, LocalTime.of(10, 1)) != null);
        v.lanza("sigue rechazando fechas anteriores a hoy", DatoInvalidoException.class,
                () -> servicio.registrarTurno(paciente, odontologo, hoy.minusDays(1), LocalTime.of(11, 0)));
        v.lanza("horario inválido rechaza apertura posterior al cierre", IllegalArgumentException.class,
                () -> new HorarioAtencion(LocalTime.of(18, 0), LocalTime.of(8, 0)));
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
        v.igual("precio tipado conserva precisión decimal", new BigDecimal("450000.00"),
                t.precioDecimalDe(TipoCirugia.IMPLANTE));
    }

    // ------------------------------------------------------------------ medios de pago

    private static void mediosDePago() {
        MedioPago ef = new Efectivo();
        MedioPago mp = new MercadoPago();
        MedioPago tj = new Tarjeta();
        v.igual("codigo estable Efectivo", "EFECTIVO", ef.getCodigo());
        v.igual("codigo estable Mercado Pago", "MERCADO_PAGO", mp.getCodigo());
        v.igual("codigo estable Tarjeta", "TARJETA", tj.getCodigo());
        v.igual("nombre Efectivo", "Efectivo", ef.getNombre());
        v.igual("nombre Mercado Pago", "Mercado Pago", mp.getNombre());
        v.igual("nombre Tarjeta", "Tarjeta", tj.getNombre());
        v.casi("Efectivo calcula 10% de descuento", 10.0, ef.calcularDescuento(100));
        v.casi("Efectivo aplica 10% de descuento", 90.0, ef.aplicarDescuento(100));
        v.casi("Mercado Pago no descuenta", 0.0, mp.calcularDescuento(100));
        v.casi("Tarjeta no descuenta", 0.0, tj.calcularDescuento(100));

        Efectivo efectivoConfigurable = new Efectivo(0.15);
        v.casi("Efectivo acepta descuento configurable", 15.0, efectivoConfigurable.calcularDescuento(100));
        v.casi("Efectivo configurable aplica el descuento", 85.0, efectivoConfigurable.aplicarDescuento(100));
        v.lanza("Efectivo rechaza descuento negativo", IllegalArgumentException.class, () -> new Efectivo(-0.01));
        v.lanza("Efectivo rechaza descuento mayor al 100%", IllegalArgumentException.class, () -> new Efectivo(1.01));
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
                    double descMedio = medio.calcularDescuento(tras);
                    double total = redondear(base - descOs - descMedio);

                    DesgloseMonto d = calc.calcular(base, os, medio);
                    combinaciones++;
                    if (Math.abs(d.getDescuentoObraSocial() - descOs) < 0.005
                            && Math.abs(d.getDescuentoMedioPago() - descMedio) < 0.005
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
        v.casi("implante + obra social + efectivo: desc. medio de pago", 31500, implante.getDescuentoMedioPago());
        v.casi("implante + obra social + efectivo: total", 283500, implante.getMontoTotal());

        DesgloseMonto sinMedio = calc.calcular(25000, true);
        v.casi("vista previa sin medio: total con obra social", 17500, sinMedio.getMontoTotal());
        v.casi("vista previa sin medio: sin descuento de medio", 0, sinMedio.getDescuentoMedioPago());
        DesgloseMonto descuentoConfigurable = calc.calcular(100, false, new Efectivo(0.15));
        v.casi("calculador delega el descuento configurable al medio", 15, descuentoConfigurable.getDescuentoMedioPago());
        v.casi("calculador obtiene el total del medio configurable", 85, descuentoConfigurable.getMontoTotal());

        v.lanza("base en cero", DatoInvalidoException.class, () -> calc.calcular(0, false, new Efectivo()));
        v.lanza("base negativa", DatoInvalidoException.class, () -> calc.calcular(-5, false));
        v.lanza("medio de pago nulo", DatoInvalidoException.class, () -> calc.calcular(1000, false, null));
        DesgloseMonto decimal = calc.calcular(new BigDecimal("0.05"), true, new Efectivo());
        v.igual("desglose conserva dos decimales exactos", new BigDecimal("0.05"), decimal.getMontoBaseDecimal());
        v.igual("redondeo decimal HALF_UP", new BigDecimal("0.02"),
                CalculadorMonto.redondear(new BigDecimal("0.015")));
        v.igual("descuento calculado en decimal", new BigDecimal("0.01"),
                new Efectivo().calcularDescuento(new BigDecimal("0.05")));
    }

    // ------------------------------------------------------------------ facturacion

    private static void facturacion() throws Exception {
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente(), new RepositorioTurno());
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo(), new RepositorioTurno());
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
        v.igual("factura 1: medio (codigo)", "EFECTIVO", f1.getMetodoPagoCodigo());
        v.casi("factura 1: base", 25000, f1.getMontoBase());
        v.casi("factura 1: desc. obra social", 7500, f1.getDescuentoObraSocial());
        v.casi("factura 1: desc. medio de pago", 1750, f1.getDescuentoMedioPago());
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

    // ------------------------------------------------------------------ controladores (etapa 23)

    private static void controladores() throws Exception {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-01T10:00:00Z"), ZoneOffset.UTC);
        RepositorioTurno repoTurnos = new RepositorioTurno();
        ServicioPaciente sp = new ServicioPaciente(new RepositorioPaciente(), repoTurnos, reloj);
        ServicioOdontologo so = new ServicioOdontologo(new RepositorioOdontologo(), repoTurnos);
        ServicioTurno st = new ServicioTurno(repoTurnos, reloj);
        ControladorPaciente cp = new ControladorPaciente(sp);
        ControladorOdontologo co = new ControladorOdontologo(so);
        ControladorTurno ct = new ControladorTurno(st, sp, so, reloj);

        Paciente paciente = cp.registrarPaciente(new DatosPaciente(
                "  Ana  ", "  Pérez ", 88001, " ana@example.com  ", null, true));
        v.igual("DTO paciente normaliza nombre", "Ana", paciente.getNombre());
        v.igual("DTO paciente normaliza apellido", "Pérez", paciente.getApellido());
        v.igual("DTO paciente normaliza email", "ana@example.com", paciente.getEmail());
        Paciente actualizado = cp.actualizarPaciente(paciente.getId(),
                new DatosPacienteActualizacion(" Ana María ", " Pérez ", " ana2@example.com ", null, false));
        v.igual("DTO actualización normaliza nombre", "Ana María", actualizado.getNombre());
        v.igual("DTO actualización normaliza email", "ana2@example.com", actualizado.getEmail());
        v.lanza("DTO paciente nulo", DatoInvalidoException.class, () -> cp.registrarPaciente(null));
        DatosDomicilio datosDomicilio = new DatosDomicilio("  San Martín ", 123, "  Quilmes ",
                "  Buenos Aires ", TipoHogar.CASA);
        Paciente conDomicilio = cp.registrarPaciente(new DatosPaciente(
                "Marta", "Suárez", 88004, "marta@example.com", datosDomicilio, false));
        v.igual("DTO domicilio normaliza calle", "San Martín", conDomicilio.getDomicilio().getCalle());
        v.igual("DTO domicilio normaliza localidad", "Quilmes", conDomicilio.getDomicilio().getLocalidad());
        Paciente sinDomicilio = cp.actualizarPaciente(conDomicilio.getId(),
                new DatosPacienteActualizacion("Marta", "Suárez", "marta@example.com", null, false, true));
        v.igual("DTO permite quitar explícitamente el domicilio", null, sinDomicilio.getDomicilio());
        v.lanza("controlador rechaza domicilio incompleto", DatoInvalidoException.class,
                () -> cp.registrarPaciente(new DatosPaciente("Nora", "Díaz", 88005,
                        "nora@example.com", new DatosDomicilio("Calle", 0, "", "BA", TipoHogar.CASA), false)));

        Odontologo odontologo = co.registrarOdontologo(new DatosOdontologo(
                "  Luis ", "  Gómez ", 88002, Especialidad.GENERAL, TipoConsulta.LIMPIEZA));
        v.igual("controlador crea odontólogo por especialidad", Especialidad.GENERAL, odontologo.getEspecialidad());
        v.igual("controlador normaliza nombre odontólogo", "Luis", odontologo.getNombre());
        v.igual("controlador aplica subespecialidad solicitada", TipoConsulta.LIMPIEZA, odontologo.getSubespecialidad());
        Odontologo actualizadoOdontologo = co.actualizarOdontologo(odontologo.getId(),
                new DatosOdontologo("Luis Alberto", "Gómez", 88006, Especialidad.CIRUJANO, TipoCirugia.IMPLANTE));
        v.igual("actualización DTO cambia nombre", "Luis Alberto", actualizadoOdontologo.getNombre());
        v.igual("actualización DTO cambia matrícula", 88006, actualizadoOdontologo.getMatricula());
        v.igual("actualización DTO cambia especialidad", Especialidad.CIRUJANO, actualizadoOdontologo.getEspecialidad());
        v.igual("actualización DTO cambia subespecialidad", TipoCirugia.IMPLANTE, actualizadoOdontologo.getSubespecialidad());
        v.lanza("controlador exige especialidad", DatoInvalidoException.class,
                () -> co.registrarOdontologo(new DatosOdontologo("A", "B", 88003, null, null)));
        v.lanza("controlador rechaza subespecialidad incompatible", DatoInvalidoException.class,
                () -> co.registrarOdontologo(new DatosOdontologo("A", "B", 88003,
                        Especialidad.GENERAL, TipoCirugia.IMPLANTE)));

        LocalDate hoy = LocalDate.now(reloj);
        LocalDate manana = hoy.plusDays(1);
        Turno hoyTurno = ct.registrarTurno(new SolicitudTurno(
                paciente.getId(), odontologo.getId(), hoy, LocalTime.of(11, 0), TipoConsulta.CONTROL));
        Turno mananaUno = ct.registrarTurno(new SolicitudTurno(
                paciente.getId(), odontologo.getId(), manana, LocalTime.of(9, 0), TipoConsulta.LIMPIEZA));
        Turno mananaDos = ct.registrarTurno(new SolicitudTurno(
                paciente.getId(), odontologo.getId(), manana, LocalTime.of(10, 0), TipoConsulta.LIMPIEZA));
        v.igual("solicitud DTO registra turno", EstadoTurno.PENDIENTE, hoyTurno.getEstado());
        v.igual("listar turnos de hoy", 1, ct.listarTurnosDeHoy().size());
        v.igual("semana calendario lunes-domingo", 2, ct.listarTurnosDeLaSemana(manana).size());
        ResumenSemanalTurnos resumen = ct.obtenerResumenSemanal(manana);
        v.igual("resumen semanal total", 2, resumen.total());
        v.igual("resumen semanal pendientes", 2, resumen.pendientes());
        v.igual("resumen semanal desde lunes", DayOfWeek.MONDAY,
                resumen.desde().getDayOfWeek());
        v.igual("resumen semanal hasta domingo", DayOfWeek.SUNDAY,
                resumen.hasta().getDayOfWeek());
        v.igual("filtro combinado por paciente y odontólogo", 3,
                ct.filtrar(new FiltroTurnos(paciente.getId(), odontologo.getId(), null, null, null)).size());
        v.igual("filtro combinado por fecha y estado", 2,
                ct.filtrar(new FiltroTurnos(null, null, manana, manana, EstadoTurno.PENDIENTE)).size());
        ct.confirmarTurno(mananaUno.getId());
        v.igual("filtro combinado detecta estado confirmado", 1,
                ct.filtrar(new FiltroTurnos(paciente.getId(), odontologo.getId(), manana, manana,
                        EstadoTurno.CONFIRMADO)).size());
        v.igual("filtro desde una fecha incluye fechas posteriores", 3,
                ct.filtrar(new FiltroTurnos(null, null, hoy, null, null)).size());
        v.igual("agenda ordenada por fecha y hora", mananaUno.getId(),
                ct.listarTodos().get(1).getId());
        v.lanza("filtro rechaza rango invertido", IllegalArgumentException.class,
                () -> new FiltroTurnos(null, null, manana, hoy, null));
        v.lanza("resumen semanal requiere fecha", DatoInvalidoException.class,
                () -> ct.obtenerResumenSemanal(null));
        ct.marcarAusente(mananaDos.getId());
        v.igual("controlador marca un turno como AUSENTE", EstadoTurno.AUSENTE,
                ct.buscarPorId(mananaDos.getId()).getEstado());
        ResumenSemanalTurnos resumenConAusente = ct.obtenerResumenSemanal(manana);
        v.igual("resumen semanal cuenta ausentes", 1, resumenConAusente.ausentes());
        v.igual("resumen semanal conserva el total al marcar ausencia", 2, resumenConAusente.total());
        v.lanza("turno inexistente usa excepción específica", TurnoNoEncontradoException.class,
                () -> ct.buscarPorId(99999));
        try {
            ct.buscarPorId(99999);
        } catch (TurnoNoEncontradoException ex) {
            v.igual("excepción específica de turno tiene código propio", 105, ex.getCodigoError());
        }
        v.lanza("solicitud turno nula", DatoInvalidoException.class, () -> ct.registrarTurno(null));
    }

    // ------------------------------------------------------------------ repositorios

    private static void repositorios() throws Exception {
        RepositorioPaciente rp = new RepositorioPaciente();
        v.verdadero("buscarPorId inexistente devuelve Optional.empty", rp.buscarPorId(1L).isEmpty());
        v.verdadero("buscarPorDni inexistente devuelve Optional.empty", rp.buscarPorDni(5).isEmpty());
        Paciente p = new Paciente(1L, "A", "B", 5, "a@b.com", LocalDate.now(), null, false);
        rp.guardar(p);
        v.igual("buscarPorId devuelve Optional con paciente", p, rp.buscarPorId(1L).orElse(null));
        v.igual("buscarPorDni usa el indice", p, rp.buscarPorDni(5).orElse(null));
        v.igual("listarTodos devuelve una copia", 1, rp.listarTodos().size());

        Paciente actualizado = new Paciente(1L, "A", "B", 6, "a@b.com", LocalDate.now(), null, false);
        rp.actualizar(actualizado);
        v.verdadero("actualizar libera el DNI anterior del indice", rp.buscarPorDni(5).isEmpty());
        v.igual("actualizar registra el DNI nuevo en el indice", actualizado, rp.buscarPorDni(6).orElse(null));
        rp.actualizar(new Paciente(77L, "Z", "Z", 9, "z@z.com", LocalDate.now(), null, false));
        v.igual("actualizar un id inexistente no agrega nada", 1, rp.listarTodos().size());
        rp.eliminar(1L);
        v.igual("eliminar quita el registro", 0, rp.listarTodos().size());
        v.verdadero("eliminar libera el DNI del indice", rp.buscarPorDni(6).isEmpty());

        RepositorioOdontologo ro = new RepositorioOdontologo();
        Odontologo odontologo = new General(4L, "O", "D", 44, TipoConsulta.CONTROL);
        ro.guardar(odontologo);
        v.igual("buscarPorMatricula usa el indice", odontologo, ro.buscarPorMatricula(44).orElse(null));
        v.verdadero("matricula inexistente devuelve Optional.empty", ro.buscarPorMatricula(99).isEmpty());
        Odontologo odontologoActualizado = new General(4L, "O", "D", 45, TipoConsulta.CONTROL);
        ro.actualizar(odontologoActualizado);
        v.verdadero("actualizar libera la matricula anterior", ro.buscarPorMatricula(44).isEmpty());
        v.igual("actualizar registra la matricula nueva", odontologoActualizado,
                ro.buscarPorMatricula(45).orElse(null));
        ro.eliminar(4L);
        v.verdadero("eliminar libera la matricula", ro.buscarPorMatricula(45).isEmpty());

        RepositorioTurno rt = new RepositorioTurno();
        Paciente pac = new Paciente(1L, "A", "B", 5, "a@b.com", LocalDate.now(), null, false);
        Odontologo od = new General(4L, "O", "D", 44, TipoConsulta.CONTROL);
        LocalDate dia = LocalDate.now().plusDays(1);
        Turno turno = new Turno(7L, pac, od, dia, LocalTime.of(9, 0), EstadoTurno.PENDIENTE,
                TipoConsulta.CONTROL);
        rt.guardar(turno);
        v.igual("buscarPorPaciente", 1, rt.buscarPorPaciente(1L).size());
        v.igual("buscarPorOdontologo", 1, rt.buscarPorOdontologo(4L).size());
        v.verdadero("turno encontrado por Optional", rt.buscarPorId(7L).isPresent());

        RepositorioFactura rf = new RepositorioFactura();
        LocalDate hoy = LocalDate.now();
        Factura f1 = factura(1L, 10L, 1L, "A B", "LIMPIEZA", hoy, "EFECTIVO", 100, 0, 10, 90);
        Factura f2 = factura(2L, 11L, 1L, "A B", "CONTROL", hoy.minusDays(1), "TARJETA", 100, 0, 0, 100);
        rf.guardar(f1);
        rf.guardar(f2);
        v.verdadero("existePorTurno usa el indice", rf.existePorTurno(10L));
        v.verdadero("no existePorTurno", !rf.existePorTurno(99L));
        v.igual("listarPorFecha hoy", 1, rf.listarPorFecha(hoy).size());
        v.igual("listarPorFecha ayer", 1, rf.listarPorFecha(hoy.minusDays(1)).size());
        v.igual("listarPorFecha sin facturas", 0, rf.listarPorFecha(hoy.plusDays(3)).size());
        v.verdadero("repositorio de facturas implementa el contrato base",
                IRepositorio.class.isAssignableFrom(RepositorioFactura.class));
        v.verdadero("el contrato de busqueda devuelve Optional",
                IRepositorio.class.getMethod("buscarPorId", long.class).getReturnType().equals(Optional.class));
        v.verdadero("RepositorioFactura no expone actualizar", !tieneMetodoPublico(RepositorioFactura.class, "actualizar"));
        v.verdadero("RepositorioFactura no expone eliminar", !tieneMetodoPublico(RepositorioFactura.class, "eliminar"));

        GeneradorIds generador = new GeneradorIds();
        v.igual("generador inicia en 1", 1L, generador.siguienteId(() -> 0L));
        v.igual("generador toma el maximo actual mas uno", 2L, generador.siguienteId(() -> 1L));
        v.igual("generador contempla ids cargados despues de su creacion", 9L,
                generador.siguienteId(() -> 8L));
        v.igual("generador refleja eliminaciones segun la politica actual", 4L,
                generador.siguienteId(() -> 3L));
        v.verdadero("ServicioPaciente no expone el repositorio", !tieneMetodoPublico(ServicioPaciente.class, "getRepositorio")
                && !tieneMetodoPublico(ServicioPaciente.class, "guardarDesdePersistencia"));
        v.verdadero("ServicioOdontologo no expone el repositorio", !tieneMetodoPublico(ServicioOdontologo.class, "getRepositorio")
                && !tieneMetodoPublico(ServicioOdontologo.class, "guardarDesdePersistencia"));
        v.verdadero("ServicioTurno no expone el repositorio", !tieneMetodoPublico(ServicioTurno.class, "getRepositorio")
                && !tieneMetodoPublico(ServicioTurno.class, "guardarDesdePersistencia"));
        v.verdadero("ServicioFactura no expone el repositorio", !tieneMetodoPublico(ServicioFactura.class, "getRepositorio"));
    }

    private static boolean tieneMetodoPublico(Class<?> tipo, String nombre) {
        for (java.lang.reflect.Method metodo : tipo.getMethods()) {
            if (metodo.getName().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ persistencia (etapa 24)

    private static Path directorioTemporal(String nombre) throws IOException {
        Path dir = Files.createTempDirectory("clinica-" + nombre + "-");
        dir.toFile().deleteOnExit();
        return dir;
    }

    private static void persistenciaPacientes() throws Exception {
        Path archivo = directorioTemporal("pacientes").resolve("pacientes.txt");
        RepositorioCsv<Paciente> csv = new RepositorioCsv<>(archivo, new PacienteCsvMapper());
        Domicilio dom = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires", TipoHogar.CASA);
        Domicilio domComa = new Domicilio("Gaboto", 849, "La Tablada, La Matanza", "Buenos Aires", TipoHogar.CASA);
        List<Paciente> origen = List.of(
                new Paciente(1L, "Fatima", "Persi", 100, "f@x.com", LocalDate.of(2024, 1, 1), dom, true),
                new Paciente(2L, "Tomas", "Barea", 200, "t@x.com", LocalDate.of(2024, 2, 2), null, false),
                new Paciente(3L, "Maria\nGarcia", "Garcia \"Sol\"", 300, "m@x.com", LocalDate.of(2024, 3, 3), domComa, true));
        csv.guardar(origen);
        ResultadoCarga<Paciente> carga = csv.cargar();
        v.igual("CSV de pacientes: se cargan los 3 registros", 3, carga.registros().size());
        v.igual("CSV de pacientes: sin errores", 0, carga.errores().size());
        v.igual("CSV de pacientes: encabezado por nombre", "id,nombre,apellido,dni,email,fechaAlta,tieneObraSocial,calle,numero,localidad,provincia,hogar",
                Files.readAllLines(archivo).get(0));
        v.igual("CSV de pacientes: localidad con coma conserva su valor", "La Tablada, La Matanza",
                carga.registros().get(2).getDomicilio().getLocalidad());
        v.igual("CSV de pacientes: salto de línea se escapa y recupera", "Maria\nGarcia", carga.registros().get(2).getNombre());
        v.igual("CSV de pacientes: comillas dobles se escapan y recuperan", "Garcia \"Sol\"",
                carga.registros().get(2).getApellido());
        v.igual("CSV de pacientes: obra social", true, carga.registros().get(0).tieneObraSocial());
        v.igual("CSV de pacientes: paciente sin domicilio", false, carga.registros().get(1).tieneDomicilio());

        Files.write(archivo, List.of(
                "1,Ana,Lopez,111,a@x.com,2024-01-01",
                "2,Luis,Perez,222,l@x.com,2024-02-02,Belgrano,430,San Justo,Buenos Aires,CASA",
                "basura,corrupta"));
        ResultadoCarga<Paciente> legado = csv.cargar();
        v.igual("CSV de pacientes: compatibilidad con 6 y 11 columnas históricas", 2, legado.registros().size());
        v.igual("CSV de pacientes: formato histórico asigna obra social false", false, legado.registros().get(0).tieneObraSocial());
        v.igual("CSV de pacientes: domicilio histórico", "Belgrano", legado.registros().get(1).getDomicilio().getCalle());
        v.igual("CSV de pacientes: error de una fila no corta la carga", 1, legado.errores().size());

        Files.write(archivo, List.of(
                "4,Nuevo,Formato,444,n@x.com,2025-01-01,true",
                "5,Con,Domicilio,555,d@x.com,2025-02-02,true,Calle,12,Localidad,Buenos Aires,PH"));
        ResultadoCarga<Paciente> formatoEtapa20 = csv.cargar();
        v.igual("CSV de pacientes: compatibilidad con formato intermedio de 7/12 columnas", 2,
                formatoEtapa20.registros().size());
        v.igual("CSV de pacientes: formato intermedio conserva obra social", true,
                formatoEtapa20.registros().get(1).tieneObraSocial());

        Files.write(archivo, List.of(
                "apellido,nombre,id,dni,email,fechaAlta,tieneObraSocial,calle,numero,localidad,provincia,hogar",
                "Lopez,Ana,9,999,a@x.com,2025-03-03,false,,,,,"));
        ResultadoCarga<Paciente> encabezadoReordenado = csv.cargar();
        v.igual("CSV de pacientes: resuelve columnas por nombre y no por posición", "Ana",
                encabezadoReordenado.registros().get(0).getNombre());
        v.igual("CSV de pacientes: encabezado reordenado conserva el DNI", 999,
                encabezadoReordenado.registros().get(0).getDni());
    }

    private static void persistenciaOdontologos() throws Exception {
        Path archivo = directorioTemporal("odontologos").resolve("odontologos.txt");
        RepositorioCsv<Odontologo> csv = new RepositorioCsv<>(archivo, new OdontologoCsvMapper());
        List<Odontologo> origen = List.of(
                new General(1L, "Ana", "Paz", 10, TipoConsulta.LIMPIEZA),
                new Cirujano(2L, "Luis", "Gil", 20, TipoCirugia.IMPLANTE),
                new Ortodoncista(3L, "Eva", "Sol", 30, TipoOrtodoncia.BRACKETS_CERAMICOS));
        csv.guardar(origen);
        ResultadoCarga<Odontologo> carga = csv.cargar();
        v.igual("CSV de odontólogos: round-trip de subtipos", 3, carga.registros().size());
        v.verdadero("CSV de odontólogos: reconstruye los tres subtipos", carga.registros().get(0) instanceof General
                && carga.registros().get(1) instanceof Cirujano && carga.registros().get(2) instanceof Ortodoncista);
        Files.write(archivo, List.of("1,General,Ana,Paz,10,LIMPIEZA", "2,Desconocida,Luis,Gil,20,IMPLANTE",
                "3,Cirujano,Sol,Eva,30,IMPLANTE"));
        ResultadoCarga<Odontologo> legado = csv.cargar();
        v.igual("CSV de odontólogos: procesa registros válidos después de uno corrupto", 2, legado.registros().size());
        v.igual("CSV de odontólogos: informa fila corrupta sin detenerse", 1, legado.errores().size());
    }

    private static void persistenciaTurnos() throws Exception {
        Path archivo = directorioTemporal("turnos").resolve("turnos.txt");
        RepositorioPaciente rp = new RepositorioPaciente();
        RepositorioOdontologo ro = new RepositorioOdontologo();
        Paciente p = new Paciente(1L, "Ana", "Lopez", 111, "a@x.com", LocalDate.now(), null, false);
        Odontologo o = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        rp.guardar(p); ro.guardar(o);
        LocalDate fecha = LocalDate.now().plusDays(2);
        List<Turno> origen = List.of(
                new Turno(1L, p, o, fecha, LocalTime.of(9, 0), EstadoTurno.PENDIENTE, TipoConsulta.LIMPIEZA),
                new Turno(2L, p, o, fecha, LocalTime.of(10, 0), EstadoTurno.AUSENTE, TipoConsulta.URGENCIA),
                new Turno(3L, p, o, fecha, LocalTime.of(11, 0), EstadoTurno.CANCELADO, null));
        RepositorioCsv<Turno> csv = new RepositorioCsv<>(archivo, new TurnoCsvMapper(
                id -> rp.buscarPorId(id).orElse(null), id -> ro.buscarPorId(id).orElse(null)));
        csv.guardar(origen);
        ResultadoCarga<Turno> carga = csv.cargar();
        v.igual("CSV de turnos: round-trip de estados", 3, carga.registros().size());
        v.igual("CSV de turnos: motivo tipado", TipoConsulta.URGENCIA, carga.registros().get(1).getMotivoConsulta().orElse(null));
        v.verdadero("CSV de turnos: turno sin motivo conserva ausencia", carga.registros().get(2).getMotivoConsulta().isEmpty());
        Files.write(archivo, List.of("1,99,1," + fecha + ",09:00,PENDIENTE,LIMPIEZA",
                "2,1,1," + fecha + ",10:00,PENDIENTE,XYZ"));
        ResultadoCarga<Turno> parcial = csv.cargar();
        v.igual("CSV de turnos: una referencia inválida se aísla", 1, parcial.registros().size());
        v.igual("CSV de turnos: informa la referencia inválida", 1, parcial.errores().size());
        v.verdadero("CSV de turnos: motivo desconocido mantiene el comportamiento histórico sin motivo",
                parcial.registros().get(0).getMotivoConsulta().isEmpty());
    }

    private static void persistenciaFacturas() throws Exception {
        Path archivo = directorioTemporal("facturas").resolve("facturas.txt");
        FacturaCsvMapper mapper = new FacturaCsvMapper(codigo -> codigo.equals("Tarjeta") ? "TARJETA" : codigo);
        RepositorioCsv<Factura> csv = new RepositorioCsv<>(archivo, mapper);
        LocalDate hoy = LocalDate.now();
        List<Factura> facturas = List.of(
                factura(1L, 10L, 1L, "Fatima Persi", "LIMPIEZA", hoy, "EFECTIVO", 25000, 7500, 1750, 15750),
                factura(2L, 11L, 2L, "Barea, Tomas", "IMPLANTE", hoy.minusDays(1), "Tarjeta", 450000, 0, 0, 450000));
        csv.guardar(facturas);
        ResultadoCarga<Factura> carga = csv.cargar();
        v.igual("CSV de facturas: carga ambos registros", 2, carga.registros().size());
        v.igual("CSV de facturas: nombre con coma conserva su valor", "Barea, Tomas", carga.registros().get(1).getPacienteNombre());
        v.igual("CSV de facturas: medio antiguo se migra a código", "TARJETA", carga.registros().get(1).getMetodoPagoCodigo());
        v.casi("CSV de facturas: total decimal", 15750, carga.registros().get(0).getMontoTotal());
        Files.write(archivo, List.of("id,turnoId,pacienteId,pacienteNombre,concepto,fecha,medioPago,montoBase,descuentoObraSocial,descuentoMedioPago,montoTotal",
                "3,12,4,Paciente,CONTROL," + hoy + ",EFECTIVO,100.00,0.00,0.00,100.00", "fila,corrupta"));
        ResultadoCarga<Factura> parcial = csv.cargar();
        v.igual("CSV de facturas: carga válida pese a una línea corrupta", 1, parcial.registros().size());
        v.igual("CSV de facturas: registra el error de la línea corrupta", 1, parcial.errores().size());
        Files.write(archivo, List.of("4,13,5,Paciente,CONTROL," + hoy + ",Tarjeta,100.00,0.00,0.00,100.00"));
        ResultadoCarga<Factura> formatoAnterior = csv.cargar();
        v.igual("CSV de facturas: compatibilidad con archivo anterior sin encabezado", 1, formatoAnterior.registros().size());
        v.igual("CSV de facturas: medio de pago antiguo se convierte en código", "TARJETA",
                formatoAnterior.registros().get(0).getMetodoPagoCodigo());
    }

    private static void persistenciaIntegrada() throws Exception {
        Path directorio = directorioTemporal("integrada");
        RepositorioPaciente rp = new RepositorioPaciente();
        RepositorioOdontologo ro = new RepositorioOdontologo();
        RepositorioTurno rt = new RepositorioTurno();
        RepositorioFactura rf = new RepositorioFactura();
        Paciente paciente = new Paciente(1L, "Ana", "Lopez", 111, "a@x.com", LocalDate.now(), null, true);
        Odontologo odontologo = new General(1L, "Magali", "Pesqueira", 1131, TipoConsulta.LIMPIEZA);
        Turno turno = new Turno(1L, paciente, odontologo, LocalDate.now().plusDays(1), LocalTime.of(9, 0),
                EstadoTurno.COMPLETADO, TipoConsulta.LIMPIEZA);
        Factura factura = factura(1L, 1L, 1L, "Ana Lopez", "LIMPIEZA", LocalDate.now(), "EFECTIVO", 100, 0, 0, 100);
        rp.guardar(paciente); ro.guardar(odontologo); rt.guardar(turno); rf.guardar(factura);
        PersistenciaClinica escritura = new PersistenciaClinica(directorio, rp, ro, rt, rf, codigo -> codigo);
        escritura.guardar(rp.listarTodos(), rt.listarTodos(), ro.listarTodos(), rf.listarTodos());

        RepositorioPaciente rp2 = new RepositorioPaciente();
        RepositorioOdontologo ro2 = new RepositorioOdontologo();
        RepositorioTurno rt2 = new RepositorioTurno();
        RepositorioFactura rf2 = new RepositorioFactura();
        PersistenciaClinica lectura = new PersistenciaClinica(directorio, rp2, ro2, rt2, rf2, codigo -> codigo);
        lectura.cargar();
        v.verdadero("PersistenciaClinica usa la carpeta configurada", Files.exists(directorio.resolve("pacientes.txt"))
                && Files.exists(directorio.resolve("odontologos.txt")) && Files.exists(directorio.resolve("turnos.txt"))
                && Files.exists(directorio.resolve("facturas.txt")));
        v.igual("PersistenciaClinica carga pacientes", 1, rp2.listarTodos().size());
        v.igual("PersistenciaClinica carga odontólogos", 1, ro2.listarTodos().size());
        v.igual("PersistenciaClinica carga turnos después de las personas", 1, rt2.listarTodos().size());
        v.igual("PersistenciaClinica carga facturas al final", 1, rf2.listarTodos().size());
    }


    // ------------------------------------------------------------------ Swing: piezas compartidas (etapa 26)

    private static void uiSwingCompartida() {
        Sidebar sidebar = new Sidebar();
        v.igual("sidebar genera una opción por cada sección enum", SeccionContenido.values().length,
                sidebar.getOpciones().size());
        v.igual("sidebar respeta el orden declarado por el enum", SeccionContenido.AGENDA,
                sidebar.getOpciones().get(0));
        java.util.concurrent.atomic.AtomicReference<SeccionContenido> seleccion =
                new java.util.concurrent.atomic.AtomicReference<>();
        sidebar.addPropertyChangeListener("seccionSeleccionada",
                evento -> seleccion.set((SeccionContenido) evento.getNewValue()));
        for (java.awt.event.MouseListener listener : sidebar.getBoton(SeccionContenido.TURNOS).getMouseListeners()) {
            listener.mouseClicked(new java.awt.event.MouseEvent(sidebar.getBoton(SeccionContenido.TURNOS),
                    java.awt.event.MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 1, 1, 1, false));
        }
        v.igual("sidebar publica el cambio de sección mediante Observer", SeccionContenido.TURNOS,
                seleccion.get());
        v.igual("sidebar marca la sección seleccionada", SeccionContenido.TURNOS,
                sidebar.getSeleccionada());

        v.igual("paleta pendiente del renderer", PaletaEstadoTurno.fondo(EstadoTurno.PENDIENTE),
                new java.awt.Color(254, 243, 199));
        v.igual("paleta completado de la agenda", PaletaEstadoTurno.borde(EstadoTurno.COMPLETADO),
                new java.awt.Color(5, 150, 105));
        v.igual("paleta AUSENTE definida", new java.awt.Color(237, 233, 254),
                PaletaEstadoTurno.fondo(EstadoTurno.AUSENTE));
        v.verdadero("AUSENTE tiene borde distinto de CANCELADO",
                !PaletaEstadoTurno.borde(EstadoTurno.AUSENTE).equals(PaletaEstadoTurno.borde(EstadoTurno.CANCELADO)));
        v.igual("paleta nula usa estilo neutro", java.awt.Color.WHITE, PaletaEstadoTurno.fondo(null));

        v.igual("formato pesos enteros", "$ 15.000", FormatoPlata.formatear(15000));
        v.igual("formato BigDecimal conserva decimales locales", "$ 15.000,5",
                FormatoPlata.formatear(new BigDecimal("15000.50")));
        v.igual("descuento positivo lleva signo menos", "-$ 1.500", FormatoPlata.formatearDescuento(1500));
        v.igual("descuento cero no lleva signo", "$ 0", FormatoPlata.formatearDescuento(0));


        Factura factura = factura(77, 8, 3, "Ana Pérez", "LIMPIEZA", LocalDate.of(2026, 11, 1),
                "EFECTIVO", 15000, 0, 1500, 13500);
        String comprobante = ComprobanteFactura.armarTexto(factura, List.of(new Efectivo()));
        v.verdadero("armado de comprobante incluye paciente", comprobante.contains("Paciente: Ana Pérez"));
        v.verdadero("armado de comprobante incluye medio legible", comprobante.contains("Medio de pago: Efectivo"));
        v.verdadero("armado de comprobante incluye total", comprobante.contains(FormatoPlata.formatear(13500)));
    }

    // ------------------------------------------------------------------ Swing: formularios de personas (etapa 28)

    private static void uiSwingPersonas() throws Exception {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-01T10:00:00Z"), ZoneOffset.UTC);
        RepositorioTurno repositorioTurnos = new RepositorioTurno();
        ControladorPaciente controladorPaciente = new ControladorPaciente(
                new ServicioPaciente(new RepositorioPaciente(), repositorioTurnos, reloj));
        ControladorOdontologo controladorOdontologo = new ControladorOdontologo(
                new ServicioOdontologo(new RepositorioOdontologo(), repositorioTurnos));
        Paciente paciente = controladorPaciente.registrarPaciente(new DatosPaciente(
                "Ana", "Pérez", 99101, "ana.swing@example.com",
                new DatosDomicilio("Belgrano", 456, "Quilmes", "Buenos Aires", TipoHogar.DEPARTAMENTO), true));
        Odontologo odontologo = controladorOdontologo.registrarOdontologo(new DatosOdontologo(
                "Luis", "Gómez", 99102, Especialidad.CIRUJANO, TipoCirugia.IMPLANTE));

        uiSwing.PanelPaciente panelPaciente = new uiSwing.PanelPaciente(controladorPaciente);
        javax.swing.JTable tablaPacientes = primerComponente(panelPaciente, javax.swing.JTable.class);
        v.igual("PanelPaciente muestra los pacientes del controlador", 1,
                tablaPacientes.getModel().getRowCount());
        tablaPacientes.setRowSelectionInterval(0, 0);
        List<javax.swing.JTextField> camposPaciente = componentes(panelPaciente, javax.swing.JTextField.class);
        v.verdadero("ListSelectionListener carga el email del paciente",
                camposPaciente.stream().anyMatch(c -> c.getText().equals("ana.swing@example.com")));
        v.verdadero("PanelPaciente cambia explícitamente al modo edición",
                componentes(panelPaciente, javax.swing.JButton.class).stream()
                        .anyMatch(b -> b.getText().equals("Actualizar paciente")));
        v.verdadero("DNI queda protegido durante la edición",
                camposPaciente.stream().filter(c -> c.getText().equals("99101")).allMatch(c -> !c.isEditable()));

        uiSwing.PanelOdontologo panelOdontologo = new uiSwing.PanelOdontologo(controladorOdontologo);
        javax.swing.JTable tablaOdontologos = primerComponente(panelOdontologo, javax.swing.JTable.class);
        v.igual("PanelOdontologo muestra los odontólogos del controlador", 1,
                tablaOdontologos.getModel().getRowCount());
        tablaOdontologos.setRowSelectionInterval(0, 0);
        v.verdadero("ListSelectionListener carga la matrícula del odontólogo",
                componentes(panelOdontologo, javax.swing.JTextField.class).stream()
                        .anyMatch(c -> c.getText().equals("99102")));
        v.verdadero("PanelOdontologo permite editar todos los datos",
                componentes(panelOdontologo, javax.swing.JButton.class).stream()
                        .anyMatch(b -> b.getText().equals("Actualizar odontólogo")));
        v.igual("tabla de odontólogos incluye subespecialidad", 6,
                tablaOdontologos.getColumnModel().getColumnCount());
    }

    private static <T extends java.awt.Component> T primerComponente(java.awt.Container contenedor, Class<T> tipo) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (tipo.isInstance(componente)) return tipo.cast(componente);
            if (componente instanceof java.awt.Container hijo) {
                T encontrado = primerComponente(hijo, tipo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static <T extends java.awt.Component> List<T> componentes(java.awt.Container contenedor, Class<T> tipo) {
        List<T> encontrados = new ArrayList<>();
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (tipo.isInstance(componente)) encontrados.add(tipo.cast(componente));
            if (componente instanceof java.awt.Container hijo) encontrados.addAll(componentes(hijo, tipo));
        }
        return encontrados;
    }

    private static void uiSwingTurnos() {
        TurnoTableModel modelo = new TurnoTableModel();
        v.igual("TurnoTableModel expone seis columnas visibles", 6, modelo.getColumnCount());
        v.igual("TurnoTableModel no expone columna ID", "Paciente", modelo.getColumnName(0));
        v.igual("TurnoTableModel nombra la columna de estado", "Estado", modelo.getColumnName(5));
        v.igual("TurnoTableModel inicia sin filas", 0, modelo.getRowCount());
        v.verdadero("TurnoTableModel es de solo lectura", !modelo.isCellEditable(0, 0));
        Paciente paciente = new Paciente(81L, "Ana", "Pérez", 12345678, "ana@x.com", LocalDate.of(2026, 1, 1), null, false);
        Odontologo odontologo = new General(82L, "Luis", "Gómez", 8765, TipoConsulta.CONTROL);
        Turno turno = new Turno(83L, paciente, odontologo, LocalDate.of(2026, 2, 3),
                LocalTime.of(10, 30), EstadoTurno.PENDIENTE, TipoConsulta.CONTROL);
        modelo.setTurnos(List.of(turno));
        v.igual("TurnoTableModel conserva una fila", 1, modelo.getRowCount());
        v.igual("TurnoTableModel permite recuperar el turno real sin columna ID", turno, modelo.getTurnoAt(0));
        v.igual("TurnoTableModel muestra el paciente", "Ana Pérez", modelo.getValueAt(0, 0));
        v.igual("TurnoTableModel muestra el estado", "PENDIENTE", modelo.getValueAt(0, 5));

        OpcionMotivo inicial = OpcionMotivo.inicial();
        v.verdadero("OpcionMotivo inicial no representa un motivo seleccionable", !inicial.esSeleccionable());
        OpcionMotivo opcion = OpcionMotivo.de(TipoConsulta.values()[0]);
        v.verdadero("OpcionMotivo conserva el motivo polimórfico", opcion.esSeleccionable()
                && opcion.motivo() == TipoConsulta.values()[0]);
        v.igual("OpcionMotivo muestra el nombre legible", TipoConsulta.values()[0].getNombreLegible(), opcion.toString());
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

}
