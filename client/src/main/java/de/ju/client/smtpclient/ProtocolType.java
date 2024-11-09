package de.ju.client.smtpclient;

public enum ProtocolType {

        HELO,
        AUTH;



        public String asString() {
            return this.name();
        }
    }

