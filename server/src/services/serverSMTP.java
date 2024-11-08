import java.io.IOException;
import java.util.*;
import socketio.*;

public class serverSMTP {
    private int port = 1234;
    private ServerSocket sSocket;
    private Socket cSocket;

    public serverSMTP() {
        try {
            sSocket = new ServerSocket(port);
            cSocket = sSocket.accept();
            cSocket.write("220 smtp.example.com ESMTP Service Ready");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
