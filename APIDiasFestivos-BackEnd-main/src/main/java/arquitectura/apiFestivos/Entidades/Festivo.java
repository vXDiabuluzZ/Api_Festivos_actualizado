package arquitectura.apiFestivos.Entidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "festivo")
@Data

public class Festivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "dia", nullable = false)
    private int dia;

    @Column(name = "mes", nullable = false)
    private int mes;

    @Column(name = "año")
    private Integer año;

    @Column(name = "diaspascua", nullable = false)
    private int diasPascua;

    @ManyToOne
    @JoinColumn(name = "idtipo", referencedColumnName = "id")
    private Tipo tipo;

    //Obtener la Fecha
    public Date getFecha() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaStr = "";
        if (año != null) {
            fechaStr = año + "-" + String.format("%02d", mes) + "-" + String.format("%02d", dia);
        } else {
            fechaStr = String.format("%02d", mes) + "-" + String.format("%02d", dia);
        }
        try {
            return dateFormat.parse(fechaStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

     //Modificar la Fecha
    public void setFecha(Date fecha) {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

        año = Integer.parseInt(yearFormat.format(fecha));
        mes = Integer.parseInt(monthFormat.format(fecha));
        dia = Integer.parseInt(dayFormat.format(fecha));
    }
}
