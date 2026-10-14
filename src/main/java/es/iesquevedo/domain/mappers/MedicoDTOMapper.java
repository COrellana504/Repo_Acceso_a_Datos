package es.iesquevedo.domain.mappers;

import es.iesquevedo.dao.model.Medico;
import es.iesquevedo.domain.dto.MedicoDTO;

import java.util.List;

public class MedicoDTOMapper {
    public MedicoDTO toDTO(Medico medico) {
        return MedicoDTO.builder()
                .id(medico.getId())
                .nombre(medico.getNombre())
                .especialidad(medico.getEspecialidad())
                .telefono(medico.getTelefono())
                .build();
    }

    public List<MedicoDTO> toDTOList(List<Medico> medicos) {
        return medicos.stream().map(this::toDTO).toList();
    }
}
