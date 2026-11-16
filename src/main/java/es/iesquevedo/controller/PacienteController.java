
package es.iesquevedo.controller;

import es.iesquevedo.dao.model.Paciente;
import es.iesquevedo.dao.model.Usuario;
import es.iesquevedo.dao.repositories.spring.PacienteJdbcRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteJdbcRepository repository;

    public PacienteController(PacienteJdbcRepository repository) {
        this.repository = repository;
    }

    // El cliente espera: id, nombre, fechaNacimiento y telefono.
    public record PacienteRespuesta(
            Long id,
            String nombre,
            LocalDate fechaNacimiento,
            String telefono
    ) {}

    // El cliente envía username y password junto a los datos del paciente.
    public record PacientePeticion(
            String nombre,
            LocalDate fechaNacimiento,
            String telefono,
            String username,
            String password
    ) {}

    @GetMapping
    public List<PacienteRespuesta> listar() {
        return repository.findAll().stream()
                .map(p -> new PacienteRespuesta(
                        p.getId(),
                        p.getNombre(),
                        p.getFechaNacimiento(),
                        p.getTelefono()
                ))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PacientePeticion peticion) {
        try {
            Paciente paciente = Paciente.builder()
                    .nombre(peticion.nombre())
                    .fechaNacimiento(peticion.fechaNacimiento())
                    .telefono(peticion.telefono())
                    .build();

            Usuario usuario = Usuario.builder()
                    .username(peticion.username())
                    .password(peticion.password())
                    .build();

            Long id = repository.add(paciente, usuario);
            return ResponseEntity.ok(id);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409)
                    .body("El nombre de usuario ya existe o los datos no son válidos.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable Long id,
            @RequestBody PacientePeticion peticion) {

        Paciente paciente = Paciente.builder()
                .id(id)
                .nombre(peticion.nombre())
                .fechaNacimiento(peticion.fechaNacimiento())
                .telefono(peticion.telefono())
                .build();

        if (repository.update(paciente)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirm) {

        if (repository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (repository.tieneHistorias(id) && !confirm) {
            return ResponseEntity.status(409)
                    .body("Este paciente tiene historias clínicas. ¿Seguro que quieres eliminarlo?");
        }

        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
