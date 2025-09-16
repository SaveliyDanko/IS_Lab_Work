package com.savadanko.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDisciplineRequest(
        @NotBlank String name,
        @NotNull Long practiceHours,
        @NotNull Long labsCount
) {}
