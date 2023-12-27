package org.example;
// this file contains all string variables
// just for simplify to change them
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
    public static final String ERROR_ADD_HABIT_DUPLICATED = "You can't add \"%s\", because it's already added";
    public static final String ERROR_RENAME_HABIT_DUPLICATED = "You can't set this name \"%s\", because it's already exist. Please change new habit name!";
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
            /check to check your habits
            <code>/check</code> or <code>/check 01.12.2023</code>
            """;
    public static final String HABITS_COMMANDS_PRINT =
            """
            <b>Command list:</b>\n
            /add to add new habit and set monthly goal\n<code>/add meditation</code> or <code>/add meditation : 24</code>\n
            /del to delete one your habit\n<code>/del meditation</code>\n
            /goal to change monthly goal for your habits\n<code>/goal meditation : 12</code>\n
            /rename to rename your habit\n<code>/rename meditation : drink beer</code>\n
            """;
    public static final String ALL_HABITS = "<b>These all your habits</b>";
    public static final String TODAY_HABITS_LIST = "Today habit check list:";
    public static final String DATE_HABITS_LIST = "Habit check list from date";
    public static final String ERROR_WRONG_DATE = "Wrong date!";
}
