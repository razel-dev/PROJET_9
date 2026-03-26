package com.medilabo.patient.mapper;

import com.medilabo.patient.domain.Patient;
import com.medilabo.patient.dto.PatientDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PatientMapper {

    PatientDto toDto(Patient entity);

    List<PatientDto> toDtos(List<Patient> entities);

    Patient toEntity(PatientDto dto);

    // Mise à jour partielle: les propriétés nulles du DTO sont ignorées
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Patient entity, PatientDto dto);
}
