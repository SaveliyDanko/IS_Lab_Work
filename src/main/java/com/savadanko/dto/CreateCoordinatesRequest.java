package com.savadanko.dto;

import jakarta.validation.constraints.NotNull;

public record CreateCoordinatesRequest(
        @NotNull Float x,
        float y
) {}
