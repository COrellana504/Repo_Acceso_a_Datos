package es.iesquevedo.domain.services;

import es.iesquevedo.common.Constantes;
import es.iesquevedo.domain.dto.backup.PacienteBackupDTO;
import es.iesquevedo.domain.error.BackupError;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
public class PacienteBackupService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Path directorio = Path.of(Constantes.BACKUP_PACIENTES_DIR);
    private final JAXBContext context;

    public PacienteBackupService() {
        try {
            this.context = JAXBContext.newInstance(PacienteBackupDTO.class);
        } catch (JAXBException e) {
            throw new BackupError("No se pudo inicializar JAXB", e);
        }
    }

    /** Escribe backups/pacientes/paciente_{id}_{fecha}.xml. Si falla, no deja nada a medias. */
    public Path backup(PacienteBackupDTO dto) {
        Path fichero = directorio.resolve(
                "paciente_" + dto.getId() + "_" + LocalDateTime.now().format(FORMATO_FECHA) + ".xml");
        try {
            Files.createDirectories(directorio);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(dto, fichero.toFile());
            log.info("Copia de seguridad creada: {}", fichero);
            return fichero;
        } catch (JAXBException | IOException e) {
            log.error("Error creando la copia de seguridad: {}", e.getMessage());
            borrarSilencioso(fichero);
            throw new BackupError("No se pudo crear la copia de seguridad del paciente " + dto.getId(), e);
        }
    }

    /** Lee la copia más reciente del paciente. */
    public PacienteBackupDTO read(Long pacienteId) {
        Path fichero = buscarMasReciente(pacienteId);
        try {
            return (PacienteBackupDTO) context.createUnmarshaller().unmarshal(fichero.toFile());
        } catch (JAXBException | ClassCastException e) {
            log.error("Error leyendo {}: {}", fichero, e.getMessage());
            throw new BackupError("El fichero de copia de seguridad no es válido: " + fichero.getFileName(), e);
        }
    }

    public void borrarSilencioso(Path fichero) {
        try {
            Files.deleteIfExists(fichero);
        } catch (IOException e) {
            log.warn("No se pudo borrar {}: {}", fichero, e.getMessage());
        }
    }

    private Path buscarMasReciente(Long pacienteId) {
        BackupError noExiste = new BackupError("No hay ninguna copia de seguridad del paciente " + pacienteId);
        if (!Files.isDirectory(directorio)) throw noExiste;

        String prefijo = "paciente_" + pacienteId + "_";
        try (Stream<Path> ficheros = Files.list(directorio)) {
            return ficheros
                    .filter(f -> f.getFileName().toString().startsWith(prefijo)
                            && f.getFileName().toString().endsWith(".xml"))
                    .max(Comparator.comparing(f -> f.getFileName().toString()))
                    .orElseThrow(() -> noExiste);
        } catch (IOException e) {
            throw new BackupError("No se pudo leer el directorio de copias de seguridad", e);
        }
    }
}
