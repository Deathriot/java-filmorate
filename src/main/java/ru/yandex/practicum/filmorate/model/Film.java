package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Data
@Builder(toBuilder = true)
@Validated
public class Film {
    private int id;

    @NotNull
    @NotEmpty
    final String name;

    @NotNull
    @NotEmpty
    @Size(max = 200)
    final String description;

    @NotNull
    final LocalDate releaseDate;

    @Positive
    final int duration;

    @PositiveOrZero
    final Integer rate;

    Set<Integer> userLike;
}
