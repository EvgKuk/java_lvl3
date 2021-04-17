package server;

import server.auth.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static server.auth.SQLHandler.*;

/**
 * MyServer хранит список подключенных клиентов, предназначенный для управления соединением с клиентом и рассылкой
 * сообщений.
 * При подключении и авторизации клиент добавляется в этот список через метод subscribe().
 * При отключении клиент удаляется через метод unsubscribe().
 * Для блокировки возможности авторизоваться нескольким клиентам под одной учётной записью
 * используется метод isNickBusy(), проверяющий занятость ника в текущем сеансе чата.
 */
public class MyServer {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;
    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() throws RuntimeException {
        clients = new ArrayList<>();
        authService = new SQLAuthService();
        authService.start();
        if (!SQLHandler.connect()) {
            throw new RuntimeException("База данных не подключена");
        }

        try (ServerSocket server = new ServerSocket(PORT)) {
            /*
             * Ниже закомментирован исходный код работы чата без баз данных.
             * */
//          authService = new BaseAuthService();
//          authService.start();
            /*
            * Ниже приведён код для работы чата с базой данных
            * */
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    // isNickBusy(String nick) - метод проверяет занят ли никнейм, чтобы не было одинаковых учётных записей
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    //broadcastMsg(String msg) - метод, который отправляет сообщение всем подключенным клиентам
    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    //sendMsgToClient() - метод для отправки сообщения от клиента "from" клиенту с указанным ником "nameTo", при
    //отсутствии такого пользователя отсылается соответствующее сообщение отправителю
    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg){
        for (ClientHandler o : clients){
            if (o.getName().equals(nickTo)) {
                o.sendMsg("от " + from.getName() + " : " + msg);
                from.sendMsg("клиенту " + nickTo + " : " + msg);
                return;
            }
        }
        from.sendMsg("Участника с ником " + nickTo + " нет в чате");
    }

    //unsubscribe(ClientHandler o) - метод удаляет ранее подключенного клиента из списка клиентов
    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastClientsList();
    }

    //subscribe(ClientHandler o) - метод добавляет подключенного клиента в список клиентов
    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientsList();
    }

    //broadcastClientsList() - метод формирует список участников чата в виде строки «/clients nick1 nick2 nick3...» и
    //рассылает его всем клиентам через методы unsubscribe и subscribe
    public synchronized void broadcastClientsList(){
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler o : clients){
            sb.append(o.getName() + " ");
        }
        broadcastMsg(sb.toString());
    }

}