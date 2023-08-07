package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Data
@Builder(toBuilder = true)
@Validated
public class Film {
    private int id;
    @NotNull
    @NotEmpty
    final private String name;
    @NotNull
    @NotEmpty
    @Size(max = 200)
    final private String description;
    @NotNull
    final private LocalDate releaseDate;
    @Positive
    final private int duration;
}
