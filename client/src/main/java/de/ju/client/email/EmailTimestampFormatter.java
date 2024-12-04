package de.ju.client.service.email;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailTimestampFormatter {
    /**
     * Formats the email timestamp into a human-readable string.
     *
     * @param emailDateTime The timestamp of the email
     * @return A string representing the formatted timestamp
     */
    public static String formatEmailTimestamp(LocalDateTime emailDateTime) {
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
}
