package am.runningtracker.controller.aspects;

import am.runningtracker.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    private ResponseEntity handleException(AppException exception) {
        return getResponse(exception);
    }

    public ResponseEntity<Map<String, String>> getResponse(AppException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorType", ex.getErrorType());
        response.put("errorMessage", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
