package ru.yandex.practicum.filmorate.validationExceptions;

public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException() {
    }

    public FilmNotFoundException(String message) {
        super(message);
    }
}
