package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void add(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(int id) {
        return films.get(id);
    }

    public List<Film> getPopularFilms(int count){
        List<Film> popularFilms = getAll()
                .stream()
                .sorted(Comparator.comparing(Film::getRate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());

        return popularFilms;
    }
}
