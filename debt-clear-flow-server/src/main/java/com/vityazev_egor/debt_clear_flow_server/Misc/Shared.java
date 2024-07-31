package com.vityazev_egor.debt_clear_flow_server.Misc;

import java.nio.file.*;
import java.util.regex.Pattern;
// класс, который хранит статичные переменные для сервера и методы
public class Shared {
    public static final Path csvFilesDirectory = Paths.get("csvFilesDirectory").toAbsolutePath();
    public static final Path imagesDirectory = Paths.get("imagesDirectory").toAbsolutePath();


    private static final String emailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    private static final Pattern emailPattern  = Pattern.compile(emailRegex);
    private static final Pattern fullNamePattern  = Pattern.compile("^[a-zA-Zа-яА-Я]+ [a-zA-Zа-яА-Я]+ [a-zA-Zа-яА-Я]+$");
    
    public static Boolean isEmail(String content){
        content = content.trim();
        return emailPattern.matcher(content).matches();
    }

    public static Boolean isFullName(String content){
        content = content.trim();
        String[] words = content.split(" ");
        if (!fullNamePattern.matcher(content).matches()){
            return false;
        }
        for (String word: words){
            if (!Character.isUpperCase( word.charAt(0))){
                return false;
            }
        }
        if (words.length != 3){
            return false;
        }
        return true;
    }

    // generate random string of given length
    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt( (int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}
