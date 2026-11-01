package main;

import controller.ControladorFactura;
import controller.ControladorOdontologo;
import controller.ControladorPaciente;
import controller.ControladorTurno;
import persistence.PersistenciaClinica;
import ui.VistaPrincipal;
import uiSwing.VentanaPrincipal;

import java.io.IOException;
import java.util.Objects;

/** Dependencias compartidas por las interfaces de consola y Swing. */
public record ContextoAplicacion(
        ControladorPaciente controladorPaciente,
        ControladorOdontologo controladorOdontologo,
        ControladorTurno controladorTurno,
        ControladorFactura controladorFactura,
        PersistenciaClinica persistencia,
        VistaPrincipal vistaPrincipal) {

    public ContextoAplicacion {
        Objects.requireNonNull(controladorPaciente);
        Objects.requireNonNull(controladorOdontologo);
        Objects.requireNonNull(controladorTurno);
        Objects.requireNonNull(controladorFactura);
        Objects.requireNonNull(persistencia);
        Objects.requireNonNull(vistaPrincipal);
    }

    public void iniciarConsola() {
        try {
            vistaPrincipal.iniciar();
        } finally {
            guardarDatos();
        }
    }

    public void iniciarSwing() {
        VentanaPrincipal ventana = new VentanaPrincipal(controladorTurno, controladorPaciente,
                controladorOdontologo, controladorFactura, persistencia);
        ventana.iniciar();
    }

    private void guardarDatos() {
        try {
            persistencia.guardar(controladorPaciente.listarTodos(), controladorTurno.listarTodos(),
                    controladorOdontologo.listarTodos(), controladorFactura.listarTodos());
        } catch (IOException e) {
            System.err.println("No se pudieron guardar los datos: " + e.getMessage());
        }
    }
}
