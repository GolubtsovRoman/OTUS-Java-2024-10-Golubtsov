package ru.otus.web.dao;

import ru.otus.web.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemorySingleAdminUserDao implements UserDao {

    private final Map<String, User> users = new HashMap<>(1);

    public InMemorySingleAdminUserDao(String login, String password) {
        users.put(login, new User(1L, "Администый админ", login, password));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.of(users.get(login));
    }
}
