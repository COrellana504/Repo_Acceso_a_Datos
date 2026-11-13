package es.iesquevedo.ui;

import es.iesquevedo.domain.dto.PacienteDTO;
import es.iesquevedo.domain.dto.PacienteDTOAlta;
import es.iesquevedo.domain.error.AppError;
import es.iesquevedo.domain.error.DatabaseError;
import es.iesquevedo.domain.services.PacienteService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import es.iesquevedo.domain.dto.backup.PacienteBackupDTO;
import es.iesquevedo.domain.error.BackupError;
import java.util.List;

@Slf4j
public class PacienteUI {
    private final PacienteService pacienteService;
    private final Consola consola;

    @Inject
    public PacienteUI(PacienteService pacienteService, Consola consola) {
        this.pacienteService = pacienteService;
        this.consola = consola;
    }

    /** Opción 1 */
    public void getAll() {
        try {
            listar();
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("getAll()", e);
        }
    }

    /** Opción 2 */
    public void save() {
        try {
            IO.println("--- AÑADIR PACIENTE ---");
            PacienteDTOAlta dto = PacienteDTOAlta.builder()
                    .nombre(consola.texto("Nombre: "))
                    .fechaNacimiento(consola.fecha("Fecha de nacimiento"))
                    .telefono(consola.texto("Teléfono: "))
                    .username(consola.texto("Usuario: "))
                    .password(consola.texto("Contraseña: "))
                    .build();
            IO.println("Paciente añadido. ID: " + pacienteService.add(dto));
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("save()", e);
        }
    }

    /** Opción 3 */
    public void update() {
        try {
            if (!listar()) return;
            PacienteDTO dto = PacienteDTO.builder()
                    .id(consola.numero("ID del paciente a actualizar: "))
                    .nombre(consola.texto("Nuevo nombre: "))
                    .fechaNacimiento(consola.fecha("Nueva fecha de nacimiento"))
                    .telefono(consola.texto("Nuevo teléfono: "))
                    .build();
            IO.println(pacienteService.update(dto) ? "Paciente actualizado." : "No existe ese paciente.");
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("update()", e);
        }
    }

    /** Opción 4 */
    /** Opción 4 */
    public void delete() {
        try {
            if (!listar()) return;
            Long id = consola.numero("ID del paciente a borrar: ");
            if (pacienteService.tieneHistorias(id)) {
                IO.println("Este paciente tiene historias clínicas. Se guardará una copia de seguridad "
                        + "en XML (sin credenciales) y se borrarán el paciente y todas sus historias.");
                if (!consola.confirmar("¿Desea continuar?")) {
                    IO.println("Operación cancelada.");
                    return;
                }
            }
            IO.println(pacienteService.delete(id) ? "Paciente borrado." : "No existe ese paciente.");
        } catch (BackupError e) {
            log.error("Error de backup {}", e.getMessage());
            IO.println("Error: " + e.getMessage() + ". El paciente NO ha sido borrado.");
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("delete()", e);
        }
    }

    /** Opción 10 */
    public void restore() {
        try {
            Long id = consola.numero("ID del paciente a restaurar: ");
            PacienteBackupDTO restaurado = pacienteService.restore(id);
            int historias = restaurado.getHistorias() == null ? 0 : restaurado.getHistorias().size();
            IO.println("Paciente restaurado: " + restaurado.getNombre()
                    + " (ID " + restaurado.getId() + ") con " + historias + " historia(s) clínica(s).");
        } catch (BackupError e) {
            log.error("Error de backup {}", e.getMessage());
            IO.println("Error: " + e.getMessage());
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("restore()", e);
        }
    }

    // Muestra la lista y devuelve false si no hay pacientes
    private boolean listar() {
        List<PacienteDTO> lista = pacienteService.getAll();
        if (lista.isEmpty()) {
            IO.println("No hay pacientes.");
            return false;
        }
        lista.forEach(p -> IO.println("ID: " + p.getId() + " | Nombre: " + p.getNombre()
                + " | Nacimiento: " + p.getFechaNacimiento() + " | Teléfono: " + p.getTelefono()));
        return true;
    }

    private void errorBD(DatabaseError e) {
        log.error("Error de BD {}", e.getMessage());
        IO.println("Error: " + e.getMessage());
    }

    private void errorCritico(String metodo, Exception e) {
        log.error("Error inesperado en PacienteUI.{}", metodo, e);
        throw new AppError("Error crítico en PacienteUI." + metodo);
    }
}
