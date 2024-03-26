package ru.yandex.practicum.filmorate.dataBaseTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DataBaseTest {
    private final UserStorage userStorage;
    private final UserService userService;
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final MPAStorage mpaService;
    private final GenreStorage genreService;
    User firstUser;
    User secondUser;
    User thirdUser;
    User fourthUser;
    Film firstFilm;
    Film secondFilm;
    Film thirdFilm;
    MPA mpa;
    Set<FilmGenre> genres;

    @BeforeEach
    public void createObjects() {
        firstUser = User.builder()
                .email("First@yandex.ru")
                .login("first")
                .name("One Firstov")
                .birthday(LocalDate.of(1978, 3, 15))
                .build();
        secondUser = User.builder()
                .email("Second@yandex.ru")
                .login("second")
                .name("Two Secondov")
                .birthday(LocalDate.of(1999, 7, 10))
                .build();
        thirdUser = User.builder()
                .email("Third@yandex.ru")
                .login("third")
                .name("Three Thirdov")
                .birthday(LocalDate.of(2005, 1, 28))
                .build();
        fourthUser = User.builder()
                .email("Fourth@yandex.ru")
                .login("fourth")
                .name(" ")
                .birthday(LocalDate.of(2002, 1, 1))
                .build();

        mpa = MPA.builder()
                .id(3)
                .name("PG-13")
                .build();

        genres = Set.of(FilmGenre.builder()
                        .id(1)
                        .build(),
                FilmGenre.builder()
                        .id(5)
                        .build());

        firstFilm = Film.builder()
                .name("First Film")
                .description("Cool")
                .duration(125)
                .releaseDate(LocalDate.of(1905, 5, 24))
                .mpa(mpa)
                .genres(genres)
                .build();
        secondFilm = Film.builder()
                .name("Second Film")
                .description("Beautiful")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 7, 15))
                .mpa(MPA.builder()
                        .id(1)
                        .build())
                .build();
        thirdFilm = Film.builder()
                .name("Third Film")
                .description("Very Good Film")
                .duration(240)
                .releaseDate(LocalDate.of(2000, 5, 11))
                .mpa(MPA.builder()
                        .id(4)
                        .build())
                .build();
    }

    @Test
    public void addUserTest() {
        firstUser = userStorage.create(firstUser);
        User userTest = userStorage.get(1);

        assertThat(firstUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }


    @Test
    public void addUserWithNoNameTest() {
        fourthUser = userStorage.create(fourthUser);
        User userTest = userStorage.get(1);

        assertThat(fourthUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }

    @Test
    public void updateUserTest() {
        thirdUser = userStorage.create(thirdUser);
        User user2 = userStorage.update(thirdUser);

        assertThat(thirdUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    public void updateUserWithNoNameTest() {
        fourthUser = userStorage.create(fourthUser);
        firstUser = userStorage.update(fourthUser);

        assertThat(fourthUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(firstUser);
    }

    @Test
    public void addFriendTest() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);

        userStorage.addFriend((firstUser.getId()), secondUser.getId());

        List<User> firstUserListFriends = userStorage.getFriends(firstUser.getId());
        List<User> secondUserListFriends = userStorage.getFriends(secondUser.getId());

        assertThat(firstUserListFriends).asList().hasSize(1);
        assertThat(firstUserListFriends).asList().contains(userStorage.get(secondUser.getId()));

        assertThat(secondUserListFriends).asList().hasSize(0);
    }

    @Test
    public void deleteFriendTest() {
        thirdUser = userStorage.create(thirdUser);
        fourthUser = userStorage.create(fourthUser);

        userService.addFriend(thirdUser.getId(), fourthUser.getId());
        userService.deleteFriend(thirdUser.getId(), fourthUser.getId());

        List<User> listFriends = userService.getFriends(thirdUser.getId());

        assertThat(listFriends).asList().hasSize(0);
        assertThat(listFriends).asList().doesNotContain(firstUser);
    }

    @Test
    public void getCommonFriendsTest() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        thirdUser = userStorage.create(thirdUser);

        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());
        List<User> mutualFriends = userService.getCommonFriends(firstUser.getId(), secondUser.getId());

        assertThat(mutualFriends).asList().hasSize(1);
        assertThat(mutualFriends).asList().contains(userStorage.get(thirdUser.getId()));
    }

    @Test
    public void getAllUsersTest() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        thirdUser = userStorage.create(thirdUser);

        List<User> listUsers = userStorage.getAll();

        assertThat(listUsers).asList().hasSize(3);
        assertThat(listUsers).asList().contains(userStorage.get(firstUser.getId()));
        assertThat(listUsers).asList().contains(userStorage.get(secondUser.getId()));
        assertThat(listUsers).asList().contains(userStorage.get(thirdUser.getId()));
    }

    @Test
    public void getEmptyUserListTest() {
        List<User> listUsers = userStorage.getAll();

        assertThat(listUsers).asList().isEmpty();
    }

    @Test
    public void addFilmTest() {
        firstFilm = filmStorage.add(firstFilm);
        Film film1 = filmStorage.get(1);

        assertThat(firstFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    public void updateFilmTest() {
        firstFilm = filmStorage.add(firstFilm);
        secondFilm = secondFilm.toBuilder()
                .id(1)
                .build();
        secondFilm = filmStorage.update(secondFilm);

        assertThat(filmStorage.get(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(secondFilm);
    }

    @Test
    public void getListFilmsEmptyTest() {
        List<Film> listFilms = filmStorage.getAll();

        assertThat(listFilms).asList().isEmpty();
    }

    @Test
    public void addLikeTest() {
        firstUser = userStorage.create(firstUser);
        firstFilm = filmStorage.add(firstFilm);

        filmService.putLikeOnFilm(firstFilm.getId(), firstUser.getId());
        firstFilm = filmStorage.get(firstFilm.getId());

        assertThat(firstFilm.getRate()).isEqualTo(1);
    }

    @Test
    public void deleteLikeTest() {
        secondUser = userStorage.create(secondUser);
        secondFilm = filmStorage.add(secondFilm);

        filmService.putLikeOnFilm(secondFilm.getId(), secondUser.getId());
        filmService.deleteLikeOnFilm(secondFilm.getId(), secondUser.getId());
        secondFilm = filmStorage.get(secondFilm.getId());

        assertThat(secondFilm.getRate()).isEqualTo(0);
    }

    @Test
    public void getPopularFilmsTest() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.add(firstFilm);
        secondFilm = filmStorage.add(secondFilm);

        filmService.putLikeOnFilm(firstFilm.getId(), firstUser.getId());
        filmService.putLikeOnFilm(secondFilm.getId(), secondUser.getId());
        filmService.putLikeOnFilm(secondFilm.getId(), firstUser.getId());

        List<Film> popularFilms = filmService.getPopularFilms(10);

        assertThat(popularFilms).asList().hasSize(2);
        assertThat(filmStorage.get(secondFilm.getId())).isEqualTo(popularFilms.get(0));
    }

    @Test
    public void getAllMPATest() {
        List<MPA> allMpa = mpaService.getAllMPA();
        assertThat(allMpa).asList().hasSize(5);

        assertThat(allMpa).asList().startsWith(mpaService.getMPAById(1));
        assertThat(allMpa).asList().contains(mpaService.getMPAById(2));
    }

    @Test
    public void getMPAByIdTest() {
        final int mpaId = 3;
        final String name = "PG-13";

        final MPA mpaTest = MPA.builder().id(mpaId).name(name).build();

        assertThat(mpaTest)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpaService.getMPAById(3));

    }

    @Test
    public void getAllGenresTest() {
        List<FilmGenre> listGenres = genreService.getAllGenre();

        assertThat(listGenres).asList().hasSize(6);

        assertThat(listGenres).asList().startsWith(genreService.getGenreById(1));
        assertThat(listGenres).asList().contains(genreService.getGenreById(2));
    }

    @Test
    public void getGenreByIdTest() {
        final int id = 3;
        final String name = "Мультфильм";

        assertThat(genreService.getGenreById(id))
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name);
    }
}
