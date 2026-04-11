package com.medilabo.front.dto;

import java.time.Instant;

public record NoteDto(
    String id,
    Long patientId,
    String author,
    String content,
    Instant createdAt
) {}