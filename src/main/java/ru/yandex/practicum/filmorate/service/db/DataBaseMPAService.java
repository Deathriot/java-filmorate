package ru.yandex.practicum.filmorate.service.db;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Service
@Primary
public class DataBaseMPAService implements MPAService {
    private final MPAStorage storage;

    public DataBaseMPAService(MPAStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<MPA> getAllMPA() {
        return storage.getAllMPA();
    }

    @Override
    public MPA getMPAById(int mpaId) {
        return storage.getMPAById(mpaId);
    }
}
