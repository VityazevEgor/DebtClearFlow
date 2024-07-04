package com.vityazev_egor.debt_clear_flow_server.Misc;

import java.nio.file.*;
import java.util.regex.Pattern;
// класс, который хранит статичные переменные для сервера и методы
public class Shared {
    public static final Path csvFilesDirectory = Paths.get("csvFilesDirectory").toAbsolutePath();


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
}
