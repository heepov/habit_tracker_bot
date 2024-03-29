package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CheckList {
    public static SendMessage createCheckList(Habit habit) {
        SendMessage message = new SendMessage();
        // first row is habit name
        message.setText(habit.getHabitName());
        //second row is two buttons "yes" and "no"
        message.setReplyMarkup(createInlineKeyboard(habit.getHabitId()));
        return message;
    }
    // Метод для создания Inline Keyboard
    private static InlineKeyboardMarkup createInlineKeyboard(int habitId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // Create buttons "yes" and "no"
        List<InlineKeyboardButton> row = new ArrayList<>();
        // Add callbackData to recognize which button taped (t - YES, f - NO)
        row.add(InlineKeyboardButton.builder().text(String.format(TextTemplates.YES)).callbackData("t"+habitId).build());
        row.add(InlineKeyboardButton.builder().text(String.format(TextTemplates.NO)).callbackData("f"+habitId).build());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
