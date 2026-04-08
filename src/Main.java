import Modelo.Paciente;
import Modelo.Odontologo;

public class Main {

    //PACIENTE
    public static void main(String[] args){
        Paciente paciente = new Paciente(1,"Magali","Pesqueira",1111,"mpesqueira@uade.edu.ar");
        Paciente paciente1 = new Paciente(2,"fatima","persi",2222,"fatipersi@uade.edu.ar");
        Odontologo odontologo = new Odontologo(01,"jesus","Cambronero",123);
         paciente.mostrarDatosPaciente();
        System.out.println("------------------------------------");
         paciente1.mostrarDatosPaciente();
        System.out.println("-------Odontologo--------");
         odontologo.mostrarDatosOdontologo();

        System.out.println("------------------comparando nombres----------------------");
        System.out.println(paciente.compararNombrePaciente(paciente1));
    }

}
