package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@Validated
public class User {
    private int id;
    @Email
    @NotNull
    final String email;
    @NotNull
    @NotBlank
    @NotEmpty
    final String login;
    final String name; // Может быть null
    @NotNull
    @Past
    final LocalDate birthday;
}
