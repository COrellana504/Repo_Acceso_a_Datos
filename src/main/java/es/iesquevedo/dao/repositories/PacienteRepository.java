package es.iesquevedo.dao.repositories;

import es.iesquevedo.dao.model.HistoriaClinica;
import es.iesquevedo.dao.model.Paciente;
import es.iesquevedo.dao.model.Receta;
import es.iesquevedo.dao.model.Cita;
import es.iesquevedo.dao.model.Factura;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository {

    List<Paciente> findAll();
    Optional<Paciente> findById(Long id);
    Long add(Paciente paciente);
    boolean update(Paciente paciente);
    boolean delete(Long id);
    void restore(
            Paciente paciente,
            List<HistoriaClinica> historias,
            List<Receta> recetas,
            List<Cita> citas,
            List<Factura> facturas
    );
}