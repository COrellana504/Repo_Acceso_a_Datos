package es.iesquevedo.dao.repositories;

import es.iesquevedo.dao.model.Factura;
import java.util.List;

public interface FacturaRepository {

    List<Factura> findByPacienteId(Long pacienteId);

}