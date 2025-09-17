package com.savadanko.domain.requests;

public record UpdateDisciplineRequest(
        String name,
        Long practiceHours,
        Long labsCount
) {}
