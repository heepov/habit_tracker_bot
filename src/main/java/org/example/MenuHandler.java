package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

// Класс меню (MenuHandler)
public class MenuHandler {

    private final MyBot bot;

    public MenuHandler(MyBot bot) {
        this.bot = bot;
    }
    public void handleAddHabit(long chatId, String messageText) {
        if(messageText.equals("exit")){
            bot.userStates.put(chatId, "MENU");
            handleMenu(chatId);
            return;
        }
        ArrayList<String> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        userHabits.add(messageText);
        bot.habitsList.put(chatId, userHabits);
        sendTextMessage(chatId, "You added \"" + messageText + "\"");
        // Установка состояния "Добавление привычки"
        bot.userStates.put(chatId, "MENU");
    }

    public void handlePrintHabits(long chatId) {
        ArrayList<String> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        StringBuilder habitsText = new StringBuilder();
        for (String habit : userHabits) {
            habitsText.append("- ").append(habit).append("\n");
        }
        sendTextMessage(chatId, habitsText.toString());
        // Установка состояния "Добавление привычки"
        bot.userStates.put(chatId, "MENU");
    }

    public void handleMenu(long chatId) {
        try {
            bot.execute(createAddHabitKeyboard(chatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // Установка состояния "Добавление привычки"
        bot.userStates.put(chatId, "MENU");
    }

    private SendMessage createAddHabitKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Menu");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Add habit");
        inlineKeyboardButton1.setCallbackData("add_habit");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Print all habits");
        inlineKeyboardButton2.setCallbackData("print_habits");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row1.add(inlineKeyboardButton1);
        row2.add(inlineKeyboardButton2);
        keyboard.add(row1);
        keyboard.add(row2);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    private void sendTextMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
