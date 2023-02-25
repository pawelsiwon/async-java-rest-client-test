package org.example.clients;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(Throwable cause) {
        super(cause);
    }
}
