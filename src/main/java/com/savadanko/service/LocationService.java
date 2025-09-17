package com.savadanko.service;

import com.savadanko.domain.Location;
import com.savadanko.domain.requests.CreateLocationRequest;
import com.savadanko.domain.dto.LocationDTO;
import com.savadanko.domain.requests.UpdateLocationRequest;
import com.savadanko.repository.LocationRepository;
import com.savadanko.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository repo;

    public LocationService(LocationRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<LocationDTO> findAll() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationDTO findById(Long id) {
        Location loc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found"));
        return toDto(loc);
    }

    @Transactional
    public LocationDTO create(CreateLocationRequest req) {
        Location loc = new Location();
        loc.setName(req.name());
        loc.setX(req.x());
        loc.setY(req.y());
        Location saved = repo.save(loc);
        return toDto(saved);
    }

    @Transactional
    public LocationDTO update(Long id, UpdateLocationRequest req) {
        Location loc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found"));

        if (req.name() != null) loc.setName(req.name());
        if (req.x() != null)    loc.setX(req.x());
        if (req.y() != null)    loc.setY(req.y());

        return toDto(loc);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Location not found");
        }
        repo.deleteById(id);
    }

    private LocationDTO toDto(Location l) {
        return new LocationDTO(l.getId(), l.getName(), l.getX(), l.getY());
    }
}