package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validationExceptions.FilmValidationException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {
    @Autowired
    private FilmController controller;

    @Test
    public void shouldAddValidFilm() {
        Film film = Film.builder().name("Test").description("testing test").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        controller.addFilm(film);

        assertEquals(controller.getMapFilms().get(1), film);
        assertEquals(controller.getMapFilms().size(), 1);
    }

    @Test
    public void shouldUpdateValidFilm() {
        Film film = Film.builder().name("Test").description("testing test").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        controller.addFilm(film);

        film = film.toBuilder().name("Updated").build();

        controller.updateFilm(film);

        assertEquals(controller.getMapFilms().get(1), film);
        assertEquals(controller.getMapFilms().size(), 1);
    }

    @Test
    public void shouldGetFilms() {
        Film film1 = Film.builder().name("Test1").description("testing test1").duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1)).build();

        Film film2 = Film.builder().name("Test2").description("testing test2").duration(200)
                .releaseDate(LocalDate.of(1999, 1, 1)).build();

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
}
