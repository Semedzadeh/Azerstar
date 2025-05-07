package com.azerstar.util;

public class Session {
    private static String currentUsername;
    private static String currentFirstname;
    private static String currentLastname;


    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
    public static void setCurrentFirstname(String firstname) {
        currentFirstname = firstname;
    }
    public static void setCurrentLastname(String lastname) {
        currentLastname = lastname;
    }


    public static String getCurrentUsername() {
        return currentUsername;
    }
    public static String getCurrentFirstname() {
        return currentFirstname;
    }
    public static String getCurrentLastname() {
        return currentLastname;
    }

    public static void clearSession() {
        currentUsername = null;
        currentFirstname = null;
        currentLastname = null;

    }
}