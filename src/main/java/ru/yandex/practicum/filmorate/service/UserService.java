package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validationExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.validationExceptions.UserValidationException;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage; // Не уверен, что это так делается

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    private int nextId = 1;

    public User create(@Valid User user) {

        if (userStorage.get(user.getId()) != null) {
            log.warn("Невозможно добавить пользователя : такой пользователь уже существует");
            throw new UserValidationException("Такой пользователь уже существует"); //Если пользователь уже существует
        }

        user.setId(nextId);
        nextId++;

        if (user.getName() == null || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        if(user.getFriends() == null){
            user = user.toBuilder().friends(new HashSet<>()).build();
            //по дефолту в постмане коллекция друзей не объявлена
        }

        userStorage.create(user);

        log.info("Пользователь успешно добавлен");
        return user;
    }

    public User update(@Valid User user) {

        if (userStorage.get(user.getId()) == null) {
            log.warn("Невозможно обновить пользователя : такого пользователя не существует!");
            throw new UserNotFoundException("Невозможно обновить пользователя, пользователя с таким id не существует"); //Если пользователя не существует
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build(); // Если имени нет - оно становится таким же как и логин
        }

        if(user.getFriends() == null){
            user = user.toBuilder().friends(new HashSet<>()).build();
        }

        userStorage.update(user);

        log.info("Пользователь успешно обновлен");

        return user;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User get(int id){
        if(userStorage.get(id) == null){
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        return userStorage.get(id);
    }

    public void addFriend(Integer id, Integer friendId){
        if(id < 0){
            throw new IllegalArgumentException("айди пользователя не может быть отрицательно");
        }

        User mainUser = userStorage.get(id);
        User friendUser = userStorage.get(friendId);

        if(mainUser == null){
            log.warn("Пользователь, который хочет добавить друга, не найден");
            throw new UserNotFoundException("Указан неверный id пользователя");
        }

        if(friendUser == null){
            log.warn("Запрос в друзья невозможен - такого пользователя не существует");
            throw new UserNotFoundException("Указан неверный id друга");
        }

        mainUser.getFriends().add(friendId);
        friendUser.getFriends().add(id);

        log.info("Друг успешно добавлен!");
    }

    public void deleteFriend(Integer id, Integer friendId){
        if(id < 0){
            throw new IllegalArgumentException("айди пользователя не может быть отрицательно");
        }

        if(friendId < 0){
            throw new IllegalArgumentException("айди друга не может быть отрицательно");
        }

        User mainUser = userStorage.get(id);
        User friendUser = userStorage.get(friendId);

        if(mainUser == null){
            log.warn("Пользователь, который хочет удалить друга, не найден");
            throw new UserNotFoundException("Указан неверный id пользователя");
        }

        if(friendUser == null){
            log.warn("удаление из друзей невозможно - такого пользователя не существует");
            throw new UserNotFoundException("Указан неверный id друга");
        }

        mainUser.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);

        log.info("Друг успешно удален!");
    }

    public List<User> getFriends(Integer userId){
        if(userId < 0){
            throw new IllegalArgumentException("айди пользователя не может быть отрицательно");
        }

        User user = userStorage.get(userId);

        if(user == null){
            log.warn("Пользователь не существует");
            throw new UserNotFoundException("Указан неверный id пользователя");
        }

        List<User> userFriends = new ArrayList<>();

        for(Integer friendId : user.getFriends()){
            User friend = userStorage.get(friendId);

            if(friend == null){
                log.warn("в списке друзей нет пользователя с таким id");
                throw new UserNotFoundException("Указан неверный id друга в списке друзей");
            }

            userFriends.add(friend);
        }

        log.info("Список друзей успешно получен!");

        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId){
        if(userId < 0){
            throw new IllegalArgumentException("айди пользователя не может быть отрицательно");
        }

        if(otherUserId < 0){
            throw new IllegalArgumentException("айди другого пользователя не может быть отрицательно");
        }

        User user = userStorage.get(userId);
        User otherUser = userStorage.get(otherUserId);

        if(user == null){
            log.warn("Пользователь с таким id не найден, невозможен запрос получения общих друзей");
            throw new UserNotFoundException("Указан неверный id пользователя");
        }

        if(otherUser == null){
            log.warn("Другого пользователя с таким id для поиска общих друзей не найдено");
            throw new UserNotFoundException("Указан неверный id другого пользователя");
        }

        Set<Integer> userFriends = user.getFriends();
        final Set<Integer> otherUserFriends = otherUser.getFriends();

        List<User> commonFriends = userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::get)
                .collect(Collectors.toList());

        log.info("Список общих друзей успешно получен");

        return commonFriends;
    }
}