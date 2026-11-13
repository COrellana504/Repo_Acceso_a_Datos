
package es.iesquevedo.domain.mappers;

import es.iesquevedo.dao.model.*;
import es.iesquevedo.domain.dto.backup.*;

import java.util.ArrayList;
import java.util.List;

public class PacienteBackupMapper {

    public PacienteBackupDTO toBackupDTO(
            Paciente paciente,
            List<HistoriaClinica> historias,
            List<Receta> recetas,
            List<Cita> citas,
            List<Factura> facturas) {

        List<HistoriaBackupDTO> historiasDTO = new ArrayList<>();

        for (HistoriaClinica h : historias) {
            List<RecetaBackupDTO> recetasDTO = new ArrayList<>();

            for (Receta r : recetas) {
                if (r.getHistoriaId() != null
                        && r.getHistoriaId().equals(h.getId())) {
                    recetasDTO.add(RecetaBackupDTO.builder()
                            .id(r.getId())
                            .medicamento(r.getMedicamento())
                            .dosis(r.getDosis())
                            .build());
                }
            }

            historiasDTO.add(HistoriaBackupDTO.builder()
                    .id(h.getId())
                    .medicoId(h.getMedicoId())
                    .fechaAdmision(h.getFechaAdmision())
                    .diagnostico(h.getDiagnostico())
                    .recetas(recetasDTO)
                    .build());
        }

        List<CitaBackupDTO> citasDTO = new ArrayList<>();
        for (Cita c : citas) {
            citasDTO.add(CitaBackupDTO.builder()
                    .id(c.getId())
                    .medicoId(c.getMedicoId())
                    .fechaCita(c.getFechaCita() == null
                            ? null
                            : c.getFechaCita().toString())
                    .build());
        }

        List<FacturaBackupDTO> facturasDTO = new ArrayList<>();
        for (Factura f : facturas) {
            facturasDTO.add(FacturaBackupDTO.builder()
                    .id(f.getId())
                    .fechaPago(f.getFechaPago() == null
                            ? null : f.getFechaPago().toString())
                    .cantidad(f.getCantidad() == null
                            ? null : f.getCantidad().toPlainString())
                    .build());
        }

        return PacienteBackupDTO.builder()
                .id(paciente.getId())
                .nombre(paciente.getNombre())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .telefono(paciente.getTelefono())
                .historias(historiasDTO)
                .citas(citasDTO)
                .facturas(facturasDTO)
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
        List<HistoriaClinica> resultado = new ArrayList<>();

        if (dto.getHistorias() == null) return resultado;

        for (HistoriaBackupDTO h : dto.getHistorias()) {
            resultado.add(HistoriaClinica.builder()
                    .id(h.getId())
                    .pacienteId(dto.getId())
                    .medicoId(h.getMedicoId())
                    .fechaAdmision(h.getFechaAdmision())
                    .diagnostico(h.getDiagnostico())
                    .build());
        }

        return resultado;
    }

    public List<Receta> toRecetas(PacienteBackupDTO dto) {
        List<Receta> resultado = new ArrayList<>();

        if (dto.getHistorias() == null) return resultado;

        for (HistoriaBackupDTO h : dto.getHistorias()) {
            if (h.getRecetas() == null) continue;

            for (RecetaBackupDTO r : h.getRecetas()) {
                resultado.add(Receta.builder()
                        .id(r.getId())
                        .historiaId(h.getId())
                        .medicamento(r.getMedicamento())
                        .dosis(r.getDosis())
                        .build());
            }
        }

        return resultado;
    }

    public List<Cita> toCitas(PacienteBackupDTO dto) {
        List<Cita> resultado = new ArrayList<>();

        if (dto.getCitas() == null) return resultado;

        for (CitaBackupDTO c : dto.getCitas()) {
            resultado.add(Cita.builder()
                    .id(c.getId())
                    .pacienteId(dto.getId())
                    .medicoId(c.getMedicoId())
                    .fechaCita(c.getFechaCita() == null
                            ? null
                            : java.time.LocalDateTime.parse(c.getFechaCita()))
                    .build());
        }

        return resultado;
    }

    public List<Factura> toFacturas(PacienteBackupDTO dto) {
        List<Factura> resultado = new ArrayList<>();

        if (dto.getFacturas() == null) return resultado;

        for (FacturaBackupDTO f : dto.getFacturas()) {
            resultado.add(Factura.builder()
                    .id(f.getId())
                    .pacienteId(dto.getId())
                    .fechaPago(f.getFechaPago() == null
                            ? null
                            : java.time.LocalDate.parse(f.getFechaPago()))
                    .cantidad(f.getCantidad() == null
                            ? null
                            : new java.math.BigDecimal(f.getCantidad()))
                    .build());
        }

        return resultado;
    }
}
