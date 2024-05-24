package arquitectura.apiFestivos.Entidades.Dtos;

import java.util.Date;
import lombok.Data;

//DTO sencillo de Festivo, con nombre y fecha
@Data
public class FestivoDto {

    private String nombre;
    private Date Fecha;

    public FestivoDto(String nombre, Date fecha) {
        this.nombre = nombre;
        Fecha = fecha;
    }
}
