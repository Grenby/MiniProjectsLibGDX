package com.mygdx.projects.utils;

import com.badlogic.gdx.ai.Logger;

public class ColoredLogger implements Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public void debug(String tag, String message) {
        this.println("[DEBUG]:", tag, message, ANSI_CYAN);
    }

    public void debug(String tag, String message, Throwable exception) {
        this.println("[DEBUG]:", tag, message, exception);
    }

    public void info(String tag, String message) {
        this.println("[INFO]:", tag, message, ANSI_YELLOW);
    }

    public void info(String tag, String message, Throwable exception) {
        this.println("[INFO]:", tag, message, exception);
    }

    public void error(String tag, String message) {
        this.println("[ERROR]:", tag, message, ANSI_RED);
    }

    public void error(String tag, String message, Throwable exception) {
        this.println("[ERROR]:", tag, message, exception);
    }

    private void println(String level, String tag, String message, String color) {
        System.out.println(color + level + " " + tag + ": " + message + ANSI_RESET);
    }

    private void println(String level, String tag, String message, Throwable exception) {
        this.println(level, tag, message, ANSI_RED);
        exception.printStackTrace();
    }
}
