package es.iesquevedo.domain.mappers;

import es.iesquevedo.dao.model.HistoriaClinica;
import es.iesquevedo.domain.dto.HistoriaClinicaDTO;

import java.util.List;

public class HistoriaClinicaDTOMapper {
    public HistoriaClinicaDTO toDTO(HistoriaClinica historia) {
        return HistoriaClinicaDTO.builder()
                .id(historia.getId())
                .pacienteId(historia.getPacienteId())
                .medicoId(historia.getMedicoId())
                .fechaAdmision(historia.getFechaAdmision())
                .diagnostico(historia.getDiagnostico())
                .build();
    }

    public HistoriaClinica toEntity(HistoriaClinicaDTO dto) {
        return HistoriaClinica.builder()
                .id(dto.getId())
                .pacienteId(dto.getPacienteId())
                .medicoId(dto.getMedicoId())
                .fechaAdmision(dto.getFechaAdmision())
                .diagnostico(dto.getDiagnostico())
                .build();
    }

    public List<HistoriaClinicaDTO> toDTOList(List<HistoriaClinica> historias) {
        return historias.stream().map(this::toDTO).toList();
    }
}
