package com.medilabo.history.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notes")
public class Note {

    @Id
    private String id;

    @Indexed
    private Long patientId;

    private String author;

    private String content;

    private Instant createdAt;
}