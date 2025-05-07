package com.azerstar.util;

public class Session {
    private static String currentUsername;
    private static String currentFirstname;
    private static String currentLastname;


    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }


    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void clearSession() {
        currentUsername = null;

    }
}