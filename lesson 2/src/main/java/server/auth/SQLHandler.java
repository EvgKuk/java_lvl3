package server.auth;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement prStmtGetNick;
    private static PreparedStatement prStmtRegistration;
    private static PreparedStatement prStmtChangeNick;

    public static boolean connect() {
        try {
           Class.forName("org.sqlite.JDBC");
           connection = DriverManager.getConnection("jdbc:sqlite:chatDB.db");
           prepareAllStmt();
           return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void prepareAllStmt() throws SQLException {
        prStmtGetNick = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND pass = ?;");
        prStmtRegistration = connection.prepareStatement("INSERT INTO users (login, pass, nick) VALUES (? ,? ,? );");
        prStmtChangeNick = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?;");
    }


    public static String getNickByLoginPass(String login, String pass) {
        String nick = "is out of name";
        try {
            prStmtGetNick.setString(1, login);
            prStmtGetNick.setString(2, pass);
            ResultSet resSet = prStmtGetNick.executeQuery();
            if (resSet.next()) nick = resSet.getString(1);
            resSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return nick;
    }

    public static boolean registration(String login, String pass, String nick) {
        try {
            prStmtRegistration.setString(1, login);
            prStmtRegistration.setString(2, pass);
            prStmtRegistration.setString(3, nick);
            prStmtRegistration.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static boolean changeNick(String nick, String newNick) {
        try {
            prStmtChangeNick.setString(1, newNick);
            prStmtChangeNick.setString(2, nick);
            prStmtChangeNick.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void disconnect() {
        try {
            prStmtRegistration.close();
            prStmtGetNick.close();
            prStmtChangeNick.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
