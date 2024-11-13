package de.ju.server;

public class Email {
    private String sender;
    private String recipient;
    private String body;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "From: " + sender + "\nTo: " + recipient + "\nBody:\n" + body;
    }
}
