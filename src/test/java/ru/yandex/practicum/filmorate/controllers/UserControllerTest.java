package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private UserController controller;

    @Test
    public void shouldCreateUser() {
        User user = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        controller.createUser(user);

        assertEquals(controller.getMapUsers().get(1), user);
        assertEquals(controller.getMapUsers().size(), 1);
    }

    @Test
    public void shouldUpdateUser(){
        User user = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        controller.createUser(user);

        user = user.toBuilder().name("Alex").build();

        controller.updateUser(user);

        assertEquals(controller.getMapUsers().get(1),user);
        assertEquals(controller.getMapUsers().size(), 1);
    }

    @Test
    public void shouldReturnAllUsers(){
        User user1 = User.builder().email("zanin.tima@gmail.com").name("Tima").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        User user2 = User.builder().email("Alex@gmail.com").name("Sasha").login("Alex123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        controller.createUser(user1);
        controller.createUser(user2);

        List<User> users = List.of(user1, user2);

        assertEquals(controller.getUsers(), users);
    }

    @Test
    public void shouldReplaceEmptyNameWithLogin(){
        User user = User.builder().email("zanin.tima@gmail.com").login("Tima123123")
                .birthday(LocalDate.of(2002, 5, 2)).build();

        controller.createUser(user);

        assertEquals(controller.getMapUsers().get(1).getName(), user.getLogin());
    }

    @Test
    public void UserShouldNotBirthInFuture(){
        User user = User.builder().email("zanin.tima@gmail.com").login("Tima123123")
                .birthday(LocalDate.of(2222, 5, 2)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.createUser(user));
    }

    @Test
    public void UserShouldHaveCorrectEmail(){
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
    public void UserShouldHaveCorrectLogin(){
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
}
