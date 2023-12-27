package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.example.FileManager.loadDataFromFile;
import static org.example.FileManager.saveDataToFile;

public class MyBot extends TelegramLongPollingBot {
    public Map<Long, ArrayList<Habit>> habitsList;
    public LocalDate date;
    private final Properties properties = new Properties();
    private final IncomingMessageHandler incomingMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    public MyBot() {
        try (FileInputStream input = new FileInputStream("src/main/conf/bot.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        date = LocalDate.now();
        this.incomingMessageHandler = new IncomingMessageHandler(this);
        this.callbackQueryHandler = new CallbackQueryHandler(this);
        // Загрузка данных при запуске бота
        habitsList = loadDataFromFile(properties.getProperty("dataFilePath"));
    }

    public static void main(String[] args) {
        MyBot bot = new MyBot();
        bot.botConnect();
    }
    private void botConnect() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            incomingMessageHandler.processIncomingMessage(update.getMessage());
        }else if ((update.hasCallbackQuery())) {
            callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());
        }else {
            System.out.println("Something else");
        }
        // Сохранение данных при получении сообщений/обновлений
        saveDataToFile(properties.getProperty("dataFilePath"), habitsList);
    }
    @Override
    public String getBotUsername() {
        return properties.getProperty("botUserName");
    }

    @Override
    public String getBotToken() {
        return properties.getProperty("botToken");
    }
}
