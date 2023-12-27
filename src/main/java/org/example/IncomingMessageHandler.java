package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IncomingMessageHandler {
    private final MyBot bot;
    private final MessageSender messageSender;

    public IncomingMessageHandler(MyBot bot) {
        this.bot = bot;
        this.messageSender = new MessageSender(bot);
    }
    public void processIncomingMessage(Message message) {
        long chatId = message.getChatId();
        String messageText = message.getText().toLowerCase().trim();

        MenuHandler menuHandler = new MenuHandler(bot);
        if (messageText.equals(String.format(TextTemplates.START)) || messageText.equals(String.format(TextTemplates.INFO))) {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.INFO_TEXT));
        } else if (messageText.equals(String.format(TextTemplates.MENU))) {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.MENU_PRINT));
        } else if (messageText.equals(String.format(TextTemplates.HABIT))) {
            messageSender.sendTextMessage(chatId, menuHandler.handlePrintHabits(chatId));
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.HABITS_COMMANDS_PRINT));
        } else if (messageText.startsWith(String.format(TextTemplates.ADD) + " ")) {
            messageSender.sendTextMessage(chatId, menuHandler.handleAddHabit(chatId, messageText.substring(5)));
        } else if (messageText.startsWith(String.format(TextTemplates.DELETE) + " ")) {
            messageSender.sendTextMessage(chatId, menuHandler.handleDelHabit(chatId, messageText.substring(5)));
        }else if (messageText.equals(String.format(TextTemplates.SHOW))) {
            messageSender.sendTextMessage(chatId, menuHandler.handlePrintHabits(chatId));
        } else if (messageText.startsWith(String.format(TextTemplates.GOAL) + " ")) {
            messageSender.sendTextMessage(chatId, menuHandler.handleChangeHabitGoal(chatId, messageText.substring(6)));
        }else if (messageText.startsWith(String.format(TextTemplates.RENAME) + " ")) {
            messageSender.sendTextMessage(chatId, menuHandler.handleRenameHabit(chatId, messageText.substring(8)));
        }else if (messageText.startsWith(String.format(TextTemplates.CHECK))) {
            ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
            if (userHabits.isEmpty()) {
                messageSender.sendTextMessage(chatId, String.format(TextTemplates.EMPTY_HABIT_LIST));
            }else{
                if (messageText.startsWith("/check ")) {
                    LocalDate date = getDateFromMessage(messageText.substring(7));
                    if (date != null){
                        messageSender.sendTextMessage(chatId,  String.format(TextTemplates.DATE_HABITS_LIST) + " " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ":");
                        this.date = date;
                    }else {
                        messageSender.sendTextMessage(chatId, String.format(TextTemplates.ERROR_WRONG_DATE));
                        return;
                    }
                }else {
                    messageSender.sendTextMessage(chatId, String.format(TextTemplates.TODAY_HABITS_LIST));
                    this.date = LocalDate.now();
                }
                List<SendMessage> habitListMessages = new ArrayList<>();
                for (Habit habit : userHabits) {
                    SendMessage habitMessage = CheckList.createCheckList(habit);
                    habitListMessages.add(habitMessage);
                }
                for (SendMessage habitMessage : habitListMessages) {
                    messageSender.sendAllTextMessage(chatId, habitMessage.getText(), (InlineKeyboardMarkup) habitMessage.getReplyMarkup());
                }

            }
        } else {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.ERROR_MENU_COMMAND));
        }
    }
}