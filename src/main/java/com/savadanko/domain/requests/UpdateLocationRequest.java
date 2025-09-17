package com.savadanko.domain.requests;

public record UpdateLocationRequest(
        String name,
        Double x,
        Integer y
) {}
