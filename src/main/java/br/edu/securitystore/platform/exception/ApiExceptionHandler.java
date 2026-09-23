package br.edu.securitystore.platform.exception;

import java.util.NoSuchElementException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail missing() { return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Recurso não encontrado"); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation() { return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados da requisição inválidos"); }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalid(IllegalArgumentException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getMessage());
    }

}
