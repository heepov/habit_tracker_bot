package org.example;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuHandler {

    private final MyBot bot;

    public MenuHandler(MyBot bot) {
        this.bot = bot;
    }
    public String handleAddHabit(long chatId, String messageText) {
        messageText = messageText.toLowerCase().trim();
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
                        return String.format(TextTemplates.ADD_NEW_HABIT, newHabit.getHabitName(), newHabit.getGoalPerMonth());
                    } else {
                        return String.format(TextTemplates.ERROR_IDENTIFY, "goal", "/add habit_name : habit_goal");
                    }
                } else {
                    return String.format(TextTemplates.ERROR_HABIT_DUPLICATED, habitName);
                }
            } else {
                return String.format(TextTemplates.ERROR_IDENTIFY, "habit name", "/add habit_name : habit_goal");
            }
        }else {
            Habit habit = findHabit(userHabits, messageText);
            if (habit == null) {
                Habit newHabit = new Habit(messageText);
                userHabits.add(newHabit);
                bot.habitsList.put(chatId, userHabits);
                return String.format(TextTemplates.ADD_NEW_HABIT, newHabit.getHabitName(), newHabit.getGoalPerMonth());
            }else {
                return String.format(TextTemplates.ERROR_HABIT_DUPLICATED, messageText);
            }
        }
    }
    private String getHabitNameFromMessage(String message){
        message = message.toLowerCase().trim();
        Pattern patternHabitName = Pattern.compile("^\\s*(.*?):");
        Matcher matcherHabitName = patternHabitName.matcher(message);
        if (matcherHabitName.find()) {
            return matcherHabitName.group(1).trim();
        }else
            return null;
    }
    private Integer getHabitGoalFromMessage(String message){
        message = message.toLowerCase().trim();
        Pattern patternGoal = Pattern.compile(":\\s*(\\d+)");
        Matcher matcherGoal = patternGoal.matcher(message);
        if (matcherGoal.find()) {
            return Integer.parseInt(matcherGoal.group(1));
        }else
            return null;
    }
    private String getNewHabitNameFromMessage(String message){
        message = message.toLowerCase().trim();
        Pattern patternNewName = Pattern.compile(":\\s*(.*)");
        Matcher matcherNewName = patternNewName.matcher(message);
        if (matcherNewName.find()) {
            return matcherNewName.group(1).trim();
        }else
            return null;
    }
    private Habit findHabit(ArrayList<Habit> userHabits, String habitName) {
        for (Habit habit : userHabits) {
            if (habit.getHabitName().equals(habitName)) {
                return habit;
            }
        }
        return null;
    }

    public String handleDelHabit(long chatId, String messageText) {
        messageText = messageText.toLowerCase().trim();
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty())
            return String.format(TextTemplates.EMPTY_HABIT_LIST);

        Iterator<Habit> iterator = userHabits.iterator();
        while (iterator.hasNext()) {
            Habit habit = iterator.next();
            if (habit.getHabitName().equals(messageText)) {
                iterator.remove();
                bot.habitsList.put(chatId, userHabits);
                return String.format(TextTemplates.DELETE_HABIT, messageText);
            }
        }
        return String.format(TextTemplates.UNKNOWN_HABIT, messageText);
    }
    public String handleChangeHabitGoal(long chatId, String messageText) {
        messageText = messageText.toLowerCase().trim();
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return String.format(TextTemplates.EMPTY_HABIT_LIST);

        String habitName = getHabitNameFromMessage(messageText);
        if (habitName != null) {
            Habit habit = findHabit(userHabits, habitName);
            if (habit != null) {
                Integer newHabitGoal = getHabitGoalFromMessage(messageText);
                if (newHabitGoal != null) {
                    habit.setGoalPerMonth(newHabitGoal);
                    bot.habitsList.put(chatId, userHabits);
                    return String.format(TextTemplates.CHANGE_HABIT_GOAL, habit.getHabitName(), habit.getGoalPerMonth());
                } else {
                    return String.format(TextTemplates.ERROR_IDENTIFY, "goal", "/goal habit_name : new_habit_goal");
                }
            } else {
                return String.format(TextTemplates.UNKNOWN_HABIT, habitName);
            }
        } else {
            return String.format(TextTemplates.ERROR_IDENTIFY, "habit name", "/goal habit_name : new_habit_goal");
        }
    }

    public String handleRenameHabit(long chatId, String messageText) {
        messageText = messageText.toLowerCase().trim();
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return String.format(TextTemplates.EMPTY_HABIT_LIST);

        String habitName = getHabitNameFromMessage(messageText);
        if (habitName != null) {
            Habit habit = findHabit(userHabits, habitName);
            if (habit != null) {
                String newHabitName = getNewHabitNameFromMessage(messageText);
                if (newHabitName != null) {
                    habit.setHabitName(newHabitName);
                    bot.habitsList.put(chatId, userHabits);
                    return String.format(TextTemplates.CHANGE_HABIT_NAME, habitName, habit.getHabitName());
                }else {
                    return String.format(TextTemplates.ERROR_IDENTIFY, "new habit name", "/rename habit_name : new_habit_name");
                }
            } else {
                return String.format(TextTemplates.UNKNOWN_HABIT, habitName);
            }
        } else {
            return String.format(TextTemplates.ERROR_IDENTIFY, "habit name", "/rename habit_name : new_habit_name");
        }
    }

    public String handlePrintHabits(long chatId) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return String.format(TextTemplates.EMPTY_HABIT_LIST);
        StringBuilder habitsText = new StringBuilder();
        habitsText.append("<b>These all your habits:</b>\n");
        for (Habit habit : userHabits) {
            habitsText.append("<code>" + habit.getHabitName() + "</code> ");
            habitsText.append(progressBarCreate(habit) + " ");
            habitsText.append(habit.countCompletedGoalsForCurrentMonth() + " / ");
            habitsText.append(habit.getGoalPerMonth() + "\n");
        }
        return habitsText.toString();
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
                    progressBar.append("|");
                }else {
                    progressBar.append(",");
                }
            }else {
                progressBar.append(".");
            }
        }
        return progressBar;
    }
}
