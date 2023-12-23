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
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (messageText.contains(":")){
            Pattern patternHabitName = Pattern.compile("^\\s*(.*?):");
            Matcher matcherHabitName = patternHabitName.matcher(messageText);
            if (matcherHabitName.find()) {
                Pattern patternGoal = Pattern.compile(":\\s*(\\d+)");
                Matcher matcherGoal = patternGoal.matcher(messageText);
                if (matcherGoal.find()) {
                    Habit habit = new Habit(matcherHabitName.group(1).trim(),Integer.parseInt(matcherGoal.group(1)));
                    userHabits.add(habit);
                    bot.habitsList.put(chatId, userHabits);
                    return "You added \"" + habit.getHabitName() + "\" and set monthly goal " + habit.getGoalPerMonth();
                }else{
                    return "Can't identify \"goal\" in your message.\nYou should send \"/add habit_name : habit_goal\". Please try again!";
                }
            }
            return "Can't identify \"habit name\" in your message.\nYou should send \"/add habit_name : habit_goal\". Please try again!";
        }else {
            Habit habit = new Habit(messageText.trim());
            userHabits.add(habit);
            bot.habitsList.put(chatId, userHabits);
            return "You added \"" + habit.getHabitName() + "\" and set monthly goal " + habit.getGoalPerMonth();
        }
    }
    public String handleDelHabit(long chatId, String messageText) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return "You don't have any habits";

        Iterator<Habit> iterator = userHabits.iterator();
        while (iterator.hasNext()) {
            Habit habit = iterator.next();
            if (habit.getHabitName().equals(messageText.trim())) {
                iterator.remove();
                bot.habitsList.put(chatId, userHabits);
                return "You delete \"" + messageText + "\"";
            }
        }
        return "You don't have \"" + messageText + "\"";
    }
    public String handleChangeHabitGoal(long chatId, String messageText){
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return "You don't have any habits";

        Pattern patternHabitName = Pattern.compile("^\\s*(.*?):");
        Matcher matcherHabitName = patternHabitName.matcher(messageText);

        if (matcherHabitName.find()) { // Вызываем find() для поиска совпадений
            for (Habit habit : userHabits) {
                if (habit.getHabitName().equals(matcherHabitName.group(1).trim())) {
                    Pattern patternGoal = Pattern.compile(":\\s*(\\d+)");
                    Matcher matcherGoal = patternGoal.matcher(messageText);

                    if (matcherGoal.find()) {
                        habit.setGoalPerMonth(Integer.parseInt(matcherGoal.group(1)));
                        bot.habitsList.put(chatId, userHabits);
                        return "Habit goal \"" + matcherHabitName.group(1).trim() + "\" has been changed for " + habit.getGoalPerMonth();
                    } else {
                        return "Can't identify \"goal\" in your message.\nYou should send \"/goal habit_name : new_habit_goal\". Please try again!";
                    }
                }
            }
            return "You don't have \"" + matcherHabitName.group(1).trim() + "\"";
        } else {
            return "Can't identify \"habit name\" in your message.\nYou should send \"/goal habit_name : new_habit_goal\". Please try again!";
        }
    }
    public String handleRenameHabit(long chatId, String messageText){
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return "You don't have any habits";

        Pattern patternHabitName = Pattern.compile("^\\s*(.*?):");
        Matcher matcherHabitName = patternHabitName.matcher(messageText);
        if (matcherHabitName.find()) {
            for (Habit habit : userHabits) {
                if (habit.getHabitName().equals(matcherHabitName.group(1).trim())) {
                    Pattern patternNewName = Pattern.compile(":\\s*(.*)");
                    Matcher matcherNewName = patternNewName.matcher(messageText);
                    if (matcherNewName.find()) {
                        habit.setHabitName(matcherNewName.group(1));
                        bot.habitsList.put(chatId, userHabits);
                        return "Habit name \"" + matcherHabitName.group(1).trim() + "\" has been changed for \"" + habit.getHabitName() + "\"";
                    } else {
                        return "Can't identify \"new name\" in you message.\nYou should send \"/rename habit_old_name : habit_new_name\". Please try again!";
                    }
                }
            }
            return "You don't have \"" + matcherHabitName.group(1).trim() + "\"";
        }else {
            return "Can't identify \"habit name\" in your message.\nYou should send \"/rename habit_old_name : habit_new_name\". Please try again!";
        }
    }
    public String handlePrintHabits(long chatId) {
        ArrayList<Habit> userHabits = bot.habitsList.getOrDefault(chatId, new ArrayList<>());
        if (userHabits.isEmpty()) return "You don't have any habits";
        StringBuilder habitsText = new StringBuilder();
        habitsText.append("<b>These all your habits:</b>\n");
        for (Habit habit : userHabits) {
            habitsText.append(habit.getHabitName() + " ");
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
                    progressBar.append("●");
                }else {
                    progressBar.append("○");
                }
            } else if (currentDate.equals(LocalDate.now())){
                progressBar.append("■");
            }else {
                progressBar.append("□");
            }
        }
        return progressBar;
    }
}
