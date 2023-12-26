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

import static org.example.FileManager.loadDataFromFile;
import static org.example.FileManager.saveDataToFile;

public class MyBot extends TelegramLongPollingBot {
    public Map<Long, ArrayList<Habit>> habitsList = new HashMap<>();
    private LocalDate date;
    private Properties properties = new Properties();
    public static void main(String[] args) {
        MyBot bot = new MyBot();
        bot.botConnect();
    }
    private void botConnect() {
        try {
            setProperties();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            // Загрузка данных при запуске бота
            habitsList = loadDataFromFile(properties.getProperty("dataFilePath"));
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
        // Сохранение данных при получении сообщений/обновлений
        saveDataToFile(properties.getProperty("dataFilePath"), habitsList);
    }
    private  void processCallbackQuery(CallbackQuery callbackQuery){
        ArrayList<Habit> userHabits = habitsList.getOrDefault(callbackQuery.getMessage().getChatId(), new ArrayList<>());
        for (Habit habit : userHabits) {
           if (habit.getHabitName().equals(callbackQuery.getMessage().getText())){
               if(callbackQuery.getData().startsWith("t")){
                   editMessageText(callbackQuery.getMessage(),
                           callbackQuery.getMessage().getText() + " " + String.format(TextTemplates.YES_DESCRIBE));
                   habit.setTodayCompleteHistory(date,true);
               }else if (callbackQuery.getData().startsWith("f")){
                   editMessageText(callbackQuery.getMessage(),
                           callbackQuery.getMessage().getText() + " " + String.format(TextTemplates.NO_DESCRIBE));
                   habit.setTodayCompleteHistory(date,false);
               }
           }
        }
    }


    private void processIncomingMessage(Message message) {
        long chatId = message.getChatId();
        String messageText = message.getText().toLowerCase().trim();

        MenuHandler menuHandler = new MenuHandler(this);
        if (messageText.equals(String.format(TextTemplates.START)) || messageText.equals(String.format(TextTemplates.INFO))) {
            sendTextMessage(chatId, String.format(TextTemplates.INFO_TEXT));
        } else if (messageText.equals(String.format(TextTemplates.MENU))) {
            sendTextMessage(chatId, String.format(TextTemplates.MENU_PRINT));
        } else if (messageText.equals(String.format(TextTemplates.HABIT))) {
            sendTextMessage(chatId, menuHandler.handlePrintHabits(chatId));
            sendTextMessage(chatId, String.format(TextTemplates.HABITS_COMMANDS_PRINT));
        } else if (messageText.startsWith(String.format(TextTemplates.ADD) + " ")) {
            sendTextMessage(chatId, menuHandler.handleAddHabit(chatId, messageText.substring(5)));
        } else if (messageText.startsWith(String.format(TextTemplates.DELETE) + " ")) {
            sendTextMessage(chatId, menuHandler.handleDelHabit(chatId, messageText.substring(5)));
        }else if (messageText.equals(String.format(TextTemplates.SHOW))) {
            sendTextMessage(chatId, menuHandler.handlePrintHabits(chatId));
        } else if (messageText.startsWith(String.format(TextTemplates.GOAL) + " ")) {
            sendTextMessage(chatId, menuHandler.handleChangeHabitGoal(chatId, messageText.substring(6)));
        }else if (messageText.startsWith(String.format(TextTemplates.RENAME) + " ")) {
            sendTextMessage(chatId, menuHandler.handleRenameHabit(chatId, messageText.substring(8)));
        }else if (messageText.startsWith(String.format(TextTemplates.CHECK))) {
            ArrayList<Habit> userHabits = habitsList.getOrDefault(chatId, new ArrayList<>());
            if (userHabits.isEmpty()) {
                sendTextMessage(chatId, String.format(TextTemplates.EMPTY_HABIT_LIST));
            }else{
                if (messageText.startsWith("/check ")) {
                    LocalDate date = getDateFromMessage(messageText.substring(7));
                    if (date != null){
                        sendTextMessage(chatId,  String.format(TextTemplates.DATE_HABITS_LIST) + " " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ":");
                        this.date = date;
                    }else {
                        sendTextMessage(chatId, String.format(TextTemplates.ERROR_WRONG_DATE));
                        return;
                    }
                }else {
                    sendTextMessage(chatId, String.format(TextTemplates.TODAY_HABITS_LIST));
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
             sendTextMessage(chatId, String.format(TextTemplates.ERROR_MENU_COMMAND));
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
    private void setProperties(){
        try {
            properties.load(new FileInputStream("src/main/conf/bot.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
