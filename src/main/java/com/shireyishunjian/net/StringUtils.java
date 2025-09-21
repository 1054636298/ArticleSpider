package com.shireyishunjian.net;

import java.util.Random;

public class StringUtils {
    private static final String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
    private static final Random random = new Random();
    private static final String[] EMAILS={
            "@139.com","@163.com","@sina.com","@sohu.com","@gmail.com","@outlook.com"
    };

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomPassword(){
        return getRandomString(random.nextInt(8,16));
    }

    public static String getRandomName(){
        return getRandomString(random.nextInt(5,9));
    }

    public static String getRandomEmail(){
        String name=getRandomString(random.nextInt(5,14));
        String address=EMAILS[random.nextInt(EMAILS.length)];
        return name+address;
    }
}
