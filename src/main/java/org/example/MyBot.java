package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyBot extends TelegramLongPollingBot {

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
    public Map<Long, String> userStates = new HashMap<>();
    public Map<Long, ArrayList<String>> habitsList = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        MenuHandler menuHandler = new MenuHandler(this);
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            // Проверяем состояние пользователя
            String currentState = userStates.getOrDefault(chatId, "START");
            System.out.println("Received message: " + messageText);
            System.out.println(userStates.get(chatId));
            switch (currentState) {
                case "START":
                    // Обработка обычных сообщений
                    handleStartState(chatId, messageText);
                    break;
                case "MENU":
                    // Обработка состояния меню
                    menuHandler.handleMenu(chatId);
                    break;
                case "ADD_HABIT":
                    // Обработка состояния добавления привычки
                    menuHandler.handleAddHabit(chatId, messageText);
                    break;
                default:
                    userStates.put(chatId, "START");
                    break;

                // Добавьте дополнительные состояния по мере необходимости
            }
        }else if ((update.hasCallbackQuery())){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            long chatId = callbackQuery.getMessage().getChatId();
            int messageId = callbackQuery.getMessage().getMessageId();

            switch (data){
                case "add_habit":
                    editMessageText(chatId,messageId,"Write your habit:");
                    userStates.put(chatId, "ADD_HABIT");
                    break;
                case "print_habits":
                    if(habitsList.getOrDefault(chatId, new ArrayList<>()).isEmpty()){
                        editMessageText(chatId, messageId, "You dont have any habits");
                    }else {
                        editMessageText(chatId, messageId, "These all your habits:");
                        menuHandler.handlePrintHabits(chatId);
                    }
                    userStates.put(chatId, "START");
                    break;
            }
        }
    }
    private void handleStartState(long chatId, String messageText) {
        MenuHandler menuHandler = new MenuHandler(this);
        // Обработка обычных сообщений в начальном состоянии
        if (messageText.equals("/menu")) {
            menuHandler.handleMenu(chatId);
            userStates.put(chatId, "MENU");
        } else if (messageText.equals("/start")) {
            sendTextMessage(chatId, "In this bot you can track your habits. Set goals and review your progress. To get started send /menu command!");
        } else if (messageText.substring(0,5).equals("/add ")) {
            menuHandler.handleAddHabit(chatId, messageText.substring(5));
        } else if (messageText.equals("/print")) {
            menuHandler.handlePrintHabits(chatId);
        } else {
            sendTextMessage(chatId, "Incorrect message use /menu command");
            userStates.put(chatId, "START");
        }
    }
    private void sendTextMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage(long chatId, SendMessage sendMessage) {
        sendMessage.setChatId(chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void editMessageText(long chatId, int messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // Верните имя вашего бота
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/conf/bot.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("botUserName");
    }

    @Override
    public String getBotToken() {
        // Верните токен, который вы получили от BotFather
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("src/main/conf/bot.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("botToken");
    }
}
