package com.techtest.computedashboardapi.exception;

import com.techtest.computedashboardapi.model.response.ResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_CODE_COMMUNICATION_FAILED = "Communication Failed";
    private static final String ERROR_CODE_REQUEST_PARSING = "Request couldn't be parsed";

    @ExceptionHandler(CommunicationFailedException.class)
    protected ResponseEntity<Object> handleCommunicationFailedException(CommunicationFailedException ex) {
        log.error("Error communicating with AWS service {}", ex);
        return buildResponseEntity(new ResponseError(INTERNAL_SERVER_ERROR, ERROR_CODE_COMMUNICATION_FAILED, ex));
    }

    @ExceptionHandler({RequestParsingException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleRequestParsingException(Exception ex) {
        log.error("Error parsing the request parameter {}", ex);
        return buildResponseEntity(new ResponseError(BAD_REQUEST, ERROR_CODE_REQUEST_PARSING, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ResponseError errorResponse) {
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }


}
