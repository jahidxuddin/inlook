package de.ju.client.tools;

import java.net.*;

public class GetLocalIP {

    public static String getIP() throws UnknownHostException{
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }
}

