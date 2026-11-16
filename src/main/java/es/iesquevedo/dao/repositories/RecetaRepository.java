package es.iesquevedo.dao.repositories;

import es.iesquevedo.dao.model.Receta;

import java.util.List;

public interface RecetaRepository {
    /** Todas las recetas de todas las historias de un paciente. */
    List<Receta> findByPacienteId(Long pacienteId);
}