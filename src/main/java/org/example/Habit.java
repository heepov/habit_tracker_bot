package org.example;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class Habit {
    private static int nextId = 1;
    private int habitId;
    private String habitName;
    private int goalPerMonth;
    private Map<LocalDate, Boolean> completeHistory;

    public Habit(String habitName, int goalPerMonth) {
        this.habitName = habitName;
        this.goalPerMonth = goalPerMonth;
        updateGoalPerMonth();
        this.habitId = nextId++;
        this.completeHistory = new HashMap<>();
    }
    public Habit(String habitName) {
        this.habitName = habitName;
        this.goalPerMonth = 32;
        updateGoalPerMonth();
        this.habitId = nextId++;
        this.completeHistory = new HashMap<>();
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

    public Map<LocalDate, Boolean> getCompleteHistory() {
        return completeHistory;
    }
    public void setTodayCompleteHistory(boolean toDo) {
        completeHistory.put(LocalDate.now(), toDo);
    }
    public void setTodayCompleteHistory(LocalDate date, boolean toDo) {
        completeHistory.put(date, toDo);
    }
    private void updateGoalPerMonth() {
        YearMonth currentYearMonth = YearMonth.now();
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int daysInPreviousMonth = currentYearMonth.minusMonths(1).lengthOfMonth();

        if (goalPerMonth == daysInPreviousMonth) {
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

        for (Map.Entry<LocalDate, Boolean> entry : completeHistory.entrySet()) {
            LocalDate date = entry.getKey();
            if (date.getYear() == currentYearMonth.getYear() && date.getMonth() == currentYearMonth.getMonth()) {
                if (entry.getValue()) {
                    count++;
                }
            }
        }

        return count;
    }
}



