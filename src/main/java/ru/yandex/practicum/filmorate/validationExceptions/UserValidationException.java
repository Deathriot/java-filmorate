package ru.yandex.practicum.filmorate.validationExceptions;

public class UserValidationException extends RuntimeException{
    public UserValidationException(){
        super();
    }

    public UserValidationException(String message){
        super(message);
    }
}
