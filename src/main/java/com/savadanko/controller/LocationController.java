package com.savadanko.controller;

import com.savadanko.domain.Location;
import com.savadanko.domain.requests.CreateLocationRequest;
import com.savadanko.domain.dto.LocationDTO;
import com.savadanko.domain.requests.UpdateLocationRequest;
import com.savadanko.repository.LocationRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepo;

    public LocationController(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @GetMapping
    @Operation(summary = "Список всех локаций (DTO)")
    public List<LocationDTO> findAll() {
        return locationRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти локацию по id")
    public LocationDTO findById(@PathVariable Long id) {
        Location loc = locationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        return toDto(loc);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Создать локацию (без привязки к пользователю)")
    public LocationDTO create(@Valid @RequestBody CreateLocationRequest req) {
        Location loc = new Location();
        loc.setName(req.name());
        loc.setX(req.x());
        loc.setY(req.y());
        Location saved = locationRepo.save(loc);
        return toDto(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Обновить локацию (только имя/координаты)")
    public LocationDTO update(@PathVariable Long id, @Valid @RequestBody UpdateLocationRequest req) {
        Location loc = locationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));

        if (req.name() != null) loc.setName(req.name());
        if (req.x() != null)    loc.setX(req.x());
        if (req.y() != null)    loc.setY(req.y());

        return toDto(loc);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Operation(summary = "Удалить локацию по id")
    public void delete(@PathVariable Long id) {
        if (!locationRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }
        locationRepo.deleteById(id);
    }

    private LocationDTO toDto(Location l) {
        return new LocationDTO(l.getId(), l.getName(), l.getX(), l.getY());
    }
}
