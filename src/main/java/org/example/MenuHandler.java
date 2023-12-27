package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuHandler {
    private final MessageSender messageSender;
    private final MyBot bot;

    public MenuHandler(MyBot bot) {
        this.bot = bot;
        this.messageSender = new MessageSender(bot);
    }
    public void handleAddHabit(long chatId, String messageText) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (messageText.contains(":")){
            String habitName = getHabitNameFromMessage(messageText);
            if (habitName != null) {
                Habit habit = findHabit(userHabits, habitName);
                if (habit == null) {
                    Integer habitGoal = getHabitGoalFromMessage(messageText);
                    if (habitGoal != null) {
                        Habit newHabit = new Habit(habitName, habitGoal);
                        userHabits.add(newHabit);
                        bot.habitsList.put(chatId, userHabits);
                        messageSender.sendTextMessage(chatId,
                                String.format(TextTemplates.ADD_NEW_HABIT, newHabit.getHabitName(), newHabit.getGoalPerMonth()));
                    } else {
                        messageSender.sendTextMessage(chatId,
                                String.format(TextTemplates.ERROR_IDENTIFY, "goal", "/add habit_name : habit_goal"));
                    }
                } else {
                    messageSender.sendTextMessage(chatId,
                            String.format(TextTemplates.ERROR_ADD_HABIT_DUPLICATED, habitName));
                }
            } else {
                messageSender.sendTextMessage(chatId,
                        String.format(TextTemplates.ERROR_IDENTIFY, "habit name", "/add habit_name : habit_goal"));
            }
        }else {
            Habit habit = findHabit(userHabits, messageText);
            if (habit == null) {
                Habit newHabit = new Habit(messageText);
                userHabits.add(newHabit);
                bot.habitsList.put(chatId, userHabits);
                messageSender.sendTextMessage(chatId,
                        String.format(TextTemplates.ADD_NEW_HABIT, newHabit.getHabitName(), newHabit.getGoalPerMonth()));
            }else {
                messageSender.sendTextMessage(chatId,
                        String.format(TextTemplates.ERROR_ADD_HABIT_DUPLICATED, messageText));
            }
        }
    }
    public void handleDelHabit(long chatId, String messageText) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if(checkListEmpty(chatId,userHabits)) return;

        Iterator<Habit> iterator = userHabits.iterator();
        while (iterator.hasNext()) {
            Habit habit = iterator.next();
            if (habit.getHabitName().equals(messageText)) {
                iterator.remove();
                bot.habitsList.put(chatId, userHabits);
                messageSender.sendTextMessage(chatId,
                        String.format(TextTemplates.DELETE_HABIT, messageText));
                return;
            }
        }
        messageSender.sendTextMessage(chatId,
                String.format(TextTemplates.UNKNOWN_HABIT, messageText));
    }
    public void handleChangeHabitGoal(long chatId, String messageText) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if(checkListEmpty(chatId,userHabits)) return;

        String habitName = getHabitNameFromMessage(messageText);
        if (habitName != null) {
            Habit habit = findHabit(userHabits, habitName);
            if (habit != null) {
                Integer newHabitGoal = getHabitGoalFromMessage(messageText);
                if (newHabitGoal != null) {
                    habit.setGoalPerMonth(newHabitGoal);
                    bot.habitsList.put(chatId, userHabits);
                    messageSender.sendTextMessage(chatId,
                            String.format(TextTemplates.CHANGE_HABIT_GOAL, habit.getHabitName(), habit.getGoalPerMonth()));
                } else {
                    messageSender.sendTextMessage(chatId,
                            String.format(TextTemplates.ERROR_IDENTIFY, "goal", "/goal habit_name : new_habit_goal"));
                }
            } else {
                messageSender.sendTextMessage(chatId,
                        String.format(TextTemplates.UNKNOWN_HABIT, habitName));
            }
        } else {
            messageSender.sendTextMessage(chatId,
                    String.format(TextTemplates.ERROR_IDENTIFY, "habit name", "/goal habit_name : new_habit_goal"));
        }
    }

    public void handleRenameHabit(long chatId, String messageText) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if(checkListEmpty(chatId,userHabits)) return;

        String habitName = getHabitNameFromMessage(messageText);
        if (habitName != null) {
            Habit habit = findHabit(userHabits, habitName);
            if (habit != null) {
                String newHabitName = getNewHabitNameFromMessage(messageText);
                if (newHabitName != null) {
                    Habit otherHabit = findHabit(userHabits, newHabitName);
                    if (otherHabit == null) {
                        habit.setHabitName(newHabitName);
                        bot.habitsList.put(chatId, userHabits);
                        messageSender.sendTextMessage(chatId,
                                String.format(TextTemplates.CHANGE_HABIT_NAME, habitName, habit.getHabitName()));
                    }else {
                        messageSender.sendTextMessage(chatId,
                                String.format(TextTemplates.ERROR_RENAME_HABIT_DUPLICATED, newHabitName));
                    }
                }else {
                    messageSender.sendTextMessage(chatId,
                            String.format(TextTemplates.ERROR_IDENTIFY, "new habit name", "/rename habit_name : new_habit_name"));
                }
            } else {
                messageSender.sendTextMessage(chatId,
                        String.format(TextTemplates.UNKNOWN_HABIT, habitName));
            }
        } else {
            messageSender.sendTextMessage(chatId,
                    String.format(TextTemplates.ERROR_IDENTIFY, "habit name", "/rename habit_name : new_habit_name"));
        }
    }

    public void handlePrintHabits(long chatId) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if(checkListEmpty(chatId,userHabits)) return;

        messageSender.sendTextMessage(chatId, TextTemplates.ALL_HABITS);
        StringBuilder habitsText = new StringBuilder();
        for (Habit habit : userHabits) {
            habitsText
                    .append("<b>" + capitalizeFirstLetter(habit.getHabitName()) + "</b>" + "\n")
                    .append("<code>" + progressBarCreate(habit) + "</code> ")
                    .append(habit.countCompletedGoalsForCurrentMonth() + " / ")
                    .append(habit.getGoalPerMonth() + "\n\n");
        }
        messageSender.sendTextMessage(chatId, habitsText.toString());
    }

    public void handleCheckHabits(long chatId, String messageText){
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if(checkListEmpty(chatId,userHabits)) return;

        if (messageText.startsWith(TextTemplates.CHECK + " ")) {
            getDateFromMessage(messageText.substring(7));
            if (bot.date != null) {
                messageSender.sendTextMessage(chatId, TextTemplates.DATE_HABITS_LIST + " " +
                        bot.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ":");
            } else {
                messageSender.sendTextMessage(chatId, TextTemplates.ERROR_WRONG_DATE);
                return;
            }
        } else {
            messageSender.sendTextMessage(chatId, TextTemplates.TODAY_HABITS_LIST);
            bot.date = LocalDate.now();
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


    private StringBuilder progressBarCreate(Habit habit){
        StringBuilder progressBar = new StringBuilder();
        // Получаем текущий год и месяц
        YearMonth currentYearMonth = YearMonth.now();
        // Обходим все дни в текущем месяце
        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            // Создаем LocalDate для каждого дня в текущем месяце
            LocalDate currentDate = currentYearMonth.atDay(day);
            if (habit.getCompleteHistory().containsKey(currentDate)){
                if(habit.getCompleteHistory().get(currentDate)){
                    progressBar.append("V");
                }else {
                    progressBar.append("X");
                }
            }else {
                progressBar.append("•");
            }
        }
        return progressBar;
    }
    private boolean checkListEmpty(long chatId, ArrayList<Habit> userHabits){
        if (userHabits.isEmpty()) {
            messageSender.sendTextMessage(chatId, TextTemplates.EMPTY_HABIT_LIST);
            return true;
        }else
            return false;
    }
    private Integer getHabitGoalFromMessage(String message){
        Pattern patternGoal = Pattern.compile(":\\s*(\\d+)");
        Matcher matcherGoal = patternGoal.matcher(message);
        if (matcherGoal.find()) {
            return Integer.parseInt(matcherGoal.group(1));
        }else
            return null;
    }
    private String getNewHabitNameFromMessage(String message){
        Pattern patternNewName = Pattern.compile(":\\s*(.*)");
        Matcher matcherNewName = patternNewName.matcher(message);
        if (matcherNewName.find()) {
            return matcherNewName.group(1).trim();
        }else
            return null;
    }
    private Habit findHabit(ArrayList<Habit> userHabits, String habitName) {
        return userHabits.stream()
            .filter(habit -> habit.getHabitName().equals(habitName))
            .findFirst()
            .orElse(null);
    }
    private String getHabitNameFromMessage(String message){
        Pattern patternHabitName = Pattern.compile("^\\s*(.*?):");
        Matcher matcherHabitName = patternHabitName.matcher(message);
        if (matcherHabitName.find()) {
            return matcherHabitName.group(1).trim();
        }else
            return null;
    }
    public String capitalizeFirstLetter(String habitName) {
        if (habitName == null || habitName.isEmpty()) {
            return habitName;
        }
        return habitName.substring(0, 1).toUpperCase() + habitName.substring(1);
    }
    private void getDateFromMessage(String textDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            bot.date = LocalDate.parse(textDate, formatter);
        } catch (Exception e) {
            bot.date = null;
        }
    }
}
