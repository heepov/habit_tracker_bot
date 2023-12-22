package org.example;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class Habit {
    private static int nextId = 1;
    private int habitId;
    private String habitName;
    private int goalPerMonth;
    private Map<LocalDate, Boolean> completionByDay;
    private List<Map<LocalDate, Boolean>> monthlyHistory;

    public Habit(String habitName, int goalPerMonth) {
        this.habitName = habitName;
        this.goalPerMonth = goalPerMonth;
        updateGoalPerMonth(); // Установим goalPerMonth при создании привычки
        this.habitId = nextId++;
        this.completionByDay = new HashMap<>();
        this.monthlyHistory = new ArrayList<>();
    }
    public Habit(String habitName) {
        this.habitName = habitName;
        this.goalPerMonth = 32;
        updateGoalPerMonth(); // Установим goalPerMonth при создании привычки
        this.habitId = nextId++;
        this.completionByDay = new HashMap<>();
        this.monthlyHistory = new ArrayList<>();
    }

    public int getHabitId() {
        return habitId;
    }

    public String getHabitName() {
        return habitName;
    }
    public void setHabitName(String habitName){
        this.habitName = habitName;
    }

    public int getGoalPerMonth() {
        return goalPerMonth;
    }
    public void setGoalPerMonth(int goalPerMonth){
        this.goalPerMonth = goalPerMonth;
        updateGoalPerMonth();
    }

    public Map<LocalDate, Boolean> getCompletionByDay() {
        return completionByDay;
    }
    public void setCompletionByDay(boolean toDo) {
        completionByDay.put(LocalDate.now(), toDo);
    }

    public List<Map<LocalDate, Boolean>> getMonthlyHistory() {
        return monthlyHistory;
    }

    public void resetCompletionForNewMonth() {
        YearMonth currentYearMonth = YearMonth.now();
        if (!currentYearMonth.equals(getLastRecordDate())){
            System.out.println("Month has changed");
            monthlyHistory.add(new HashMap<>(completionByDay));
            completionByDay.clear();
            updateGoalPerMonth(); // Обновим goalPerMonth при сбросе для нового месяца
        }
        System.out.println("Month has not changed");
    }
    public YearMonth getLastRecordDate() {
        LocalDate lastEntryDate = completionByDay.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByKey())
                    .orElseThrow()
                    .getKey();
        return YearMonth.from(lastEntryDate);
    }


    private void updateGoalPerMonth() {
        YearMonth currentYearMonth = YearMonth.now();
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int daysInPreviousMonth = currentYearMonth.minusMonths(1).lengthOfMonth();

        if (goalPerMonth == daysInPreviousMonth && !monthlyHistory.isEmpty()) {
            goalPerMonth = daysInMonth;
        } else if (goalPerMonth>=daysInMonth) {
            goalPerMonth = daysInMonth;
        } else {
            goalPerMonth = Math.min(daysInMonth, goalPerMonth);
        }
    }

    public int countCompletedGoalsForCurrentMonth() {
        YearMonth currentYearMonth = YearMonth.now();
        int count = 0;

        for (Map.Entry<LocalDate, Boolean> entry : completionByDay.entrySet()) {
            LocalDate date = entry.getKey();
            if (date.getYear() == currentYearMonth.getYear() && date.getMonth() == currentYearMonth.getMonth()) {
                // Эта цель выполнена в текущем месяце
                if (entry.getValue()) {
                    count++;
                }
            }
        }

        return count;
    }
}



