package com.savadanko.controller;

import com.savadanko.domain.Coordinates;
import com.savadanko.domain.requests.CreateCoordinatesRequest;
import com.savadanko.domain.requests.UpdateCoordinatesRequest;
import com.savadanko.repository.CoordinatesRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinatesController {

    private final CoordinatesRepository repo;

    public CoordinatesController(CoordinatesRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @Operation(summary = "Список координат")
    public List<Coordinates> findAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти координаты по id")
    public Coordinates findById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coordinates not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Создать координаты")
    public Coordinates create(@Valid @RequestBody CreateCoordinatesRequest req) {
        Coordinates c = new Coordinates();
        c.setX(req.x());
        c.setY(req.y());
        return repo.save(c);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Обновить координаты")
    public Coordinates update(@PathVariable Long id, @Valid @RequestBody UpdateCoordinatesRequest req) {
        Coordinates c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coordinates not found"));
        if (req.x() != null) c.setX(req.x());
        if (req.y() != null) c.setY(req.y());
        return c;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Operation(summary = "Удалить координаты")
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coordinates not found");
        }
        repo.deleteById(id);
    }
}

