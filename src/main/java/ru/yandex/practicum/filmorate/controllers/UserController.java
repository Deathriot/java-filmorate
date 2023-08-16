package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validationExceptions.UserValidationException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {

        if (users.containsKey(user.getId())) {
            log.warn("Невозможно добавить пользователя : такой пользователь уже существует");
            throw new UserValidationException(); //Если пользователь уже существует
        }

        user.setId(nextId);
        nextId++;

        if (user.getName() == null || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        if (!users.containsKey(user.getId())) {
            log.warn("Невозможно обновить пользователя : такого пользователя не существует!");
            throw new UserValidationException(); //Если пользователя не существует
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build(); // Если имени нет - оно становится таким же как и логин
        }

        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен");
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public Map<Integer, User> getMapUsers() {
        return new HashMap<>(users);
    }
}
