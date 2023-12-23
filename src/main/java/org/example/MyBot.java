package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            processIncomingMessage(update.getMessage());
        }else if ((update.hasCallbackQuery())) {
            processCallbackQuery(update.getCallbackQuery());
        }else {
            System.out.println("Something else");
        }
    }
    public Map<Long, ArrayList<Habit>> habitsList = new HashMap<>();
    private LocalDate date;
    private  void processCallbackQuery(CallbackQuery callbackQuery){
//        System.out.println(callbackQuery.getData());
//        System.out.println(callbackQuery.getMessage().getText());
//        System.out.println(callbackQuery.getMessage().getMessageId());

        ArrayList<Habit> userHabits = habitsList.getOrDefault(callbackQuery.getMessage().getChatId(), new ArrayList<>());
        for (Habit habit : userHabits) {
           if (habit.getHabitName().equals(callbackQuery.getMessage().getText())){
               if(callbackQuery.getData().startsWith("t")){
                   editMessageText(callbackQuery.getMessage(),
                           callbackQuery.getMessage().getText() + " is done! You so cool!");
                   habit.setTodayCompleteHistory(date,true);
               }else if (callbackQuery.getData().startsWith("f")){
                   editMessageText(callbackQuery.getMessage(),
                           callbackQuery.getMessage().getText() + " is done! You so cool!");
                   habit.setTodayCompleteHistory(date,false);
               }
           }
        }
    }


    private void processIncomingMessage(Message message) {
        long chatId = message.getChatId();
        String messageText = message.getText();

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
                if (messageText.startsWith("/check ")) {
                    LocalDate date = getDateFromMessage(messageText.substring(7));
                    if (date != null){
                        sendTextMessage(chatId, "Habit check list from date " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ":");
                        this.date = date;
                    }else {
                        sendTextMessage(chatId, "Wrong date!");
                        return;
                    }
                }else {
                    sendTextMessage(chatId, "Today habit check list:");
                    this.date = LocalDate.now();
                }
                List<SendMessage> habitListMessages = new ArrayList<>();
                for (Habit habit : userHabits) {
                    SendMessage habitMessage = CheckList.createCheckList(habit);
                    habitListMessages.add(habitMessage);
                }
                for (SendMessage habitMessage : habitListMessages) {
                    sendAllTextMessage(chatId, habitMessage.getText(), (InlineKeyboardMarkup) habitMessage.getReplyMarkup());
                }

            }
        } else {
             sendTextMessage(chatId, "Incorrect message use /menu command");
        }
    }
    private LocalDate getDateFromMessage(String textDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            LocalDate date = LocalDate.parse(textDate, formatter);
            return date;
        } catch (Exception e) {
            return null;
        }
    }
    private void editMessageText(Message message, String text){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setText(text);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
//    private void updateHabitsList(long chatId){
//        ArrayList<Habit> userHabits = habitsList.getOrDefault(chatId, new ArrayList<>());
//        if (userHabits.isEmpty()) System.out.println("User has empty habit list");
//        for (Habit habit : userHabits) {
//            habit.resetCompletionForNewMonth();
//        }
//        System.out.println("User's habit list has been update");
//    }
}
