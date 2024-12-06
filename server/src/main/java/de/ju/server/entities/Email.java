package de.ju.server.entities;

public class Email {
    private int id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;

    public Email() {
    }

    public Email(int id, String sender, String recipient, String subject, String body) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    public int getId() {
        return id;
    }

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int calcSize() {
        String email = sender + recipient + subject + body;
        return email.getBytes().length;
    }

    @Override
    public String toString() {
        return "Email{" + "id=" + id + ", sender='" + sender + '\'' + ", recipient='" + recipient + '\'' + ", subject='" + subject + '\'' + ", body='" + body.trim() + '\'' + '}';
    }
}
