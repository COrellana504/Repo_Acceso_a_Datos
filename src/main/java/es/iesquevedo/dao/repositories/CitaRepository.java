package es.iesquevedo.dao.repositories;

import es.iesquevedo.dao.model.Cita;
import java.util.List;

public interface CitaRepository {

    List<Cita> findByPacienteId(Long pacienteId);

}