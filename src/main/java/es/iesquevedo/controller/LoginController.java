
package es.iesquevedo.controller;

import es.iesquevedo.dao.repositories.spring.UsuarioJdbcRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UsuarioJdbcRepository repository;

    public LoginController(UsuarioJdbcRepository repository) {
        this.repository = repository;
    }

    public record LoginPeticion(String username, String password) {}

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> login(@RequestBody LoginPeticion peticion) {
        boolean correcto = repository.login(
                peticion.username(),
                peticion.password()
        );

        return ResponseEntity.ok(Boolean.toString(correcto));
    }
}
