package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Primary
public class DataBaseUserService implements UserService {
    private final UserStorage storage;

    @Autowired
    public DataBaseUserService(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(User user) {
        return storage.update(user);
    }

    @Override
    public User get(int userId) {
        return storage.get(userId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        return storage.getFriends(userId);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        storage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        storage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return storage.getCommonFriends(userId, otherId);
    }
}
