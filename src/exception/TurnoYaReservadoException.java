package exception;

import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoYaReservadoException extends ClinicaException {

    //identificador unico
    private static final int CODIGO = 103;

    // nombre del odontólogo, fecha y hora
    public TurnoYaReservadoException(String nombreOdontologo, LocalDate fecha, LocalTime hora) {
        super("El odontólogo " + nombreOdontologo +
                " ya tiene un turno reservado el " + fecha +
                " a las " + hora + ". Elija otro horario.", CODIGO);
    }


    // Mensaje
    public TurnoYaReservadoException(String mensaje) {
        super(mensaje, CODIGO);
    }
}
