package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validationExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.validationExceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.validationExceptions.UserNotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private int nextId = 1;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        validateFilm(film);

        if (filmStorage.get(film.getId()) != null) {
            log.warn("Невозможно добавить фильм : такой фильм уже существует");
            throw new FilmValidationException("Фильм с таким id уже существует");
        }

        film.setId(nextId);
        nextId++;

        if (film.getUserLike() == null) {
            film = film.toBuilder().userLike(new HashSet<>()).build();
            // инициализируем вручную
        }

        if (film.getRate() == null) {
            film = film.toBuilder().rate(0).build();
            //И это инициализируем
        }


        filmStorage.add(film);

        log.info("Фильм успешно добавлен");
        return film;
    }

    public Film updateFilm(Film film) {
        validateFilm(film);

        if (filmStorage.get(film.getId()) == null) {
            log.warn("Невозможно обновить фильм : такого фильма не существует");
            throw new FilmNotFoundException("Такой фильм не найден");
        }

        if (film.getUserLike() == null) {
            film = film.toBuilder().userLike(new HashSet<>()).build();
        }

        if (film.getRate() == null) {
            film = film.toBuilder().rate(0).build();
        }

        filmStorage.update(film);

        log.info("Фильм успешно обновлен");
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {

            log.warn("Неверно передан фильм!");
            throw new FilmValidationException("Неверно переданы параметры фильма");
        }
    }

    public void putLikeOnFilm(Integer userId, Integer filmId) {
        if (userId < 0) {
            throw new IllegalArgumentException("айди пользователя не может быть отрицательно");
        }

        if (filmId < 0) {
            throw new IllegalArgumentException("айди фильма не может быть отрицательно");
        }

        Film film = filmStorage.get(filmId);

        if (film == null) {
            throw new FilmNotFoundException("Указан неверный id фильма");
        }

        if (film.getUserLike().contains(userId)) {
            throw new FilmValidationException("Такой пользователь уже ставил лайк");
        }

        Set<Integer> userLike = film.getUserLike();
        Integer rate = film.getRate() + 1;
        userLike.add(userId);

        film = film.toBuilder()
                .rate(rate)
                .userLike(userLike)
                .build();

        filmStorage.update(film);
    }

    public void deleteLikeOnFilm(Integer userId, Integer filmId) {
        Film film = filmStorage.get(filmId);

        if (filmId < 0) {
            throw new IllegalArgumentException("айди фильма не может быть отрицательно");
        }

        if (film == null) {
            throw new FilmNotFoundException("Указан неверный id фильма");
        }

        if (!film.getUserLike().contains(userId)) {
            throw new UserNotFoundException("Такой пользователь не ставил лайк");
        }

        Set<Integer> userLike = film.getUserLike();
        userLike.remove(userId);

        film = film.toBuilder()
                .rate(film.getRate() - 1)
                .userLike(userLike)
                .build();

        filmStorage.update(film);
    }

    public List<Film> getPopularFilms(int count) {

        if (count < 0) {
            throw new IllegalArgumentException("Параметр count не может быть отрицательным");
        }

        return filmStorage.getPopularFilms(count);
    }

    public Film getFilm(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("айди фильма не может быть отрицательно");
        }

        Film film = filmStorage.get(id);

        if (film == null) {
            throw new FilmNotFoundException("Фильма с таким id не существует");
        }
        return film;
    }
}
