package br.com.radarcidadaogv.denuncia;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestControllerAdvice public class ApiExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String, String> invalid(RuntimeException exception) { return Map.of("mensagem", exception.getMessage()); }
    @ExceptionHandler(java.util.NoSuchElementException.class) @ResponseStatus(HttpStatus.NOT_FOUND) public Map<String, String> missing(RuntimeException exception) { return Map.of("mensagem", exception.getMessage()); }
}
