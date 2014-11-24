package com.example.mike.tpdisk;

/**
 * Created by Mike on 26.10.2014.
 */
public class Credentials {
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Credentials.token = token;
    }

}
