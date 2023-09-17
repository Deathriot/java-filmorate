package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validationExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.validationExceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.validationExceptions.UserNotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {
    @Autowired
    private FilmController controller;

    @Test
    public void shouldAddValidFilm() {
        Film film = Film.builder().name("Test").description("testing test").duration(100).rate(0)
                .userLike(new HashSet<>()).releaseDate(LocalDate.of(2000, 1, 1)).build();

        controller.addFilm(film);

        assertEquals(controller.getFilms().get(0), film);
        assertEquals(controller.getFilms().size(), 1);
    }

    @Test
    public void shouldUpdateValidFilm() {
        Film film = Film.builder().name("Test").description("testing test").duration(100).rate(0)
                .userLike(new HashSet<>()).releaseDate(LocalDate.of(2000, 1, 1)).build();

        controller.addFilm(film);

        film = film.toBuilder().name("Updated").build();

        controller.updateFilm(film);

        assertEquals(controller.getFilms().get(0), film);
        assertEquals(controller.getFilms().size(), 1);
    }

    @Test
    public void shouldGetFilms() {
        Film film1 = Film.builder().name("Test1").description("testing test1").duration(100)
                .rate(0).userLike(new HashSet<>()).releaseDate(LocalDate.of(2000, 1, 1)).build();

        Film film2 = Film.builder().name("Test2").description("testing test2").duration(200)
                .rate(0).userLike(new HashSet<>()).releaseDate(LocalDate.of(1999, 1, 1)).build();

        controller.addFilm(film1);
        controller.addFilm(film2);

        List<Film> films = List.of(film1, film2);

        assertEquals(controller.getFilms(), films);
    }

    @Test
    public void shouldNotAddFilmWithEmptyName() {
        final Film film1 = Film.builder().name("").description("testing test").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film1));

        final Film film2 = Film.builder().description("testing test").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film2));
    }

    @Test
    public void shouldNotAddFilmWithEmptyDescription() {
        final Film film1 = Film.builder().name("Test").description("").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film1));

        final Film film2 = Film.builder().name("Test").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film2));
    }

    @Test
    public void filmDescriptionShouldBeLessThan200() {

        String longDesc = "a".repeat(201);

        final Film film1 = Film.builder().name("Test").description(longDesc).duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film1));
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() {
        final Film film1 = Film.builder().name("Test").description("Testing test").duration(-100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film1));
    }

    @Test
    public void filmDateShouldBeNotBefore1895_12_28() {
        final Film film1 = Film.builder().name("Test").description("Testing test").duration(200)
                .releaseDate(LocalDate.of(1895, 12, 27)).build();

        assertThrows(FilmValidationException.class,
                () -> controller.addFilm(film1));

        final Film film2 = Film.builder().name("Test").description("Testing test").duration(200).build();

        assertThrows(ConstraintViolationException.class,
                () -> controller.addFilm(film2));
    }

    @Test
    public void shouldCorrectlyPutLike(){
        createFilms();

        controller.putLike(1,3);
        controller.putLike(1,2);
        controller.putLike(1,4);
        controller.putLike(2,2);

        assertEquals(controller.getFilm(1).getUserLike(), Set.of(3,2,4));
        assertEquals(controller.getFilm(1).getRate(), 3);

        assertEquals(controller.getFilm(2).getUserLike(), Set.of(2));
        assertEquals(controller.getFilm(2).getRate(), 1);

        assertEquals(controller.getFilm(3).getUserLike(), new HashSet<>());
        assertEquals(controller.getFilm(3).getRate(), 0);
    }

    @Test
    public void shouldNotPutLikeOnNotExistingFilm(){
        createFilms();

        assertThrows(FilmNotFoundException.class,
                () -> controller.putLike(10,1));
    }

    @Test
    public void sameUserShouldNotPutLikeOnSameFilmTwice(){
        createFilms();

        controller.putLike(1,1);

        assertThrows(FilmValidationException.class,
                () -> controller.putLike(1,1));
    }

    @Test
    public void shouldDeleteLikeCorrectly(){
        createFilms();

        controller.putLike(1,1);
        controller.putLike(1,2);

        controller.deleteLike(1,1);

        assertEquals(controller.getFilm(1).getUserLike(), Set.of(2));
        assertEquals(controller.getFilm(1).getRate(), 1);
    }

    @Test
    public void shouldNotDeleteLikeOnNotExistingFilm(){
        createFilms();

        controller.putLike(1,1);
        controller.putLike(1,2);

        assertThrows(FilmNotFoundException.class,
                () -> controller.deleteLike(5,1));
    }

    @Test
    public void shouldNotDeleteNotExistingLike(){
        createFilms();

        controller.putLike(1,1);
        controller.putLike(1,2);

        assertThrows(UserNotFoundException.class,
                () -> controller.deleteLike(1,4));
    }

    @Test
    public void shouldGetPopularFilmsCorrectly(){
        createFilms();

        controller.putLike(3,1);
        controller.putLike(3,2);
        controller.putLike(3,3);
        controller.putLike(3,4);

        controller.putLike(1,1);
        controller.putLike(1,2);

        List<Film> popularFilms = List.of(controller.getFilm(3), controller.getFilm(1)
                ,controller.getFilm(2));

        assertEquals(controller.getPopularFilms(3), popularFilms);
    }

    private void createFilms(){
        final Film film1 = Film.builder().name("Gachi Muchi").description("Movie about men friendship").duration(140)
                .releaseDate(LocalDate.of(2007, 1, 1)).build();

        final Film film2 = Film.builder().name("Test").description("Movie about Testing").duration(110)
                .releaseDate(LocalDate.of(2001, 4, 1)).build();

        final Film film3 = Film.builder().name("Cube").description("Movie about death maze").duration(120)
                .releaseDate(LocalDate.of(2003, 1, 11)).build();

        controller.addFilm(film1);
        controller.addFilm(film2);
        controller.addFilm(film3);
    }
}
