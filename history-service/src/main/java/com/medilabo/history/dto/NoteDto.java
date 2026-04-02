package com.medilabo.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class NoteDto {
    private String id;

    @NotNull
    private Long patientId;

    private String author;

    @NotBlank
    private String content;

    private Instant createdAt;
}