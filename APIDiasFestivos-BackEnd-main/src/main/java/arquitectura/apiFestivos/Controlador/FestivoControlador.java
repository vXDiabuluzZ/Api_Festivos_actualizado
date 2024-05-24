package arquitectura.apiFestivos.Controlador;

import arquitectura.apiFestivos.Entidades.Festivo;
import arquitectura.apiFestivos.Entidades.Dtos.FestivoDto;
import arquitectura.apiFestivos.Servicio.FestivoServicio;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.text.ParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class FestivoControlador {

    private FestivoServicio festivoServicio;

    public FestivoControlador(FestivoServicio festivoServicio) {
        this.festivoServicio = festivoServicio;
    }

    // Obtiene todos los festivos de la Base de Datos.
    @GetMapping("/get")
    @ApiOperation(value = "Obtiene todos los festivos", response = Festivo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Festivos encontrado existosamente"),
            @ApiResponse(code = 400, message = "La petición es invalida"),
            @ApiResponse(code = 500, message = "Error interno al procesar la respuesta")})
    public ResponseEntity<List<Festivo>> getFestivos() {
        return ResponseEntity.ok(festivoServicio.getFestivos());
    }

    // Obtiene todos los festivos de un año específico, sin guardar (En tiempo de
    // Ejecución).
    @GetMapping("/obtener/{año}")
    @ApiOperation(value = "Obtiene todos los festivos de un año específico", response = Festivo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Festivos encontrado existosamente"),
            @ApiResponse(code = 400, message = "La petición es invalida"),
            @ApiResponse(code = 500, message = "Error interno al procesar la respuesta")})
    public ResponseEntity<List<FestivoDto>> obtenerFestivos(@PathVariable int año) {
        List<FestivoDto> festivosGuardados = festivoServicio.obtenerFestivos(año, false);
        return ResponseEntity.ok(festivosGuardados);
    }

    // Guarda todos los festivos de un año específico, en la Base de datos.
    @GetMapping("/guardar/{año}")
    @ApiOperation(value = "Obtiene todos los festivos de un año específico", response = Festivo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Festivos guardados existosamente"),
            @ApiResponse(code = 400, message = "La petición es invalida"),
            @ApiResponse(code = 500, message = "Error interno al procesar la respuesta")})
    public ResponseEntity<String> guardarFestivos(@PathVariable int año) {
        List<FestivoDto> festivosGuardados = festivoServicio.obtenerFestivos(año, true);

        // No es necesaio retornar la lista de FestivoDto
        if (festivosGuardados == null) {
            return ResponseEntity.ok("Festivos Guardados");
        }
        return ResponseEntity.ok("Festivos No Guardados");
    }

    // Verifica si es festivo para una fecha específica, (En tiempo de
    // Ejecución).
    @GetMapping("/verificar/{año}/{mes}/{dia}")
    @ApiOperation(value = "Verifica si es festivo para una fecha específica", response = Festivo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Consulta exitosa"),
            @ApiResponse(code = 400, message = "La petición es invalida"),
            @ApiResponse(code = 500, message = "Error interno al procesar la respuesta")})
    public ResponseEntity<String> verificarFestivo(
            @PathVariable int año,
            @PathVariable int mes,
            @PathVariable int dia) throws ParseException {
        String mensaje = festivoServicio.verificarFestivo(año, mes, dia);
        return ResponseEntity.ok(mensaje);
    }

    // Verifica si es festivo para una fecha específica, de la Base de Datos.
    @GetMapping("/verificarbd/{año}/{mes}/{dia}")
    @ApiOperation(value = "Verifica si es festivo para una fecha específica", response = Festivo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Consulta exitosa"),
            @ApiResponse(code = 400, message = "La petición es invalida"),
            @ApiResponse(code = 500, message = "Error interno al procesar la respuesta")})
    public ResponseEntity<String> verificarFestivoBD(
            @PathVariable int año,
            @PathVariable int mes,
            @PathVariable int dia) {
        String mensaje = festivoServicio.verificarFestivoBD(año, mes, dia);
        return ResponseEntity.ok(mensaje);
    }
}