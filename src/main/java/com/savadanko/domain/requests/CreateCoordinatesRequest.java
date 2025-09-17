package com.savadanko.domain.requests;

import jakarta.validation.constraints.NotNull;

public record CreateCoordinatesRequest(
        @NotNull Float x,
        float y
) {}
