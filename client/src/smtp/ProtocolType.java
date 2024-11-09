package smtp.client;

public enum ProtocolType {

        HELO,
        AUTH;



        public String asString() {
            return this.name();
        }
    }

