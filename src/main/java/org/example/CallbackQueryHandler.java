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

    public   void processCallbackQuery(CallbackQuery callbackQuery){
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(callbackQuery.getMessage().getChatId(), new ArrayList<>());
        for (Habit habit : userHabits) {
            if (habit.getHabitName().equals(callbackQuery.getMessage().getText())){
                if(callbackQuery.getData().startsWith("t")){
                    messageSender.editMessageText(callbackQuery.getMessage(),
                            callbackQuery.getMessage().getText() + " " + String.format(TextTemplates.YES_DESCRIBE));
                    habit.setTodayCompleteHistory(bot.date,true);
                }else if (callbackQuery.getData().startsWith("f")){
                    messageSender.editMessageText(callbackQuery.getMessage(),
                            callbackQuery.getMessage().getText() + " " + String.format(TextTemplates.NO_DESCRIBE));
                    habit.setTodayCompleteHistory(bot.date,false);
                }
            }
        }
    }
}
