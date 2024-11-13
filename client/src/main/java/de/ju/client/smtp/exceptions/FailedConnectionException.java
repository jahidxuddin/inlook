package de.ju.client.smtp.exceptions;

public class FailedConnectionException extends Exception {
    public FailedConnectionException(String message) {
        super(message);
    }

    public FailedConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedConnectionException(Throwable cause) {
        super(cause);
    }
}