package server.auth;

import server.MyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/** Класс ClientHandler - отвечает за общение сервера с каждым клиентом.
 * Новый объект ClientHandler создаётся при подключении нового клиента.
 * Объект ClientHandler - аутентифицирует клиента и получает от него сообщения.
 */

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    public String getName() {
        return name;
    }

    /*
     Запуск объекта ClientHandler с запуском отдельного потока, который читает все сообщения клиента.
     каждый ClientHandler получил ссылку на сервер, к которому он прикреплён
     для возможности обратиться к методам этого сервера.
     Поле name отвечает за ник клиента, если name пуст, клиент считается неавторизованным.
    */

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    /*
     метод authentication() - сервер ожидает от клиента сообщения вида «/auth login password»,
     при получении разбивает его на части и проверяет наличие учётной записи с таким логином/паролем,
     если запись есть и не занята другим пользователем, отсылаем клиенту сообщение об успешной авторизации
     и его ник (например, «/authok nick1») и рассылаем всем клиентам сообщение о том, что подключился новый участник,
     подписываем этого участника на рассылку чата и выходим из цикла авторизации.
     Если авторизация по какой-то причине не удалась, отсылаем клиенту сообщение с причиной отказа.
    */

    public void authentication() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick =
                        myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    /*
     Цикл обмена сообщениями до тех пор, пока клиент не пришлёт команду «/end»,
     в результате которой выкидываем его из списка рассылки, закрываем сокет
     и завершаем поток чтения сообщений от него.
     метод readMessages() - чтение/обработка сообщений
     Этот цикл обработки сообщений претерпел некоторые изменения.
     Как только от клиента приходит сообщение, производится проверка на наличие служебных команд,
     начинающихся с символа /.
     Если такой символ стоит на первом месте, обрабатываем пришедшую команду, если нет — делаем рассылку
     сообщения всем участникам чата.
     В качестве служебной команды добавлена возможность отсылки личных сообщений через шаблон
     «/w имя_получателя сообщение», если сервер получает такое сообщение, извлекает имя_получателя
     и текстовое сообщение, после чего через метод сервера sendMsgToClient() отсылает его.
    */

    public void readMessages () throws IOException {
        String msg;
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/")) {
                if (str.equals("/end")) {
                    break;
                }
                if (str.startsWith("/w ")) {
                    String[] tokens = str.split("\\s");
                    String nick = tokens[1];
                    msg = str.substring(4 + nick.length());
                    myServer.sendMsgToClient(this, nick, msg);
                }
                /* НАЧАЛО
                * Фрагмент кода для смены ника при работе с базой данных!
                * */
                if (str.startsWith("/changenick ")) {
                    String[] tokens = str.split("\\s",2);
                    if (tokens.length < 2) {
                        continue;
                    }
                    if (tokens[1].contains(" ")){
                       sendMsg("Ник не может содержать пробелы");
                       continue;
                    }
                    if (myServer.getAuthService().changeNick(this.name, tokens[1])) {
                        sendMsg("/yournick " + tokens[1]);
                        sendMsg("ваш ник изменён на " + tokens[1]);
                        this.name = tokens[1];
                        myServer.broadcastClientsList();
                        }else {
                        sendMsg("не удалось сменить ник, так как ник " + tokens[1] + " уже существует");
                    }
                }
                /*  КОНЕЦ
                 * Фрагмент кода для смены ника при работе с базой данных!
                 * */
                continue;
            }
            myServer.broadcastMsg(name + ": " + str);
        }
    }

    /* метод sendMsg(String msg) - отправка сообщений */
    public void sendMsg (String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* метод closeConnection() - закрывает соединение */
    public void closeConnection () {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

