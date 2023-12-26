package org.example;

public class TextTemplates {
    public static final String START = "/start";
    public static final String INFO = "/info";
    public static final String MENU = "/menu";
    public static final String ADD = "/add";
    public static final String DELETE = "/del";
    public static final String SHOW = "/show";
    public static final String GOAL = "/goal";
    public static final String RENAME = "/rename";
    public static final String CHECK = "/check";
    public static final String HABIT = "/habit";

    public static final String YES = "Done";
    public static final String YES_DESCRIBE = "is done! You so cool!";
    public static final String NO = "Neah";
    public static final String NO_DESCRIBE = "not today... May be next time!";

    public static final String ERROR_IDENTIFY = "Can't identify \"%s\" in your message.\nYou should send \"%s\". Please try again!";
    public static final String ERROR_HABIT_DUPLICATED = "You can't add \"%s\", because it's already added";
    public static final String ADD_NEW_HABIT = "You added \"%s\" and set monthly goal %d";
    public static final String EMPTY_HABIT_LIST = "You don't have any habits";
    public static final String UNKNOWN_HABIT = "You don't have \"%s\"";
    public static final String DELETE_HABIT = "You delete \"%s\"";
    public static final String CHANGE_HABIT_GOAL = "The goal of the habit \"%s\" has been changed to %d";
    public static final String CHANGE_HABIT_NAME = "The name of the habit \"%s\" has been changed to \"%s\"";

    public static final String ERROR_MENU_COMMAND = "Incorrect message use " + MENU + " command";
    public static final String INFO_TEXT = "In this bot you can track your habits. " +
            "Set goals and review your progress. To get started send " + MENU;
    public static final String MENU_PRINT =
            """
            /info for information\n
            /habit to add, delete or change your habits\n
            /show to show all your habits\n
            /check to check your habits for today\n
            /check DD.MM.YYYY to check your habits for another day\n
            """;
    public static final String HABITS_COMMANDS_PRINT =
            """
            /add 'habit_name' or 'habit_name' : 'monthly_goal'\n<i>to add new habit and set monthly goal\nlike <code>/add meditation</code> or <code>/add meditation : 24</code></i>\n
            /del 'habit_name'\n<i>to delete your habit\nlike <code>/del meditation</code></i>\n
            /goal 'habit_name' : 'new_monthly_goal'\n<i>to change monthly goal for your habits\nlike <code>/add meditation : 24</code></i>\n
            /rename 'old_habit_name' : 'new_habit_name'\n<i>to rename your habit\nlike <code>/rename meditation : drink beer</code></i>\n
            """;
    public static final String ALL_HABITS = "<b>These all your habits:</b>";
    public static final String TODAY_HABITS_LIST = "Today habit check list:";
    public static final String DATE_HABITS_LIST = "Habit check list from date";
    public static final String ERROR_WRONG_DATE = "Wrong date!";
}
