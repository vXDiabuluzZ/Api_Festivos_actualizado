package arquitectura.apiFestivos.Interfaces;

import arquitectura.apiFestivos.Entidades.Dtos.FestivoDto;
import java.util.Date;
import java.util.List;

public interface FestivoServicioInterface {
    List<FestivoDto> obtenerFestivos(int año, boolean guardar);
    boolean esFestivo(Date fecha);
    boolean esFechaValida(String strFecha);
}

