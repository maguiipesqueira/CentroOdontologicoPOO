package uiSwing;

import entity.Factura;

import javax.swing.*;
import java.awt.*;

// muestra el comprobante de una factura con el desglose del cobro
public class ComprobanteFactura {

    // abre un dialogo con el comprobante de la factura
    public static void mostrar(Component padre, Factura f, java.util.List<pago.MedioPago> mediosPago) {
        JTextArea texto = new JTextArea(armarTexto(f, mediosPago));
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13)); // fuente de ancho fijo para alinear los importes

        JOptionPane.showMessageDialog(padre, texto, "Comprobante - Factura #" + f.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    // arma el texto del comprobante: datos de la factura y el desglose de importes
    public static String armarTexto(Factura f, java.util.List<pago.MedioPago> mediosPago) {
        StringBuilder sb = new StringBuilder();
        sb.append("CENTRO ODONTOLOGICO \"SONRISA FELIZ\"\n");
        sb.append("Factura N° ").append(f.getId()).append("\n");
        sb.append("Fecha: ").append(f.getFecha()).append("\n");
        sb.append("Paciente: ").append(f.getPacienteNombre()).append("\n");
        sb.append("Tratamiento: ").append(f.getConcepto()).append("\n");
        String nombreMedio = mediosPago.stream()
                .filter(medio -> medio.getCodigo().equals(f.getMetodoPagoCodigo()))
                .map(pago.MedioPago::getNombre)
                .findFirst()
                .orElse(f.getMetodoPagoCodigo());
        sb.append("Medio de pago: ").append(nombreMedio).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(linea("Precio de lista", FormatoPlata.formatear(f.getMontoBase())));
        sb.append(linea("Desc. obra social", FormatoPlata.formatearDescuento(f.getDescuentoObraSocial())));
        sb.append(linea("Desc. por medio de pago", FormatoPlata.formatearDescuento(f.getDescuentoMedioPago())));
        sb.append("------------------------------------------\n");
        sb.append(linea("TOTAL PAGADO", FormatoPlata.formatear(f.getMontoTotal())));
        return sb.toString();
    }

    // una linea con el concepto a la izquierda y el importe a la derecha
    private static String linea(String concepto, String importe) {
        return String.format("%-26s%16s%n", concepto, importe);
    }
}