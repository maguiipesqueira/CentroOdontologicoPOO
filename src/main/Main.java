package main;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.util.Arrays;

/** Punto de entrada: selecciona el modo de interfaz y delega el armado de dependencias. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Path directorioDatos = Path.of(System.getProperty("clinica.data.dir", "."));
        ContextoAplicacion contexto = ConfiguradorAplicacion.crear(directorioDatos);

        boolean iniciarConsola = Arrays.asList(args).contains("--consola");
        if (iniciarConsola) {
            contexto.iniciarConsola();
            return;
        }

        SwingUtilities.invokeLater(contexto::iniciarSwing);
    }
}
