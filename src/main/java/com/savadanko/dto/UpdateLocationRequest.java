package com.savadanko.dto;

public record UpdateLocationRequest(
        String name,
        Double x,
        Integer y
) {}
