package ru.yandex.practicum.filmorate.validationExceptions;

public class FilmValidationException extends RuntimeException {
    public FilmValidationException() {
        super();
    }

    public FilmValidationException(String message) {
        super(message);
    }
}
