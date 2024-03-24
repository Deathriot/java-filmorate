package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Primary
public class DataBaseGenreService implements GenreService {
    private final GenreStorage storage;

    @Autowired
    public DataBaseGenreService(GenreStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<FilmGenre> getAllGenre() {
        return storage.getAllGenre();
    }

    @Override
    public FilmGenre getGenreById(int genreId) {
        return storage.getGenreById(genreId);
    }
}
