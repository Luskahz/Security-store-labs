package br.edu.securitystore.iam.authentication.api.http.exception;
import br.edu.securitystore.iam.authentication.core.application.AuthenticationService;
import org.springframework.http.*;import org.springframework.web.bind.annotation.*;
@RestControllerAdvice(basePackages="br.edu.securitystore.iam.authentication.api.http")
public class AuthenticationExceptionHandler {
 @ExceptionHandler(AuthenticationService.InvalidCredentialsException.class)
 ProblemDetail invalid(){return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,"Credenciais ou token inválidos");}
}
