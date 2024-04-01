package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAService {
    List<MPA> getAllMPA();

    MPA getMPAById(int mpaId);
}
