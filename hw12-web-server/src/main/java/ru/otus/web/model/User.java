package ru.otus.web.model;

public record User(long id,
                   String name,
                   String login,
                   String password) {
}
