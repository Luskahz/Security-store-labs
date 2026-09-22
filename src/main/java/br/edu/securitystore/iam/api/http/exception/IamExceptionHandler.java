package br.edu.securitystore.iam.api.http.exception;

import br.edu.securitystore.iam.core.application.IamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.edu.securitystore.iam.api.http")
public class IamExceptionHandler {
    @ExceptionHandler(IamService.DuplicateEmailException.class)
    ProblemDetail duplicateEmail() { return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "E-mail já cadastrado"); }
}
