package com.savadanko.repository;

import com.savadanko.domain.LabWork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabWorkRepository extends JpaRepository<LabWork, Long> {
    boolean existsByCoordinatesId(Long coordinatesId);
    boolean existsByDisciplineId(Long disciplineId);
    boolean existsByAuthorId(Long authorId);
}
