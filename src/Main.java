import Modelo.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        Recepcionista recepcionista = new Recepcionista();

        //crea ebjeto asociando dom con paciente
        Domicilio dom1 = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires");
        Paciente pac1 = new Paciente(1, "Fátima", "Persi", 46113226, "fatimap@gmail.com", LocalDate.now(), dom1);

        Domicilio dom2 = new Domicilio("9 de Julio", 120, "CABA", "Buenos Aires");
        Paciente pac2 = new Paciente(2, "Magali", "Pesqueira", 354473, "maga@mail.com", LocalDate.now(), dom2);

        Domicilio dom3 = new Domicilio("Avenida Mayo", 450, "Bariloche", "Bariloche");
        Paciente pac3 = new Paciente(3, "Jesus", "Cambronero", 4448544, "jesus@mail.com", LocalDate.now(), dom3);

        // crea los tipos de odontologos
        Odontologo odon1 = new Cirujano(10, "Guillermina", "Cotrone", 1234);
        Odontologo odon2 = new Ortodoncista(11, "Tomas", "Barea", 5678);
        Odontologo odon3 = new General(12, "Camilo", "Hunter", 9012);

        // asocia el odontologo con el paciente por la recep
        recepcionista.registrarTurno(pac1, odon1, LocalDate.of(2026, 5, 20), LocalTime.of(15, 30));
        recepcionista.registrarTurno(pac2, odon2, LocalDate.of(2026, 5, 21), LocalTime.of(10, 00));
        recepcionista.registrarTurno(pac3, odon3, LocalDate.of(2026, 5, 22), LocalTime.of(18, 15));

        // imprime en consola por los ToString()
        System.out.println("===============================================");
        System.out.println("       SISTEMA DE GESTIÓN ODONTOLÓGICA         ");
        System.out.println("===============================================\n");

        System.out.println("--------- DATOS DE DOMICILIOS ---------");
        System.out.println(dom1.toString());
        System.out.println(dom2.toString());
        System.out.println(dom3.toString());
        System.out.println("---------------------------------------\n");

        System.out.println("--------- DATOS DE PACIENTES ----------");
        System.out.println(pac1.toString());
        System.out.println(pac2.toString());
        System.out.println(pac3.toString());
        System.out.println("---------------------------------------\n");

        System.out.println("--------- DATOS DE ODONTÓLOGOS --------");
        System.out.println(odon1.toString());
        System.out.println(odon2.toString());
        System.out.println(odon3.toString());
        System.out.println("---------------------------------------\n");

        System.out.println("--------- TURNOS REGISTRADOS ----------");
        for (Turno t : recepcionista.listarTurnos()) {
            System.out.println(t.toString());
        }
        System.out.println("---------------------------------------\n");

        System.out.println("===============================================");
        System.out.println("        EJECUCIÓN FINALIZADA EXITOSAMENTE      ");
        System.out.println("===============================================");
    }
}