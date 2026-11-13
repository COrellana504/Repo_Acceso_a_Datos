package es.iesquevedo.domain.mappers;

import es.iesquevedo.dao.model.HistoriaClinica;
import es.iesquevedo.dao.model.Paciente;
import es.iesquevedo.dao.model.Receta;
import es.iesquevedo.domain.dto.backup.HistoriaBackupDTO;
import es.iesquevedo.domain.dto.backup.PacienteBackupDTO;
import es.iesquevedo.domain.dto.backup.RecetaBackupDTO;

import java.util.ArrayList;
import java.util.List;

public class PacienteBackupMapper {

    /** Entidades -> DTO de backup. El usuario del paciente se deja fuera a propósito. */
    public PacienteBackupDTO toBackupDTO(Paciente paciente, List<HistoriaClinica> historias, List<Receta> recetas) {
        List<HistoriaBackupDTO> historiasDTO = new ArrayList<>();
        historias.forEach(h -> {
            List<RecetaBackupDTO> recetasDTO = new ArrayList<>();
            recetas.stream()
                    .filter(r -> r.getHistoriaId().equals(h.getId()))
                    .forEach(r -> recetasDTO.add(RecetaBackupDTO.builder()
                            .id(r.getId())
                            .medicamento(r.getMedicamento())
                            .dosis(r.getDosis())
                            .build()));

            historiasDTO.add(HistoriaBackupDTO.builder()
                    .id(h.getId())
                    .medicoId(h.getMedicoId())
                    .fechaAdmision(h.getFechaAdmision())
                    .diagnostico(h.getDiagnostico())
                    .recetas(recetasDTO)
                    .build());
        });

        return PacienteBackupDTO.builder()
                .id(paciente.getId())
                .nombre(paciente.getNombre())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .telefono(paciente.getTelefono())
                .historias(historiasDTO)
                .build();
    }

    public Paciente toPaciente(PacienteBackupDTO dto) {
        return Paciente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .fechaNacimiento(dto.getFechaNacimiento())
                .telefono(dto.getTelefono())
                .build();
    }

    public List<HistoriaClinica> toHistorias(PacienteBackupDTO dto) {
        List<HistoriaClinica> historias = new ArrayList<>();
        if (dto.getHistorias() == null) return historias;
        dto.getHistorias().forEach(h -> historias.add(HistoriaClinica.builder()
                .id(h.getId())
                .pacienteId(dto.getId())
                .medicoId(h.getMedicoId())
                .fechaAdmision(h.getFechaAdmision())
                .diagnostico(h.getDiagnostico())
                .build()));
        return historias;
    }

    /** Todas las recetas de todas las historias del backup. */
    public List<Receta> toRecetas(PacienteBackupDTO dto) {
        List<Receta> recetas = new ArrayList<>();
        if (dto.getHistorias() == null) return recetas;
        dto.getHistorias().forEach(h -> {
            if (h.getRecetas() == null) return;
            h.getRecetas().forEach(r -> recetas.add(Receta.builder()
                    .id(r.getId())
                    .historiaId(h.getId())
                    .medicamento(r.getMedicamento())
                    .dosis(r.getDosis())
                    .build()));
        });
        return recetas;
    }
}