package es.iesquevedo.ui;

import es.iesquevedo.domain.dto.HistoriaClinicaDTO;
import es.iesquevedo.domain.error.AppError;
import es.iesquevedo.domain.error.DatabaseError;
import es.iesquevedo.domain.services.HistoriaClinicaService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HistoriaClinicaUI {
    private final HistoriaClinicaService historiaService;
    private final Consola consola;

    @Inject
    public HistoriaClinicaUI(HistoriaClinicaService historiaService, Consola consola) {
        this.historiaService = historiaService;
        this.consola = consola;
    }

    /** Opción 6 */
    public void mostrarHistoria() {
        try {
            listarDePaciente("ID del paciente: ");
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("mostrarHistoria()", e);
        }
    }

    /** Opción 7 */
    public void add() {
        try {
            HistoriaClinicaDTO dto = HistoriaClinicaDTO.builder()
                    .pacienteId(consola.numero("ID del paciente: "))
                    .medicoId(consola.numero("ID del médico: "))
                    .diagnostico(consola.texto("Diagnóstico: "))
                    .fechaAdmision(consola.fecha("Fecha de admisión"))
                    .build();
            IO.println("Registro añadido. ID: " + historiaService.add(dto));
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("add()", e);
        }
    }

    /** Opción 8 */
    public void update() {
        try {
            if (!listarDePaciente("ID del paciente para mostrar sus registros: ")) return;
            HistoriaClinicaDTO dto = HistoriaClinicaDTO.builder()
                    .id(consola.numero("ID del registro a actualizar: "))
                    .medicoId(consola.numero("ID del médico: "))
                    .diagnostico(consola.texto("Diagnóstico: "))
                    .fechaAdmision(consola.fecha("Fecha de admisión"))
                    .build();
            IO.println(historiaService.update(dto) ? "Registro actualizado." : "No existe ese registro.");
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("update()", e);
        }
    }

    /** Opción 9 */
    public void delete() {
        try {
            if (!listarDePaciente("ID del paciente para mostrar sus registros: ")) return;
            Long id = consola.numero("ID del registro a borrar: ");
            IO.println(historiaService.delete(id) ? "Registro borrado." : "No existe ese registro.");
        } catch (DatabaseError e) {
            errorBD(e);
        } catch (Exception e) {
            errorCritico("delete()", e);
        }
    }

    // Pide un paciente, muestra sus registros y devuelve false si no tiene ninguno
    private boolean listarDePaciente(String mensaje) {
        Long pacienteId = consola.numero(mensaje);
        List<HistoriaClinicaDTO> lista = historiaService.getByPaciente(pacienteId);
        if (lista.isEmpty()) {
            IO.println("Este paciente no tiene registros de historia clínica.");
            return false;
        }
        lista.forEach(h -> IO.println("Registro ID: " + h.getId() + " | Paciente: " + h.getPacienteId()
                + " | Médico: " + h.getMedicoId() + " | Fecha: " + h.getFechaAdmision()
                + " | Diagnóstico: " + h.getDiagnostico()));
        return true;
    }

    private void errorBD(DatabaseError e) {
        log.error("Error de BD {}", e.getMessage());
        IO.println("Error: " + e.getMessage());
    }

    private void errorCritico(String metodo, Exception e) {
        log.error("Error inesperado en HistoriaClinicaUI.{}", metodo, e);
        throw new AppError("Error crítico en HistoriaClinicaUI." + metodo);
    }
}
