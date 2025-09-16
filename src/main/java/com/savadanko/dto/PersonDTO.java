package com.savadanko.dto;

import com.savadanko.domain.Color;
import com.savadanko.domain.Country;

public record PersonDTO(
        Long id,
        String name,
        Color eyeColor,
        Color hairColor,
        Double weight,
        Country nationality,
        Long locationId,
        String locationName
) {}
