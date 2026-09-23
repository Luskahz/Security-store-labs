package br.edu.securitystore.iam.api.http.exception;

import br.edu.securitystore.iam.identity.core.application.IdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"br.edu.securitystore.iam.api.http", "br.edu.securitystore.iam.identity.api.http"})
public class IamExceptionHandler {
    @ExceptionHandler(IdentityService.DuplicateEmailException.class)
    ProblemDetail duplicateEmail() { return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "E-mail já cadastrado"); }
}
