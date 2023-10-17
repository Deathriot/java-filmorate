package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    void add(Film film);

    void update(Film film);

    Film get(int id);

    List<Film> getPopularFilms(int count);
}
