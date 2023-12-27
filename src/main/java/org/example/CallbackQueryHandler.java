package org.example;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import java.util.ArrayList;
public class CallbackQueryHandler {
    private final MyBot bot;
    private final MessageSender messageSender;
    public CallbackQueryHandler(MyBot bot) {
        this.bot = bot;
        this.messageSender = new MessageSender(bot);
    }
    public void processCallbackQuery(CallbackQuery callbackQuery){
        // get all user's habits for chatId
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(callbackQuery.getMessage().getChatId(), new ArrayList<>());
        // check and write all users response for habits check list
        for (Habit habit : userHabits) {
            if (habit.getHabitName().equals(callbackQuery.getMessage().getText())){
                // if user tap YES button
                if(callbackQuery.getData().startsWith("t")){
                    messageSender.editMessageText(callbackQuery.getMessage(),
                            callbackQuery.getMessage().getText() + " " + String.format(TextTemplates.YES_DESCRIBE));
                    habit.setProgressHistoryByDate(bot.date,true);
                // if user tap NO button
                }else if (callbackQuery.getData().startsWith("f")){
                    messageSender.editMessageText(callbackQuery.getMessage(),
                            callbackQuery.getMessage().getText() + " " + String.format(TextTemplates.NO_DESCRIBE));
                    habit.setProgressHistoryByDate(bot.date,false);
                }
            }
        }
    }
}
