package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
   List<User> getAll();

   void create(User user);

   void update(User user);

   User get(int id);
}
