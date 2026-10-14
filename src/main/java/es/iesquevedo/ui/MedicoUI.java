package es.iesquevedo.ui;

import es.iesquevedo.domain.dto.MedicoDTO;
import es.iesquevedo.domain.error.AppError;
import es.iesquevedo.domain.error.DatabaseError;
import es.iesquevedo.domain.services.MedicoService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MedicoUI {
    private final MedicoService medicoService;

    @Inject
    public MedicoUI(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    /** Opción 5 */
    public void getAll() {
        try {
            List<MedicoDTO> lista = medicoService.getAll();
            if (lista.isEmpty()) {
                IO.println("No hay médicos.");
                return;
            }
            lista.forEach(m -> IO.println("ID: " + m.getId() + " | Nombre: " + m.getNombre()
                    + " | Especialidad: " + m.getEspecialidad() + " | Teléfono: " + m.getTelefono()));
        } catch (DatabaseError e) {
            log.error("Error de BD {}", e.getMessage());
            IO.println("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado", e);
            throw new AppError("Error crítico en MedicoUI.getAll()");
        }
    }
}
