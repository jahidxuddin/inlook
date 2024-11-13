package de.ju.client.smtp.exceptions;

public class FailedAuthenticationException extends Exception{

    public FailedAuthenticationException(String message) {
        super(message);
    }

    // Constructor that accepts a custom message and cause
    public FailedAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public FailedAuthenticationException(Throwable cause) {
        super(cause);
    }
}
