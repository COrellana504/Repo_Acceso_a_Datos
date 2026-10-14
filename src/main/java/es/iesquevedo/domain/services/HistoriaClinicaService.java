package es.iesquevedo.domain.services;

import es.iesquevedo.dao.repositories.HistoriaClinicaRepository;
import es.iesquevedo.domain.dto.HistoriaClinicaDTO;
import es.iesquevedo.domain.mappers.HistoriaClinicaDTOMapper;
import jakarta.inject.Inject;

import java.util.List;

public class HistoriaClinicaService {
    private final HistoriaClinicaRepository historiaRepository;
    private final HistoriaClinicaDTOMapper historiaDTOMapper;

    @Inject
    public HistoriaClinicaService(HistoriaClinicaRepository historiaRepository, HistoriaClinicaDTOMapper historiaDTOMapper) {
        this.historiaRepository = historiaRepository;
        this.historiaDTOMapper = historiaDTOMapper;
    }

    public List<HistoriaClinicaDTO> getByPaciente(Long pacienteId) {
        return historiaDTOMapper.toDTOList(historiaRepository.findByPacienteId(pacienteId));
    }

    public Long add(HistoriaClinicaDTO dto) {
        return historiaRepository.add(historiaDTOMapper.toEntity(dto));
    }

    public boolean update(HistoriaClinicaDTO dto) {
        return historiaRepository.update(historiaDTOMapper.toEntity(dto));
    }

    public boolean delete(Long id) {
        return historiaRepository.delete(id);
    }
}
