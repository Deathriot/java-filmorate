package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Data
@Builder(toBuilder = true)
@Validated
public class Film {
    private int id;
    @NotEmpty
    private final String name;

    @NotEmpty
    @Size(max = 200)
    private final String description;

    @NotNull
    private final LocalDate releaseDate;

    @Positive
    private final int duration;

    @PositiveOrZero
    private final Integer rate;

    @JsonIgnore
    private final Set<Integer> userLike;

    private final Set<FilmGenre> genres;
    @NotNull
    private final MPA mpa;
}
