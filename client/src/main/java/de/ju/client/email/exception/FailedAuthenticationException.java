package de.ju.client.service.email.exception;

public class FailedAuthenticationException extends Exception {
    public FailedAuthenticationException(String message) {
        super(message);
    }

    public FailedAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedAuthenticationException(Throwable cause) {
        super(cause);
    }
}
