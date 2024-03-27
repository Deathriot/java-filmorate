package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    Film get(int id);

    List<Film> getPopularFilms(int count);

    void deleteLikeOnFilm(int filmId, int userId);

    void putLikeOnFilm(int filmId, int userId);
}
