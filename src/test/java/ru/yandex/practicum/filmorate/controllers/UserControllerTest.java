package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validationExceptions.UserNotFoundException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private UserController controller;

    @Test
    public void shouldCreateUser() {
        User user = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).friends(new HashSet<>()).build();

        controller.createUser(user);

        assertEquals(controller.getUsers().get(0), user);
        assertEquals(controller.getUsers().size(), 1);
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).friends(new HashSet<>()).build();

        controller.createUser(user);

        user = user.toBuilder().name("Alex").build();

        controller.updateUser(user);

        assertEquals(controller.getUsers().get(0), user);
        assertEquals(controller.getUsers().size(), 1);
    }

    @Test
    public void shouldReturnAllUsers() {
        User user1 = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).friends(new HashSet<>()).build();

        User user2 = User.builder().email("Alex@gmail.com").name("Sasha").login("Alex123123")
                .birthday(LocalDate.of(2002, 5, 2)).friends(new HashSet<>()).build();

        controller.createUser(user1);
        controller.createUser(user2);

        List<User> users = List.of(user1, user2);

        assertEquals(controller.getUsers(), users);
    }

    @Test
    public void shouldReplaceEmptyNameWithLogin() {
        User user = User.builder().email("zanin.tima@gmail.com").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        controller.createUser(user);

        assertEquals(controller.getUsers().get(0).getName(), user.getLogin());
    }

    @Test
    public void userShouldNotBirthInFuture() {
        User user = User.builder().email("zanin.tima@gmail.com").login("Tima123123")
                .birthday(LocalDate.of(2222, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user));
    }

    @Test
    public void userShouldHaveCorrectEmail() {
        User user1 = User.builder().email("@Zanin.gmail.com").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user1));

        User user2 = User.builder().login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user2));
    }

    @Test
    public void userShouldHaveCorrectLogin() {
        User user1 = User.builder().email("zanin.tima@gmail.com")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user1));

        User user2 = User.builder().email("zanin.tima@gmail.com").login("")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user2));

        User user3 = User.builder().email("zanin.tima@gmail.com").login("      ")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user3));
    }

    @Test
    public void friendsShouldAddCorrectly() {
        createUsers();

        controller.addFriend(1, 2);
        assertEquals(controller.getUser(1).getFriends(), Set.of(2));
        assertEquals(controller.getUser(2).getFriends(), Set.of(1));
        assertEquals(controller.getUser(3).getFriends(), new HashSet<>()); //Нет друзей
    }

    @Test
    public void shouldNotAddFriendNotExistingUser() {
        createUsers();

        assertThrows(UserNotFoundException.class,
                () -> controller.addFriend(1, 6));

        assertThrows(UserNotFoundException.class,
                () -> controller.addFriend(6, 1));
    }

    @Test
    public void shouldDeleteFriendCorrectly() {
        createUsers();

        controller.addFriend(1, 2);

        controller.deleteFriend(1, 2);
        assertEquals(controller.getUser(1).getFriends(), new HashSet<>());
        assertEquals(controller.getUser(2).getFriends(), new HashSet<>());
    }

    @Test
    public void shouldNotDeleteNotExistingFriend() {
        createUsers();

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);

        assertThrows(UserNotFoundException.class,
                () -> controller.deleteFriend(6, 1));
    }

    @Test
    public void getFriendsTest() {
        createUsers();

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);

        assertEquals(controller.getFriends(1), List.of(controller.getUser(2), controller.getUser(3)));
        assertEquals(controller.getFriends(2), List.of(controller.getUser(1)));
    }

    @Test
    public void shouldNotGetFriendsOfNotExistingUser() {
        createUsers();

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);

        assertThrows(UserNotFoundException.class,
                () -> controller.getFriends(5));
    }

    @Test
    public void getCommonFriendsTest() {
        createUsers();

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);

        assertEquals(controller.getCommonFriends(1, 2), new ArrayList<>());
        assertEquals(controller.getCommonFriends(2, 3), List.of(controller.getUser(1)));
    }

    @Test
    public void shouldNotGetCommonFriendsOfNotExistingUser() {
        createUsers();

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);

        assertThrows(UserNotFoundException.class,
                () -> controller.getCommonFriends(1, 6));
        assertThrows(UserNotFoundException.class,
                () -> controller.getCommonFriends(111, 2));
    }

    private void createUsers() {
        User user1 = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        controller.createUser(user1);

        User user2 = User.builder().email("someone@gmail.com").name("Grisha").login("Grisha123")
                .birthday(LocalDate.of(1999, 1, 2)).build();

        controller.createUser(user2);

        User user3 = User.builder().email("Hello@gmail.com").name("Dima").login("Dima123")
                .birthday(LocalDate.of(1939, 1, 2)).build();

        controller.createUser(user3);
    }
}
