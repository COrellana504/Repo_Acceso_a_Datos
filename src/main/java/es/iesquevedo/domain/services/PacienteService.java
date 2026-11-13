package es.iesquevedo.domain.services;

import es.iesquevedo.dao.model.HistoriaClinica;
import es.iesquevedo.dao.model.Paciente;
import es.iesquevedo.dao.repositories.HistoriaClinicaRepository;
import es.iesquevedo.dao.repositories.PacienteRepository;
import es.iesquevedo.domain.dto.PacienteDTO;
import es.iesquevedo.domain.dto.PacienteDTOAlta;
import es.iesquevedo.domain.dto.backup.PacienteBackupDTO;
import es.iesquevedo.domain.error.BackupError;
import es.iesquevedo.domain.error.DatabaseError;
import es.iesquevedo.domain.error.PacienteDuplicadoError;
import es.iesquevedo.domain.mappers.PacienteBackupMapper;
import es.iesquevedo.domain.mappers.PacienteDTOMapper;
import jakarta.inject.Inject;
import es.iesquevedo.dao.model.Receta;
import es.iesquevedo.dao.repositories.RecetaRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final HistoriaClinicaRepository historiaRepository;
    private final PacienteDTOMapper pacienteDTOMapper;
    private final PacienteBackupMapper backupMapper;
    private final PacienteBackupService backupService;
    private final RecetaRepository recetaRepository;
    @Inject
    public PacienteService(PacienteRepository pacienteRepository,
                           HistoriaClinicaRepository historiaRepository,
                           RecetaRepository recetaRepository,
                           PacienteDTOMapper pacienteDTOMapper,
                           PacienteBackupMapper backupMapper,
                           PacienteBackupService backupService) {
        this.pacienteRepository = pacienteRepository;
        this.historiaRepository = historiaRepository;
        this.recetaRepository = recetaRepository;
        this.pacienteDTOMapper = pacienteDTOMapper;
        this.backupMapper = backupMapper;
        this.backupService = backupService;
    }

    public List<PacienteDTO> getAll() {
        return pacienteDTOMapper.toDTOList(pacienteRepository.findAll());
    }

    public Long add(PacienteDTOAlta pacienteDTO) {
        return pacienteRepository.add(pacienteDTOMapper.toEntity(pacienteDTO));
    }

    public boolean update(PacienteDTO pacienteDTO) {
        return pacienteRepository.update(pacienteDTOMapper.toEntity(pacienteDTO));
    }

    /** La UI lo usa para saber si tiene que pedir confirmación. */
    public boolean tieneHistorias(Long id) {
        return !historiaRepository.findByPacienteId(id).isEmpty();
    }

    /** Si tiene historias: backup XML primero. Si el backup falla, NO se borra. */
    public boolean delete(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isEmpty()) return false;

        List<HistoriaClinica> historias = historiaRepository.findByPacienteId(id);

        Path backup = null;
        if (!historias.isEmpty()) {
            List<Receta> recetas = recetaRepository.findByPacienteId(id);
            backup = backupService.backup(backupMapper.toBackupDTO(paciente.get(), historias, recetas));
        }

        try {
            boolean borrado = pacienteRepository.delete(id);
            if (!borrado && backup != null) backupService.borrarSilencioso(backup);
            return borrado;
        } catch (DatabaseError e) {
            if (backup != null) backupService.borrarSilencioso(backup);
            throw e;
        }
    }

    /** Lee el XML más reciente del paciente y lo reinserta en la BD. */
    public PacienteBackupDTO restore(Long id) {
        PacienteBackupDTO dto = backupService.read(id);
        if (dto.getId() == null || !dto.getId().equals(id)) {
            throw new BackupError("La copia de seguridad no corresponde al paciente " + id);
        }
        if (pacienteRepository.findById(id).isPresent()) {
            throw new PacienteDuplicadoError();
        }
        pacienteRepository.restore(
                backupMapper.toPaciente(dto),
                backupMapper.toHistorias(dto),
                backupMapper.toRecetas(dto));
        return dto;
    }
}