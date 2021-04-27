package server.auth;

public class SQLAuthService implements AuthService {

    @Override
    public void start() {
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        return SQLHandler.getNickByLoginPass(login, pass);
    }

    @Override
    public boolean registration(String login, String pass, String nick) {
        return SQLHandler.registration(login, pass, nick);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean changeNick(String nick, String newNick) {
        return SQLHandler.changeNick(nick, newNick);
    }
}
