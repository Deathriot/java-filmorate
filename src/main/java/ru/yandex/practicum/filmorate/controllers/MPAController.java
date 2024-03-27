package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService mpa;

    @Autowired
    public MPAController(MPAService mpa) {
        this.mpa = mpa;
    }

    @GetMapping
    public List<MPA> getAllMPA() {
        return mpa.getAllMPA();
    }

    @GetMapping("/{id}")
    public MPA getMPAById(@PathVariable int id) {
        return mpa.getMPAById(id);
    }
}