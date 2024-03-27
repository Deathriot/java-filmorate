package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validationExceptions.UserNotFoundException;

import java.util.*;

@Primary
@Component
public class DataBaseUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataBaseUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> get(rs.getInt("id")));
    }

    @Override
    public User create(User user) {
        user = checkUserName(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        int id = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).intValue();

        return get(id);
    }

    @Override
    public User update(User user) {
        user = checkUserName(user);
        userValidation(user.getId());

        String sql = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());

        return get(user.getId());
    }

    @Override
    public User get(int id) {
        User user;
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        SqlRowSet userSql = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (userSql.next()) {
            user = User.builder().id(userSql.getInt("id"))
                    .email(userSql.getString("email"))
                    .name(userSql.getString("name"))
                    .login(userSql.getString("login"))
                    .birthday(Objects.requireNonNull(userSql.getDate("birthday")).toLocalDate())
                    .build();
        } else {
            throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public List<User> getFriends(int id) {
        userValidation(id);
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                get(rs.getInt("friend_id")), id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        userValidation(userId);
        userValidation(friendId);

        if (isFriends(userId, friendId)) {
            String sqlQuery = "MERGE INTO user_friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } else {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        userValidation(userId);
        userValidation(friendId);
        if (!isFriends(userId, friendId)) {
            String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
        }

        // Новые тесты постаман не хотят получать 404 ошибку, если идет удаление друга, которого нет
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        userValidation(userId);
        userValidation(otherUserId);

        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ? AND  friend_id IN " +
                "(SELECT friend_id FROM user_friends WHERE user_id = ?)";

        Set<User> mutualFriends = new HashSet<>();

        SqlRowSet friends = jdbcTemplate.queryForRowSet(sql, userId, otherUserId);

        if (friends.next()) {
            mutualFriends.add(get(friends.getInt("friend_id")));
        }

        return new ArrayList<>(mutualFriends);
    }

    private User checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        return user;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> mapUser = new HashMap<>();

        mapUser.put("name", user.getName());
        mapUser.put("email", user.getEmail());
        mapUser.put("login", user.getLogin());
        mapUser.put("birthday", user.getBirthday());
        return mapUser;
    }

    private void userValidation(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userSql = jdbcTemplate.queryForRowSet(sql, id);

        if (!userSql.next()) {
            throw new UserNotFoundException();
        }
    }

    private boolean isFriends(int userId, int friendId) {
        String sql = "SELECT * FROM user_friends WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userFriends = jdbcTemplate.queryForRowSet(sql, userId, friendId);

        return !userFriends.next();
    }
}
