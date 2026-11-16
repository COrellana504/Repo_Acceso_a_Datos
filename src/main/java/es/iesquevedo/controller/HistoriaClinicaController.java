
package es.iesquevedo.controller;

import es.iesquevedo.dao.model.HistoriaClinica;
import es.iesquevedo.dao.repositories.spring.HistoriaClinicaJdbcRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class HistoriaClinicaController {

    private final HistoriaClinicaJdbcRepository repository;

    public HistoriaClinicaController(HistoriaClinicaJdbcRepository repository) {
        this.repository = repository;
    }

    // GET /pacientes/{id}/historias
    @GetMapping("/pacientes/{id}/historias")
    public List<HistoriaClinica> listarPorPaciente(@PathVariable Long id) {
        return repository.findByPacienteId(id);
    }

    // POST /historias
    @PostMapping("/historias")
    public ResponseEntity<Long> crear(@RequestBody HistoriaClinica historia) {
        Long id = repository.add(historia);
        return ResponseEntity.ok(id);
    }

    // PUT /historias
    @PutMapping("/historias")
    public ResponseEntity<Void> actualizar(@RequestBody HistoriaClinica historia) {
        if (repository.update(historia)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /historias/{id}
    @DeleteMapping("/historias/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (repository.delete(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
