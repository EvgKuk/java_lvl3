package client.gui;

import server.auth.AuthService;
import server.auth.BaseAuthService;
import server.auth.ClientHandler;

import java.util.function.Consumer;

public class ClientChatFrame implements ChatFrameInteraction {
    private final ChatFrame chatFrame;

    public ClientChatFrame(Consumer<String> messageConsumer) {
        this.chatFrame = new ChatFrame("Client Chat v1.0", messageConsumer);
    }

    @Override
    public void append(String message) {
        chatFrame.getChatArea().append(message);
        chatFrame.getChatArea().append("\n");
    }
}

