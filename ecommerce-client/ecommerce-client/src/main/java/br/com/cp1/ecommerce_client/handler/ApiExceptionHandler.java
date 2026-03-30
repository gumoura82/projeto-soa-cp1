package br.com.cp1.ecommerce_client.handler;

import br.com.cp1.ecommerce_client.exception.TransportadoraSoapException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(TransportadoraSoapException.class)
    public ProblemDetail handleSoapException(TransportadoraSoapException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problemDetail.setTitle("Falha ao consultar a transportadora");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ProblemDetail handleValidationException(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Parametros invalidos");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}
