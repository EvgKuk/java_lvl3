package server.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Интерфейс AuthService - описывает правила работы с сервисом авторизации;
 * метод start() - служит для запуска интерфейса;
 * метод getNickByLoginPass() - служит для получения ника по логину/паролю либо null, если такой пары логин/пароль нет;
 * метод stop() - служит для остановки сервиса;
 *
 * BaseAuthService - реализация интерфейса AuthService
 * Основана на использовании списка записей логин-пароль-ник;
 * При запуске и остановке ничего не происходит;
 * Поиск осуществляется перебором списка записей;
 *
 * Сервис авторизации в дальнейшем может быть доработан для использования с базой данных.
 */

public interface AuthService {
    void start();

    String getNickByLoginPass(String login, String pass);
    boolean registration(String login, String pass, String nick);
    boolean changeNick(String nick, String newNick);

    void stop();



}

