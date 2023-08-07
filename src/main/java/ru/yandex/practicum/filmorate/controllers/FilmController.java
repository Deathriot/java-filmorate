package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validationExceptions.FilmValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);

        if (films.containsKey(film.getId())) {
            log.warn("Невозможно добавить фильм : такой фильм уже существует");
            throw new FilmValidationException();
        }

        film.setId(nextId);
        nextId++;
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validateFilm(film);

        if (!films.containsKey(film.getId())) {
            log.warn("Невозможно обновить фильм : такого фильма не существует");
            throw new FilmValidationException();
        }

        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен");
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) {
        // Не умею делать аннотации, придется так
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {

            log.warn("Неверно передан фильм!");
            throw new FilmValidationException();
        }
    }

    public Map<Integer, Film> getMapFilms() {
        return films; // Для тестов
    }
}
