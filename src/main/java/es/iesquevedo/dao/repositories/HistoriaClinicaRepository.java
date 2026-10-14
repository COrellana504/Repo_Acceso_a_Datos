package es.iesquevedo.dao.repositories;

import es.iesquevedo.dao.model.HistoriaClinica;

import java.util.List;

public interface HistoriaClinicaRepository {
    List<HistoriaClinica> findByPacienteId(Long pacienteId);
    Long add(HistoriaClinica historia);
    boolean update(HistoriaClinica historia);
    boolean delete(Long id);
}
