package exception;

import java.time.LocalDate;
import java.time.LocalTime;

/** Se produce cuando el odontólogo ya tiene ocupado el horario solicitado. */
public class TurnoYaReservadoException extends ClinicaException {

    private static final long serialVersionUID = 1L;
    private static final int CODIGO = 103;

    public TurnoYaReservadoException(String nombreOdontologo, LocalDate fecha, LocalTime hora) {
        super("El odontólogo " + nombreOdontologo + " ya tiene un turno reservado el " + fecha
                + " a las " + hora + ". Elija otro horario.", CODIGO);
    }

    public TurnoYaReservadoException(String mensaje) {
        super(mensaje, CODIGO);
    }
}
