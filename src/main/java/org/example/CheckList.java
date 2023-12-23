package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CheckList {
    private final MyBot bot;

    public CheckList(MyBot bot) {
        this.bot = bot;
    }
    public static SendMessage createCheckList(Habit habit) {
        SendMessage message = new SendMessage();
        message.setText(habit.getHabitName());
        message.setReplyMarkup(createInlineKeyboard(habit.getHabitId()));
        return message;
    }
    // Метод для создания Inline Keyboard
    private static InlineKeyboardMarkup createInlineKeyboard(int habitId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // Создаем кнопки "Yes" и "No"
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("Done").callbackData("t"+habitId).build());
        row.add(InlineKeyboardButton.builder().text("Neah").callbackData("f"+habitId).build());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
