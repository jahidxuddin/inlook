package de.ju.client.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Email {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int id;
    private String sender;
    private String recipient;
    private String subject;
    private String body;
    private String sentAt;

    public Email(int id, String sender, String recipient, String subject, String body, String sentAt) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public static Email parseEmail(String input) {
        // Remove the "Email{" and ending "}"
        input = input.substring(6, input.length() - 1);

        // Split the key-value pairs by ", "
        String[] fields = input.split(", ");

        // Initialize variables to hold field values
        String id = null, sender = null, recipient = null, subject = null, body = null, sentAt = null;

        // Parse each field
        for (String field : fields) {
            String[] keyValue = field.split("=", 2); // Split into key and value
            String key = keyValue[0];
            String value = keyValue[1];

            // Remove single quotes around values (if applicable)
            value = value.replaceAll("^'|'$", "");

            // Assign values based on the key
            switch (key) {
                case "id" -> id = value;
                case "sender" -> sender = value;
                case "recipient" -> recipient = value;
                case "subject" -> subject = value;
                case "body" -> body = value;
                case "sentAt" -> sentAt = value;
            }
        }

        assert sentAt != null;
        LocalDateTime dateTime = LocalDateTime.parse(sentAt, formatter);

        // Create and return the Email object
        assert id != null;
        assert body != null;
        return new Email(Integer.parseInt(id), sender, recipient, subject, body.replaceAll(" n ", "\n"), formatEmailTimestamp(dateTime.plusHours(1)));
    }

    private static String formatEmailTimestamp(LocalDateTime emailDateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate emailDate = emailDateTime.toLocalDate();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = today.minusDays(1);

        // Check if the email is fresh (within the last minute)
        if (Duration.between(emailDateTime, now).toMinutes() < 1) {
            return "jetzt";
        }

        // Check if the email is from today
        if (emailDate.isEqual(today)) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return emailDateTime.format(timeFormatter);
        }

        // For older emails
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d. MMM");
        return emailDateTime.format(dateFormatter);
    }

    @Override
    public String toString() {
        return "";
    }
}
