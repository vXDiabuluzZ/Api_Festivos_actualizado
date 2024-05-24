package arquitectura.apiFestivos.Servicio;

import arquitectura.apiFestivos.Interfaces.FestivoServicioInterface;
import arquitectura.apiFestivos.Entidades.Festivo;
import arquitectura.apiFestivos.Entidades.Dtos.FestivoDto;
import arquitectura.apiFestivos.Repositorio.FestivoRepositorio;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FestivoServicio implements FestivoServicioInterface {

    private final Logger log = LoggerFactory.getLogger(FestivoServicio.class);

    @Autowired
    FestivoRepositorio festivoRepositorio;

    private Date obtenerDomingoPascua(int año) {
        int mes, dia, A, B, C, D, E, M, N;
        M = 0;
        N = 0;
        if (año >= 1583 && año <= 1699) {
            M = 22;
            N = 2;
        } else if (año >= 1700 && año <= 1799) {
            M = 23;
            N = 3;
        } else if (año >= 1800 && año <= 1899) {
            M = 23;
            N = 4;
        } else if (año >= 1900 && año <= 2099) {
            M = 24;
            N = 5;
        } else if (año >= 2100 && año <= 2199) {
            M = 24;
            N = 6;
        } else if (año >= 2200 && año <= 2299) {
            M = 25;
            N = 0;
        }

        A = año % 19;
        B = año % 4;
        C = año % 7;
        D = ((19 * A) + M) % 30;
        E = ((2 * B) + (4 * C) + (6 * D) + N) % 7;

        // Decidir entre los 2 casos
        if (D + E < 10) {
            dia = D + E + 22;
            mes = 3; // Marzo
        } else {
            dia = D + E - 9;
            mes = 4; // Abril
        }

        // Excepciones especiales
        if (dia == 26 && mes == 4)
            dia = 19;
        if (dia == 25 && mes == 4 && D == 28 && E == 6 && A > 10)
            dia = 18;
        return new Date(año - 1900, mes - 1, dia);
    }

    private Date agregarDias(Date fecha, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DATE, dias);
        return cal.getTime();
    }

    private Date siguienteLunes(Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        if (c.get(Calendar.DAY_OF_WEEK) > Calendar.MONDAY)
            fecha = agregarDias(fecha, 9 - c.get(Calendar.DAY_OF_WEEK));
        else if (c.get(Calendar.DAY_OF_WEEK) < Calendar.MONDAY)
            fecha = agregarDias(fecha, 1);
        return fecha;
    }

    private List<Festivo> calcularFestivos(List<Festivo> festivos, int año) {
        if (festivos != null) {
            Date pascua = obtenerDomingoPascua(año);
            int i = 0;
            for (final Festivo festivo : festivos) {
                switch (festivo.getTipo().getId()) {
                    case 1:
                        festivo.setFecha(new Date(año - 1900, festivo.getMes() - 1, festivo.getDia()));
                        break;
                    case 2:
                        festivo.setFecha(siguienteLunes(new Date(año - 1900, festivo.getMes() - 1, festivo.getDia())));
                        break;
                    case 3:
                        festivo.setFecha(agregarDias(pascua, festivo.getDiasPascua()));
                        break;
                    case 4:
                        festivo.setFecha(siguienteLunes(agregarDias(pascua, festivo.getDiasPascua())));
                        break;
                }
                festivos.set(i, festivo);
                i++;
            }
        }
        return festivos;
    }

    //Calcular festivos dado un año, puede guardar en la base de datos o simplemente calcular
    @Override
    public List<FestivoDto> obtenerFestivos(int año, boolean guardar) {
        List<Festivo> todosFestivos = festivoRepositorio.findAll();
        List<Festivo> festivosNuevo = new ArrayList<>();
        List<Festivo> festivosBase = new ArrayList<>();
        List<Festivo> festivosConAño = new ArrayList<>();

        //Separando los festivos Base de los demás 
        for (Festivo festivo : todosFestivos) {
            if (festivo.getAño() != null) {
                festivosConAño.add(festivo);
            } else {
                festivosBase.add(festivo);
            }
        }

        festivosBase = clonarFestivos(festivosBase);
        festivosNuevo = calcularFestivos(festivosBase, año);

        log.info("TODOS LOS festivosNuevos");
        for (Festivo festivo : festivosNuevo) {
            log.info(festivo.toString());
        }

        if (guardar) {
            // Guardo en la bd festivoRepositorio.save(todosFestivos), asegurandose de no repetir fechas
            for (Festivo festivo : festivosNuevo) {
                boolean esDuplicado = false;
                for (Festivo festivoExistente : festivosConAño) {

                    if (Objects.equals(festivo.getDia(), festivoExistente.getDia()) &&
                            Objects.equals(festivo.getMes(), festivoExistente.getMes()) &&
                            Objects.equals(festivo.getAño(), festivoExistente.getAño())) {
                        esDuplicado = true;
                        break;
                    }
                }

                if (!esDuplicado) {
                    festivoRepositorio.save(festivo);
                    log.info("Festivo Guardado: " + festivo.toString());
                } else {

                    log.warn("Festivo duplicado: " + festivo.getNombre());
                }
            }
        }

        if (!guardar) {
            // Retorna los festivos como un DTO con sólo nombre y fecha
            List<FestivoDto> fechas = new ArrayList<FestivoDto>();
            for (final Festivo festivo : festivosNuevo) {
                fechas.add(new FestivoDto(festivo.getNombre(), festivo.getFecha()));
            }
            return fechas;
        }
        return null;
    }

    private boolean fechasIguales(Date fecha1, Date fecha2) {
        return fecha1.equals(fecha2);
    }

    private boolean esFestivo(List<Festivo> festivos, Date fecha) {
        if (festivos != null) {
            festivos = calcularFestivos(festivos, fecha.getYear() + 1900);

            for (final Festivo festivo : festivos) {
                if (fechasIguales(festivo.getFecha(), fecha))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean esFestivo(Date fecha) {
        List<Festivo> festivos = festivoRepositorio.findAll();
        return esFestivo(festivos, fecha);
    }

    @Override
    public boolean esFechaValida(String strFecha) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            df.parse(strFecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    //Obtener todos los festivos de la base de datos
    public List<Festivo> getFestivos() {
        List<Festivo> FestivoList = festivoRepositorio.findAll();
        if (FestivoList.isEmpty()) {
            log.info("No se encuentran Dias festivos en la base de datos");

        }
        return FestivoList;
    }

    //Verifica si es festivos desde el cálculo en tiempo de ejecución
    public String verificarFestivo(int año, int mes, int dia) throws ParseException {
        String strFecha = año + "-" + mes + "-" + dia;

        if (esFechaValida(strFecha)) {
            log.info("RESULTADO DE LA FECHA ES VALIDA");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = df.parse(strFecha);

            List<FestivoDto> festivos = obtenerFestivos(año, false);

            for (FestivoDto festivo : festivos) {
                if (fechasIguales(festivo.getFecha(), fechaDate)) {
                    log.info("RESULTADO DE LA FECHA EXISTE");
                    return "Es festivo";
                }
            }
            return "No es festivo";
        }
        return "Fecha no válida";
    }

    //Verifica si es festivo desde la Base de Datos
    public String verificarFestivoBD(int año, int mes, int dia) {
        String strFecha = año + "-" + mes + "-" + dia;

        if (esFechaValida(strFecha)) {
            List<Festivo> festivos = festivoRepositorio.findAll();

            for (Festivo festivo : festivos) {
                //Para festivos sin año. Los festivos Base
                if (festivo.getAño() == null) {

                    if (festivo.getMes() == mes && festivo.getDia() == dia) {
                        return "Es festivo";
                    }
                
                //Para festivos con año
                } else {
                    if (festivo.getAño() == año && festivo.getMes() == mes && festivo.getDia() == dia) {
                        return "Es festivo";
                    }
                }
            }
            return "No es festivo";
        }
        return "Fecha no válida";
    }

    //Método Clonar Festivos
    private List<Festivo> clonarFestivos(List<Festivo> festivos) {
        List<Festivo> festivosNuevo = new ArrayList<>();

        for (Festivo festivo : festivos) {
            Festivo festivoNuevo = new Festivo();

            festivoNuevo.setId(null);
            festivoNuevo.setNombre(festivo.getNombre());
            festivoNuevo.setDia(festivo.getDia());
            festivoNuevo.setMes(festivo.getMes());
            festivoNuevo.setAño(festivo.getAño());
            festivoNuevo.setDiasPascua(festivo.getDiasPascua());
            festivoNuevo.setTipo(festivo.getTipo());

            festivosNuevo.add(festivoNuevo);
        }
        return festivosNuevo;
    }
}