package com.savadanko.controller;

import com.savadanko.domain.*;
import com.savadanko.domain.dto.*;
import com.savadanko.domain.requests.CreateLabWorkRequest;
import com.savadanko.domain.requests.UpdateLabWorkRequest;
import com.savadanko.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/labworks")
public class LabWorkController {

    private final LabWorkRepository labWorkRepo;
    private final CoordinatesRepository coordinatesRepo;
    private final PersonRepository personRepo;
    private final DisciplineRepository disciplineRepo;

    public LabWorkController(
            LabWorkRepository labWorkRepo,
            CoordinatesRepository coordinatesRepo,
            PersonRepository personRepo,
            DisciplineRepository disciplineRepo
    ) {
        this.labWorkRepo = labWorkRepo;
        this.coordinatesRepo = coordinatesRepo;
        this.personRepo = personRepo;
        this.disciplineRepo = disciplineRepo;
    }

    @GetMapping
    @Operation(summary = "Список всех лабораторных работ")
    @Transactional(readOnly = true)
    public List<LabWorkDTO> findAll() {
        return labWorkRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/full")
    @Operation(summary = "Список всех лабораторных работ (полная древовидная структура)")
    @Transactional(readOnly = true)
    public List<LabWorkFullDTO> findAllFull() {
        return labWorkRepo.findAll().stream()
                .map(this::toFullDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти лабораторную работу по id")
    @Transactional(readOnly = true)
    public LabWorkDTO findById(@PathVariable Long id) {
        LabWork lw = labWorkRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LabWork not found"));
        return toDto(lw);
    }

    @GetMapping("/{id}/full")
    @Operation(summary = "Лабораторная работа по id (полная древовидная структура)")
    @Transactional(readOnly = true)
    public LabWorkFullDTO findFullById(@PathVariable Long id) {
        LabWork lw = labWorkRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LabWork not found"));
        return toFullDto(lw);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Создать лабораторную работу")
    public LabWorkDTO create(@Valid @RequestBody CreateLabWorkRequest req) {
        Coordinates coordinates = coordinatesRepo.findById(req.coordinatesId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordinates not found"));

        LabWork lw = new LabWork();
        lw.setName(req.name());
        lw.setCoordinates(coordinates);
        lw.setDescription(req.description());
        lw.setDifficulty(req.difficulty());
        lw.setMinimalPoint(req.minimalPoint());

        if (req.authorId() != null) {
            Person author = personRepo.findById(req.authorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author not found"));
            lw.setAuthor(author);
        }

        if (req.disciplineId() != null) {
            Discipline discipline = disciplineRepo.findById(req.disciplineId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discipline not found"));
            lw.setDiscipline(discipline);
        }

        LabWork saved = labWorkRepo.save(lw);
        return toDto(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Обновить лабораторную работу")
    public LabWorkDTO update(@PathVariable Long id, @Valid @RequestBody UpdateLabWorkRequest req) {
        LabWork lw = labWorkRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LabWork not found"));

        if (req.name() != null)        lw.setName(req.name());
        if (req.description() != null)  lw.setDescription(req.description());
        if (req.difficulty() != null)   lw.setDifficulty(req.difficulty());
        if (req.minimalPoint() != null) lw.setMinimalPoint(req.minimalPoint());

        if (req.coordinatesId() != null) {
            Coordinates coordinates = coordinatesRepo.findById(req.coordinatesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordinates not found"));
            lw.setCoordinates(coordinates);
        }

        if (req.authorId() != null) {
            Person author = personRepo.findById(req.authorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author not found"));
            lw.setAuthor(author);
        }

        if (req.disciplineId() != null) {
            Discipline discipline = disciplineRepo.findById(req.disciplineId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discipline not found"));
            lw.setDiscipline(discipline);
        }

        return toDto(lw);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Operation(summary = "Удалить лабораторную работу")
    public void delete(@PathVariable Long id) {
        if (!labWorkRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LabWork not found");
        }
        labWorkRepo.deleteById(id);
    }

    private LabWorkDTO toDto(LabWork lw) {
        Long coordinatesId = lw.getCoordinates() != null ? lw.getCoordinates().getId() : null;
        Long authorId = null;
        String authorName = null;
        if (lw.getAuthor() != null) {
            authorId = lw.getAuthor().getId();
            authorName = lw.getAuthor().getName();
        }
        Long disciplineId = null;
        String disciplineName = null;
        if (lw.getDiscipline() != null) {
            disciplineId = lw.getDiscipline().getId();
            disciplineName = lw.getDiscipline().getName();
        }
        return new LabWorkDTO(
                lw.getId() != null ? lw.getId() : null,
                lw.getName(),
                lw.getDescription(),
                lw.getDifficulty(),
                lw.getMinimalPoint(),
                lw.getCreationDate(),
                coordinatesId,
                authorId,
                authorName,
                disciplineId,
                disciplineName
        );
    }

    private LabWorkFullDTO toFullDto(LabWork lw) {
        CoordinatesDTO coord = null;
        if (lw.getCoordinates() != null) {
            var c = lw.getCoordinates();
            // если у тебя y в сущности primitive float — автоупаковка сработает
            coord = new CoordinatesDTO(c.getId(), c.getX(), c.getY());
        }

        DisciplineDTO disc = null;
        if (lw.getDiscipline() != null) {
            var d = lw.getDiscipline();
            disc = new DisciplineDTO(d.getId(), d.getName(), d.getPracticeHours(), d.getLabsCount());
        }

        PersonFullDTO author = null;
        if (lw.getAuthor() != null) {
            var p = lw.getAuthor();
            LocationDTO locDto = null;
            if (p.getLocation() != null) {
                var l = p.getLocation();
                locDto = new LocationDTO(l.getId(), l.getName(), l.getX(), l.getY());
            }
            author = new PersonFullDTO(
                    p.getId(),
                    p.getName(),
                    p.getEyeColor(),
                    p.getHairColor(),
                    p.getWeight(),
                    p.getNationality(),
                    locDto
            );
        }

        return new LabWorkFullDTO(
                lw.getId(),
                lw.getName(),
                lw.getDescription(),
                lw.getDifficulty(),
                lw.getMinimalPoint(),
                lw.getCreationDate(),
                coord,
                author,
                disc
        );
    }
}

