package com.medilabo.history.mapper;

import com.medilabo.history.domain.Note;
import com.medilabo.history.dto.NoteDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {
    NoteDto toDto(Note note);
    List<NoteDto> toDtos(List<Note> notes);
    Note toEntity(NoteDto dto);
}