package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class DataBaseFilmService implements FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public DataBaseFilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Film getFilm(int filmId) {
        return filmStorage.get(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public void deleteLikeOnFilm(Integer filmId, Integer userId) {
        filmStorage.deleteLikeOnFilm(filmId, userId);
    }

    @Override
    public void putLikeOnFilm(Integer filmId, Integer userId) {
        filmStorage.putLikeOnFilm(filmId, userId);
    }
}
