package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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

    @Override
    public void onUpdateReceived(Update update) {
        MenuHandler menuHandler = new MenuHandler(this);
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
//            updateHabitsList(chatId);
            commandAnalyser(chatId, messageText);
        }else if ((update.hasCallbackQuery())) {
            System.out.println(update.getCallbackQuery());
            System.out.println(update.getCallbackQuery().getData());
            System.out.println(update.getCallbackQuery().getMessage().getText());
            System.out.println(update.getCallbackQuery().getMessage().getMessageId());
            System.out.println(update.getCallbackQuery().getChatInstance());
        }else {
            System.out.println("Something else");
        }
    }

    public Map<Long, ArrayList<Habit>> habitsList = new HashMap<>();
    private void updateHabitsList(long chatId){
        ArrayList<Habit> userHabits = habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) System.out.println("User has empty habit list");
        for (Habit habit : userHabits) {
            habit.resetCompletionForNewMonth();
        }
        System.out.println("User's habit list has been update");
    }
    private void commandAnalyser(long chatId, String messageText) {
        MenuHandler menuHandler = new MenuHandler(this);
        if (messageText.equals("/start") || messageText.equals("/info") ) {
            sendTextMessage(chatId, "In this bot you can track your habits. Set goals and review your progress. To get started send /menu command!");
        } else if (messageText.equals("/menu")) {
            StringBuilder menu = new StringBuilder();
            menu.append("/info for information\n");
            menu.append("\n");
            menu.append("/habit to add, delete or change your habits\n");
            menu.append("\n");
            menu.append("/show to show all your habits\n");
            menu.append("\n");
            menu.append("/check to check your habit\n");
            menu.append("\n");
            menu.append("/stat to look your progress\n");
            sendTextMessage(chatId, menu.toString());
        } else if (messageText.equals("/habit")) {
            sendTextMessage(chatId, menuHandler.handlePrintHabits(chatId));
            StringBuilder habit = new StringBuilder();
            habit.append("/add 'habit name' : 'monthly goal'\n<i>to add new habit and set monthly goal (like '/add meditation : 24')</i>\n");
            habit.append("\n");
            habit.append("/del 'habit name'\n<i>to delete your habit (like '/del meditation')</i>\n");
            habit.append("\n");
            habit.append("/goal 'habit name' : 'new monthly goal'\n<i>to change monthly goal for your habits (like '/add meditation : 24')</i>\n");
            habit.append("\n");
            habit.append("/rename 'old habit name' : 'new habit name'\n<i>to rename your habit (like '/rename meditation : drink beer')</i>\n");
            sendTextMessage(chatId, habit.toString());
        } else if (messageText.startsWith("/add ")) {
            sendTextMessage(chatId, menuHandler.handleAddHabit(chatId, messageText.substring(5)));
        } else if (messageText.startsWith("/del ")) {
            sendTextMessage(chatId, menuHandler.handleDelHabit(chatId, messageText.substring(5)));
        }else if (messageText.equals("/show")) {
            sendTextMessage(chatId, menuHandler.handlePrintHabits(chatId));
        } else if (messageText.startsWith("/goal ")) {
            sendTextMessage(chatId, menuHandler.handleChangeHabitGoal(chatId, messageText.substring(6)));
        }else if (messageText.startsWith("/rename ")) {
            sendTextMessage(chatId, menuHandler.handleRenameHabit(chatId, messageText.substring(8)));
        }else if (messageText.startsWith("/check")) {
            ArrayList<Habit> userHabits = habitsList.getOrDefault(chatId, new ArrayList<>());
            if (userHabits.isEmpty()) {
                sendTextMessage(chatId, "You don't have any habits.");
            }else{
                List<SendMessage> habitListMessages = new ArrayList<>();
                for (Habit habit : userHabits) {
                    SendMessage message = CheckList.createCheckList(habit);
                    habitListMessages.add(message);
                }
                for (SendMessage message : habitListMessages) {
                    sendAllTextMessage(chatId, message.getText(), (InlineKeyboardMarkup) message.getReplyMarkup());
                }
            }
        } else {
             sendTextMessage(chatId, "Incorrect message use /menu command");
        }
    }

    private void sendTextMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableHtml(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAllTextMessage(Long chatId, String text, InlineKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage()
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
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
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("src/main/conf/bot.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("botToken");
    }
}
