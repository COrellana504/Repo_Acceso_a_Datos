
package es.iesquevedo.controller;

import es.iesquevedo.dao.repositories.spring.MedicoJdbcRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoJdbcRepository repository;

    public MedicoController(MedicoJdbcRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return repository.findAll().stream()
                .map(m -> Map.<String, Object>of(
                        "medico_id", m.getId(),
                        "nombre", m.getNombre(),
                        "especialidad", m.getEspecialidad(),
                        "telefono", m.getTelefono()
                ))
                .toList();
    }
}
