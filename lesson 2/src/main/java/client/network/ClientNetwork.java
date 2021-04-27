package client.network;

public interface ClientNetwork {
    void send(String message);
    String receive();
}

