package de.ju.client;

import de.ju.client.smtp.SMTPClient;
import de.ju.client.smtp.exceptions.FailedAuthenticationException;
import de.ju.client.smtp.exceptions.FailedConnectionException;

public class Main {

        public static void main (String[]args){
            try {
                SMTPClient smtpClient = new SMTPClient("localhost", 1234, "testuser");
                Thread.sleep(1000);
                smtpClient.authenticate("testpassword");
                smtpClient.sendMail("abishan.arankesan@outlook.de", "TEST", "Among Us");
            } catch (FailedConnectionException | FailedAuthenticationException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

