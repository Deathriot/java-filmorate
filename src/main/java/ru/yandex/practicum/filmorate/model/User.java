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
    final private String email;
    @NotNull
    @NotBlank
    @NotEmpty
    final private String login;
    final private String name; // Может быть null
    @NotNull
    @Past
    final private LocalDate birthday;
}
