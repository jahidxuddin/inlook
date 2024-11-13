package de.ju.server.models;

public class Email {
    private String sender;
    private String recipient;
    private String body;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "From: " + sender + "\nTo: " + recipient + "\nBody:\n" + body;
    }
}
