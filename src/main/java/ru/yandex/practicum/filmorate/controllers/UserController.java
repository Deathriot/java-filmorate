package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid  @RequestBody User user) {
        log.info("createUser");
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("updateUser");
        return userService.update(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("getUsers");
        return userService.getAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("addFriend");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("deleteFriend");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("getFriends");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("getCommonFriends");
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("getUser");
        return userService.get(id);
    }
}
