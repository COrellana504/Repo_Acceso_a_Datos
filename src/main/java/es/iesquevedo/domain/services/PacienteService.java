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
import es.iesquevedo.dao.model.Cita;
import es.iesquevedo.dao.model.Factura;
import es.iesquevedo.dao.repositories.CitaRepository;
import es.iesquevedo.dao.repositories.FacturaRepository;


public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final HistoriaClinicaRepository historiaRepository;
    private final PacienteDTOMapper pacienteDTOMapper;
    private final PacienteBackupMapper backupMapper;
    private final PacienteBackupService backupService;
    private final RecetaRepository recetaRepository;
    private final CitaRepository citaRepository;
    private final FacturaRepository facturaRepository;

    @Inject
    public PacienteService(
            PacienteRepository pacienteRepository,
            HistoriaClinicaRepository historiaRepository,
            RecetaRepository recetaRepository,
            CitaRepository citaRepository,
            FacturaRepository facturaRepository,
            PacienteDTOMapper pacienteDTOMapper,
            PacienteBackupMapper backupMapper,
            PacienteBackupService backupService) {

        this.pacienteRepository = pacienteRepository;
        this.historiaRepository = historiaRepository;
        this.recetaRepository = recetaRepository;
        this.citaRepository = citaRepository;
        this.facturaRepository = facturaRepository;
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
    public boolean tieneDatosRelacionados(Long id) {
        return !historiaRepository.findByPacienteId(id).isEmpty()
                || !recetaRepository.findByPacienteId(id).isEmpty()
                || !citaRepository.findByPacienteId(id).isEmpty()
                || !facturaRepository.findByPacienteId(id).isEmpty();
    }

    /** Si tiene historias: backup XML primero. Si el backup falla, NO se borra. */
    public boolean delete(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isEmpty()) return false;

        List<HistoriaClinica> historias =
                historiaRepository.findByPacienteId(id);

        List<Receta> recetas =
                recetaRepository.findByPacienteId(id);

        List<Cita> citas =
                citaRepository.findByPacienteId(id);

        List<Factura> facturas =
                facturaRepository.findByPacienteId(id);

        boolean tieneDatos =
                !historias.isEmpty()
                        || !recetas.isEmpty()
                        || !citas.isEmpty()
                        || !facturas.isEmpty();

        if (tieneDatos) {
            PacienteBackupDTO dto = backupMapper.toBackupDTO(
                    paciente.get(),
                    historias,
                    recetas,
                    citas,
                    facturas
            );

            backupService.backup(dto);
        }

        return pacienteRepository.delete(id);
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
                backupMapper.toRecetas(dto),
                backupMapper.toCitas(dto),
                backupMapper.toFacturas(dto)
        );
        return dto;
    }
}