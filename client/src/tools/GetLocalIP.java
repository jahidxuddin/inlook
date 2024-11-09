package tools;

import java.net.*;

public abstract class GetLocalIP {
    public static void main(String[] args) {
    }


    public static String getIP() throws UnknownHostException{
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }
}

