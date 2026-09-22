package br.edu.securitystore.config;

import java.util.NoSuchElementException;
import br.edu.securitystore.iam.core.application.IamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail missing() { return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Recurso não encontrado"); }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalid(IllegalArgumentException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    @ExceptionHandler(IamService.DuplicateEmailException.class)
    ProblemDetail duplicateEmail() { return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "E-mail já cadastrado"); }
}
