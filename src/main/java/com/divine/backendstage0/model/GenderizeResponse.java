package com.divine.backendstage0.model;

public record GenderizeResponse(
        String name,
        String gender,
        double probability,
        int count
) {}
