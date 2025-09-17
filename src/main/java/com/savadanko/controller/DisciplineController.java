package com.savadanko.controller;

import com.savadanko.domain.Discipline;
import com.savadanko.domain.requests.CreateDisciplineRequest;
import com.savadanko.domain.dto.DisciplineDTO;
import com.savadanko.domain.requests.UpdateDisciplineRequest;
import com.savadanko.repository.DisciplineRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/disciplines")
public class DisciplineController {

    private final DisciplineRepository repo;

    public DisciplineController(DisciplineRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Список дисциплин")
    public List<DisciplineDTO> findAll() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Найти дисциплину по id")
    public DisciplineDTO findById(@PathVariable Long id) {
        Discipline d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found"));
        return toDto(d);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Создать дисциплину")
    public DisciplineDTO create(@Valid @RequestBody CreateDisciplineRequest req) {
        Discipline d = new Discipline();
        d.setName(req.name());
        d.setPracticeHours(req.practiceHours());
        d.setLabsCount(req.labsCount());
        Discipline saved = repo.save(d);
        return toDto(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Обновить дисциплину")
    public DisciplineDTO update(@PathVariable Long id, @Valid @RequestBody UpdateDisciplineRequest req) {
        Discipline d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found"));
        d.setName(req.name());
        d.setPracticeHours(req.practiceHours());
        d.setLabsCount(req.labsCount());
        return toDto(d);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Operation(summary = "Удалить дисциплину")
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found");
        }
        repo.deleteById(id);
    }

    private DisciplineDTO toDto(Discipline d) {
        return new DisciplineDTO(
                d.getId(),
                d.getName(),
                d.getPracticeHours(),
                d.getLabsCount()
        );
    }
}

