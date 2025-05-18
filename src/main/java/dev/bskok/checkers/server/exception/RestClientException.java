package dev.bskok.checkers.server.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RestClientException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public RestClientException(int statusCode, String responseBody, String message) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
