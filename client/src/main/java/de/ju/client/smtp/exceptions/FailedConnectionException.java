package de.ju.client.smtp.exceptions;

// Define a custom checked exception
public class FailedConnectionException extends Exception {
    // Constructor without any arguments

    // Constructor that accepts a custom message
    public FailedConnectionException(String message) {
        super(message);
    }

    // Constructor that accepts a custom message and cause
    public FailedConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public FailedConnectionException(Throwable cause) {
        super(cause);
    }
}