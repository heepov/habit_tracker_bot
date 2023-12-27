package org.example;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class Habit  implements Serializable {
    // hide parameter needs to generate Habit ID
    private static int nextId = 1;
    private int habitId;
    private String habitName;
    private int habitMonthlyGoal;
    private Map<LocalDate, Boolean> habitProgressHistory;

    public Habit(String habitName, int habitMonthlyGoal) {
        this.habitName = habitName;
        this.habitMonthlyGoal = habitMonthlyGoal;
        //checking correct input monthly goal (0 < goal <= day current month)
        checkHabitGoalPerMonth();
        this.habitId = nextId++;
        this.habitProgressHistory = new HashMap<>();
    }
    public Habit(String habitName) {
        this.habitName = habitName;
        this.habitMonthlyGoal = 32;
        //set monthly goal (every day for current month)
        checkHabitGoalPerMonth();
        this.habitId = nextId++;
        this.habitProgressHistory = new HashMap<>();
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
    public int getHabitMonthlyGoal() {
        return habitMonthlyGoal;
    }
    public void setHabitMonthlyGoal(int habitMonthlyGoal){
        this.habitMonthlyGoal = habitMonthlyGoal;
        //checking correct input monthly goal (0 < goal <= day current month)
        checkHabitGoalPerMonth();
    }

    public Map<LocalDate, Boolean> getHabitProgressHistory() {
        return habitProgressHistory;
    }
    public void setProgressHistoryByDate(LocalDate date, boolean toDo) {
        habitProgressHistory.put(date, toDo);
    }

    //if last month goal set up for everyday next month set to everyday too
    // and check out of range 0 < habitMonthlyGoal < days this month
    private void checkHabitGoalPerMonth() {
        YearMonth currentYearMonth = YearMonth.now();
        int daysInThisMonth = currentYearMonth.lengthOfMonth();
        int daysInPreviousMonth = currentYearMonth.minusMonths(1).lengthOfMonth();

        if (habitMonthlyGoal == daysInPreviousMonth) {
            habitMonthlyGoal = daysInThisMonth;
        } else if (habitMonthlyGoal >=daysInThisMonth) {
            habitMonthlyGoal = daysInThisMonth;
        } else {
            habitMonthlyGoal = Math.min(daysInThisMonth, habitMonthlyGoal);
        }
    }

    public int countCompletedOneHabitGoalsForCurrentMonth() {
        YearMonth currentYearMonth = YearMonth.now();
        int count = 0;
        for (Map.Entry<LocalDate, Boolean> entry : habitProgressHistory.entrySet()) {
            LocalDate date = entry.getKey();
            // this habit did in this month
            if (date.getYear() == currentYearMonth.getYear() && date.getMonth() == currentYearMonth.getMonth()) {
                if (entry.getValue()) {
                    count++;
                }
            }
        }
        return count;
    }
}