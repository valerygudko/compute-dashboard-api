package com.techtest.computedashboardapi.exception;

public class RequestParsingException extends Exception {

    public RequestParsingException(Throwable cause) {
        super(cause);
    }

    public RequestParsingException(String errorMessage) {
        super(errorMessage);
    }

}
