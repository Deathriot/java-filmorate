package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(int filmId);

    List<Film> getPopularFilms(int count);

    void deleteLikeOnFilm(Integer filmId, Integer userId);

    void putLikeOnFilm(Integer filmId, Integer userId);
}