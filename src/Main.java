import Modelo.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        //instanciamos la Recepcionista
        Recepcionista recepcionista = new Recepcionista();
        // registro de odontologos
        System.out.println("--- REGISTRANDO ODONTÓLOGOS EN EL SISTEMA ---");
        Odontologo odon1 = new Cirujano(10, "Guillermina", "Cotrone", 1234,"Extraccion de muela");
        Odontologo odon2 = new Ortodoncista(11, "Tomas", "Barea", 5678,"Brackets invisibles");
        Odontologo odon3 = new General(12, "Camilo", "Hunter", 9012,"Limpieza");
        Odontologo odon4 = new General(13, "Juan", "Gonzales", 0, "Tratamiento de conducto");

        // llama al metodo
        System.out.println(recepcionista.altaOdontologo(odon1));
        System.out.println(recepcionista.altaOdontologo(odon2));
        System.out.println(recepcionista.altaOdontologo(odon3));
        System.out.println(recepcionista.altaOdontologo(odon4));

        System.out.println("-------------------------------------------\n");

        // domicilio+paciente
        Domicilio dom1 = new Domicilio("Necochea", 581, "Lobos", "Buenos Aires");
        Paciente pac1 = new Paciente(1, "Fatima", "Persi", 46113226, "fatimap@gmail.com", LocalDate.now(), dom1);
      //  System.out.println(pac1.getNombre());
        Domicilio dom2 = new Domicilio("Rosas", 120, "Temperley", "Buenos Aires");
        Paciente pac2 = new Paciente(2, "Magali", "Pesqueira", 354473, "maga@mail.com", LocalDate.now(), dom2);

        Domicilio dom3 = new Domicilio("Avenida Mayo", 450, "Bariloche", "Bariloche");
        Paciente pac3 = new Paciente(3, "Jesus", "Cambronero", 4448544, "jesus@mail.com", LocalDate.now(), dom3);

        //registro
        recepcionista.altaPaciente(pac1);
        recepcionista.altaPaciente(pac2);
        recepcionista.altaPaciente(pac3);

        // regis turno
        recepcionista.registrarTurno(pac1, odon1, LocalDate.of(2026, 5, 20), LocalTime.of(15, 30));
        recepcionista.registrarTurno(pac2, odon2, LocalDate.of(2026, 5, 21), LocalTime.of(10, 0));
        recepcionista.registrarTurno(pac3, odon3, LocalDate.of(2026, 5, 22), LocalTime.of(18, 15));

        // impresion por consola
        System.out.println("===============================================");
        System.out.println("       SISTEMA DE GESTIÓN ODONTOLÓGICA         ");
        System.out.println("===============================================\n");

        System.out.println("--------- PACIENTES Y SUS DIRECCIONES ---------");
        System.out.println(pac1.toString());
        System.out.println(pac2.toString());
        System.out.println(pac3.toString());
        System.out.println("-----------------------------------------------\n");

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