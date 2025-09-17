package com.savadanko.controller;

import com.savadanko.domain.Location;
import com.savadanko.domain.Person;
import com.savadanko.domain.dto.LocationDTO;
import com.savadanko.domain.dto.PersonFullDTO;
import com.savadanko.domain.requests.CreatePersonRequest;
import com.savadanko.domain.dto.PersonDTO;
import com.savadanko.domain.requests.UpdatePersonRequest;
import com.savadanko.repository.LocationRepository;
import com.savadanko.repository.PersonRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonRepository perRepo;
    private final LocationRepository locRepo;

    public PersonController(PersonRepository perRepo, LocationRepository locRepo) {
        this.perRepo = perRepo;
        this.locRepo = locRepo;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Список персон")
    public List<PersonDTO> findAll() {
        return perRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/full")
    @Transactional(readOnly = true)
    @Operation(summary = "Список персон (полная структура с локацией)")
    public List<PersonFullDTO> findAllFull() {
        return perRepo.findAll().stream()
                .map(this::toFullDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Найти персону по id")
    public PersonDTO findById(@PathVariable Long id) {
        Person p = perRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        return toDto(p);
    }

    @GetMapping("/{id}/full")
    @Transactional(readOnly = true)
    @Operation(summary = "Персона по id (полная структура с локацией)")
    public PersonFullDTO findFullById(@PathVariable Long id) {
        Person p = perRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        return toFullDto(p);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Создать персону")
    public PersonDTO create(@Valid @RequestBody CreatePersonRequest req) {
        Person p = new Person();
        p.setName(req.name());
        p.setEyeColor(req.eyeColor());
        p.setHairColor(req.hairColor());
        p.setWeight(req.weight());
        p.setNationality(req.nationality());

        if (req.locationId() != null) {
            Location loc = locRepo.findById(req.locationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location not found"));
            p.setLocation(loc);
        }

        Person saved = perRepo.save(p);
        return toDto(saved);
    }


    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Обновить персону")
    public PersonDTO update(@PathVariable Long id, @Valid @RequestBody UpdatePersonRequest req) {
        Person p = perRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        if (req.name() != null)       p.setName(req.name());
        if (req.eyeColor() != null)   p.setEyeColor(req.eyeColor());
        if (req.hairColor() != null)  p.setHairColor(req.hairColor());
        if (req.weight() != null)     p.setWeight(req.weight());
        if (req.nationality() != null)p.setNationality(req.nationality());
        if (req.locationId() != null) {
            Location loc = locRepo.findById(req.locationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location not found"));
            p.setLocation(loc);
        }

        return toDto(p);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Operation(summary = "Удалить персону")
    public void delete(@PathVariable Long id) {
        if (!perRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }
        perRepo.deleteById(id);
    }

    private PersonDTO toDto(Person p) {
        Long locId = null;
        String locName = null;
        Location loc = p.getLocation();
        if (loc != null) {
            locId = loc.getId();
            locName = loc.getName();
        }
        return new PersonDTO(
                p.getId(),
                p.getName(),
                p.getEyeColor(),
                p.getHairColor(),
                p.getWeight(),
                p.getNationality(),
                locId,
                locName
        );
    }

    private PersonFullDTO toFullDto(Person p) {
        LocationDTO lDto = null;
        Location loc = p.getLocation();
        if (loc != null) {
            lDto = new LocationDTO(loc.getId(), loc.getName(), loc.getX(), loc.getY());
        }
        return new PersonFullDTO(
                p.getId(),
                p.getName(),
                p.getEyeColor(),
                p.getHairColor(),
                p.getWeight(),
                p.getNationality(),
                lDto
        );
    }
}
