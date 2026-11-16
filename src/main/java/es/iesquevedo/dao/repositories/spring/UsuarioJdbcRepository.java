
package es.iesquevedo.dao.repositories.spring;

import es.iesquevedo.dao.model.Usuario;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioJdbcRepository {

    private final JdbcClient jdbcClient;

    public UsuarioJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Usuario> findByUsername(String username) {
        return jdbcClient.sql("""
                SELECT username, password
                FROM usuarios
                WHERE username = :username
                """)
                .param("username", username)
                .query((rs, rowNum) -> Usuario.builder()
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .build())
                .optional();
    }

    public boolean login(String username, String password) {
        return findByUsername(username)
                .map(usuario -> usuario.getPassword().equals(password))
                .orElse(false);
    }
}
