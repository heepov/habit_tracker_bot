package org.example;

import org.telegram.telegrambots.meta.api.objects.Message;

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
        // commands "/start" and "/info"
        if (messageText.equals(String.format(TextTemplates.START)) || messageText.equals(String.format(TextTemplates.INFO))) {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.INFO_TEXT));
            // command "/menu"
        } else if (messageText.equals(String.format(TextTemplates.MENU))) {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.MENU_PRINT));
            // command "/habit"
        } else if (messageText.equals(String.format(TextTemplates.HABIT))) {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.HABITS_COMMANDS_PRINT));
            // command "/add ..."
        } else if (messageText.startsWith(String.format(TextTemplates.ADD) + " ")) {
            menuHandler.handleAddHabit(chatId, messageText.substring(5));
            // command "/del ..."
        } else if (messageText.startsWith(String.format(TextTemplates.DELETE) + " ")) {
            menuHandler.handleDelHabit(chatId, messageText.substring(5));
            // command "/show"
        } else if (messageText.equals(String.format(TextTemplates.SHOW))) {
            menuHandler.handlePrintHabits(chatId);
            // command "/goal ..."
        } else if (messageText.startsWith(String.format(TextTemplates.GOAL) + " ")) {
            menuHandler.handleChangeHabitGoal(chatId, messageText.substring(6));
            // command "/rename ..."
        } else if (messageText.startsWith(String.format(TextTemplates.RENAME) + " ")) {
            menuHandler.handleRenameHabit(chatId, messageText.substring(8));
            // command "/check" or "/check DD.MM.YYYY"
        } else if (messageText.startsWith(String.format(TextTemplates.CHECK))) {
            menuHandler.handleCheckHabits(chatId, messageText);
            // Unknown command
        } else {
            messageSender.sendTextMessage(chatId, String.format(TextTemplates.ERROR_MENU_COMMAND));
        }
    }
}