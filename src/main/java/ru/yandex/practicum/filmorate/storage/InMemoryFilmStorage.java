package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(int id) {
        return films.get(id);
    }

    public List<Film> getPopularFilms(int count) {

        return getAll()
                .stream()
                .sorted(Comparator.comparing(Film::getRate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }

    // Данная имплементация более не используется, времени мало, давайте потом ее сделаю:)
    @Override
    public void deleteLikeOnFilm(int filmId, int userId) {

    }

    @Override
    public void putLikeOnFilm(int filmId, int userId) {

    }

}
