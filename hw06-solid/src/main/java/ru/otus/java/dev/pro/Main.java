package ru.otus.java.dev.pro;

public class Main {

    public static void main(String ...args) {
        var message = "== Look to tests ==";
        var border = "=".repeat(message.length());
        System.out.printf("%s\n%s\n%s", border, message, border);
    }

}