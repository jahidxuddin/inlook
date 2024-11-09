package de.ju.client.smtpclient;




import de.ju.client.exceptions.FailedAuthenticationException;
import de.ju.client.exceptions.FailedConnectionException;
import de.ju.client.tools.GetLocalIP;
import socketio.Socket;

import java.io.IOException;
import java.util.Base64;

import static java.lang.StringTemplate.STR;

public class ClientSMTP {
    private Socket cSocket;
    private final String HELO = "HELO";
    private final String AUTH = "AUTH LOGIN";
    private final String MAILFROM = "MAIL FROM";
    private String RCPTTO = "RCPT TO";
    private String username;
    private String password;

    public ClientSMTP(String hostname, int port, String username, String password){
        try {
            cSocket = new Socket(hostname, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username = username;
        this.password = password;
    }

    public String stringToBase64(String arg){
        return Base64.getEncoder().encodeToString(arg.getBytes());
    }

    public Socket getcSocket() {
        return cSocket;
    }

    public void setcSocket(Socket cSocket) {
        this.cSocket = cSocket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void connect() throws FailedConnectionException, IOException {
        // Überprüfung ob Verbindung gelungen ist
        if(!cSocket.connect()) {
            throw new FailedConnectionException("Client could not connect to the server");
        }

        String response = cSocket.readLine();

        String status = response.substring(0, 2);
        // Überprüft nach status code 2xx (Positive Completion Reply)
        if (!status .startsWith("2")) {
            throw new FailedConnectionException("Bad response status from server ");
        }
    }

    public void sendHELO() throws IOException {
            cSocket.write(STR."\{HELO} \{GetLocalIP.getIP()}");
    }

    public void authenticate() throws IOException, InterruptedException, FailedAuthenticationException {
        String response = "";
        String status = "";

            cSocket.write(AUTH);
            Thread.sleep(100);
            do {
                response = cSocket.readLine();
                //prüft nach status code 3xx (Positive Intermediate Reply)
                status = response.substring(0, 2);
                if (!status.equalsIgnoreCase("334")) {
                    throw new FailedAuthenticationException("Bad authentication status from server");
                } else if (response.substring(3).equals("VXNlcm5hbWU6")) {
                    cSocket.write(stringToBase64(username));
                } else {
                    cSocket.write(stringToBase64(password));
                }
                Thread.sleep(100);
            } while (status.startsWith("2"));
    }

    public void mailRouting(String receiverUsername) throws IOException, FailedConnectionException, InterruptedException {
        cSocket.write(STR."\{MAILFROM}:<\{username}>");
        if(!cSocket.readLine().startsWith("2")){
            throw new FailedConnectionException("Bad response status from server");
        }
        Thread.sleep(100);
        cSocket.write(STR."\{RCPTTO}:<\{username}>");

        if(!cSocket.readLine().startsWith("2")){
            throw new FailedConnectionException("Bad response status from server");
        }

    }

    public void startSMTPSession() throws FailedConnectionException, IOException {
        connect();

    }

    public static void main(String[] args) {
        ClientSMTP clientSMTP = new ClientSMTP("a", 22, "wwa", "niger");

    }
}
