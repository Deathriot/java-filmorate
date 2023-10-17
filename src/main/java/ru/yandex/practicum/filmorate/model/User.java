package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@Validated
public class User {
    private int id;

    @Email
    @NotNull
    private final String email;

    @NotBlank
    private final String login;

    private final String name; // Может быть null

    @NotNull
    @PastOrPresent
    private final LocalDate birthday;

    @JsonIgnore
    private final Set<Integer> friends;
}
