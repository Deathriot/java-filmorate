package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validationExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.validationExceptions.UserNotFoundException;

import java.util.*;

@Component
@Primary
@Slf4j
public class DataBaseFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataBaseFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String request = "SELECT film_id FROM films ORDER BY film_id";

        return jdbcTemplate.query(request, (resSet, num) -> get(resSet.getInt("film_id")));
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        int id = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue();

        if (film.getGenres() != null) {
            addFilmGenres(film, id);
        }

        return get(id);
    }

    @Override
    public Film update(Film film) {
        validationFilm(film.getId());

        String sqlQuery = "UPDATE films SET name = ?, rate = ?, description = ?,  duration = ?, release_date = ?, " +
                "mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getRate(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());

        return get(film.getId());
    }

    @Override
    public Film get(int id) {
        validationFilm(id);

        String sqlFilm = "SELECT * FROM films WHERE film_id = ?";
        String sqlGenre = "SELECT genre_id FROM films_genre WHERE film_id = ?";
        String sqlUsersLike = "SELECT * FROM users_like WHERE film_id = ?";

        List<FilmGenre> genresFilm =
                jdbcTemplate.query(sqlGenre, (rs, rowNum) -> getGenre(rs.getInt("genre_id")), id);
        List<Integer> usersLike = jdbcTemplate.query(sqlUsersLike, (rs, rowNum) -> rs.getInt("user_id"), id);
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(sqlFilm, id);

        Film film = null;

        //так хотят тесты, не я
        Set<FilmGenre> sortedGenres = new TreeSet<>(Comparator.comparingInt(FilmGenre::getId));
        sortedGenres.addAll(genresFilm);

        if (sqlQuery.next()) {
            film = Film.builder()
                    .id(id)
                    .name(sqlQuery.getString("name"))
                    .rate(sqlQuery.getInt("rate"))
                    .description(sqlQuery.getString("description"))
                    .duration(sqlQuery.getInt("duration"))
                    .releaseDate(Objects.requireNonNull(sqlQuery.getDate("release_date")).toLocalDate())
                    .mpa(getMPA(sqlQuery.getInt("mpa_id")))
                    .genres(sortedGenres)
                    .userLike(new HashSet<>(usersLike))
                    .build();
        }

        log.info("get Film id = " + id);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM films ORDER BY rate DESC LIMIT ?;";

        List<Film> popularFilms = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> get(rs.getInt("film_id")),
                count);
        popularFilms.sort(Comparator.comparingInt(Film::getRate).reversed());

        return popularFilms;
    }

    @Override
    public void deleteLikeOnFilm(int filmId, int userId) {
        if (!isUserLiked(filmId, userId)) {
            throw new UserNotFoundException("Такой пользователь не лайкал фильм");
        }

        String sqlUserLike = "DELETE FROM users_like WHERE film_id = ? AND user_id = ?";
        String sqlRate = "UPDATE films SET rate = ? WHERE film_id = ?";
        Film film = get(filmId);

        jdbcTemplate.update(sqlUserLike, filmId, userId);
        jdbcTemplate.update(sqlRate, (film.getRate() - 1), filmId);
    }

    @Override
    public void putLikeOnFilm(int filmId, int userId) {
        if (isUserLiked(filmId, userId)) {
            throw new IllegalArgumentException("Данный пользователь уже поставил лайк этому фильму");
        }

        String sqlUserLike = "MERGE INTO users_like (film_id, user_id) VALUES (?, ?)";
        String sqlRate = "UPDATE films SET rate = ? WHERE film_id = ?";
        Film film = get(filmId);

        jdbcTemplate.update(sqlUserLike, filmId, userId);
        jdbcTemplate.update(sqlRate, (film.getRate() + 1), filmId);
    }

    private MPA getMPA(int id) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (mpaRow.next()) {
            return MPA.builder()
                    .id(mpaRow.getInt("mpa_id"))
                    .name(mpaRow.getString("title"))
                    .build();
        } else {
            throw new NoSuchElementException("МРА с таким id не существует");
        }
    }

    private FilmGenre getGenre(int id) {
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (genreRow.next()) {
            return FilmGenre.builder()
                    .id(genreRow.getInt("genre_id"))
                    .name(genreRow.getString("name"))
                    .build();
        } else {
            throw new NoSuchElementException("Жанра под таким идентификатором не существует");
        }
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> mapFilm = new HashMap<>();

        mapFilm.put("name", film.getName());
        mapFilm.put("description", film.getDescription());
        mapFilm.put("rate", film.getRate());
        mapFilm.put("release_date", film.getReleaseDate());
        mapFilm.put("duration", film.getDuration());
        mapFilm.put("mpa_id", film.getMpa().getId());

        return mapFilm;
    }

    private boolean isUserLiked(Integer filmId, Integer userId) {
        String sqlQuery = "SELECT * FROM users_like WHERE film_id =? AND user_id = ?";
        SqlRowSet userLike = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);

        return userLike.next();
    }

    private void validationFilm(int filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet film = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        if (!film.next()) {
            throw new FilmNotFoundException("Фильм с таким id не найден");
        }
    }

    private void addFilmGenres(Film film, int filmId) {
        Set<FilmGenre> filmGenres = new HashSet<>(film.getGenres());

        for (FilmGenre genre : filmGenres) {
            String sqlFilmGenres = "MERGE INTO films_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlFilmGenres, filmId, genre.getId());
        }
    }
}
