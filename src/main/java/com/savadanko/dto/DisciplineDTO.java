package com.savadanko.dto;

public record DisciplineDTO(
        Long id,
        String name,
        Long practiceHours,
        Long labsCount
) {}
