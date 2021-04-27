package server.auth;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private class Entry {
        String login;
        String pass;
        String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    //Интерфейс List сохраняет последовательность добавления элементов;
    // Позволяет осуществлять доступ к элементу по индексу.
    private List<Entry> entries;

    //start() - служит для запуска сервиса авторизации AuthService;
    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    //stop() - служит для остановки сервиса авторизации AuthService;
    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }

    //BaseAuthService - реализует интерфейс AuthService
    public BaseAuthService() {
        entries = new ArrayList<>();

        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));

        for (int i = 0; i < 10; i++){
            entries.add(new Entry("login" + i,"pass" + i, "nick" + i  ));
        }
    }

    @Override
    //getNickByLoginPass() - служит для получения ника по логину/паролю
    // присваивает null, если такой пары логин/пароль нет;
    public String getNickByLoginPass(String login, String pass) {
        for (Entry o : entries) {
            if (o.login.equals(login) && o.pass.equals(pass)) return o.nick;
        }
        return null;
    }

    @Override
    public boolean registration(String login, String pass, String nick) {
        for (Entry o : entries) {
            if(o.login.equals(login) || o.nick.equals(nick)){
                return false;
            }
        }
        entries.add(new Entry(login, pass, nick));
        return true;
    }

    @Override
    public boolean changeNick(String nick, String newNick) {
        return false;
    }
}
