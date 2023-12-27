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
    // Chat ID and Habit List (unique list for one user)
    public Map<Long, ArrayList<Habit>> habitsList;
    public LocalDate date;
    // file with "bot Username", "bot Token" and "data File Path"
    private final Properties properties = new Properties();
    private final IncomingMessageHandler incomingMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    // initial all
    public MyBot() {
        // load properties file
        try (FileInputStream input = new FileInputStream("src/main/conf/bot.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        date = LocalDate.now();
        this.incomingMessageHandler = new IncomingMessageHandler(this);
        this.callbackQueryHandler = new CallbackQueryHandler(this);
        // load data file
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
    // actually main function
    @Override
    public void onUpdateReceived(Update update) {
        // for user send message
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getChatId());
            incomingMessageHandler.processIncomingMessage(update.getMessage());
        // for user tap the button or something like this
        }else if ((update.hasCallbackQuery())) {
            callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());
        }else {
        //other user's actions
            System.out.println("Something else");
        }
        // save habitsList to data file
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
