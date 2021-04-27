package client;

import client.gui.ClientChatFrame;
import client.network.BasicChatNetwork;
import client.network.ClientNetwork;


import java.util.function.Consumer;

public class ClientChatAdapter {
    private final ClientNetwork network;
    private final ClientChatFrame frame;

    public ClientChatAdapter(String host, int port) {
        this.network = new BasicChatNetwork(host, port);
        this.frame = new ClientChatFrame(sender());
        receive();
    }

    private Consumer<String> sender() {
        return new Consumer<String>() {
            @Override
            public void accept(String message) {
                network.send(message);
            }
        };
    }

    private void receive() {
        new Thread(() -> {
            while (true) {
                String message = network.receive();
                frame.append(message);
            }
        })
                .start();
    }
}

