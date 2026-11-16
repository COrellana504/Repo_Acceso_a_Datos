
package es.iesquevedo.dao.repositories.spring;

import es.iesquevedo.dao.model.Medico;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedicoJdbcRepository {

    private final JdbcClient jdbcClient;

    public MedicoJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Medico> findAll() {
        return jdbcClient.sql("""
                SELECT medico_id, nombre, especialidad, telefono
                FROM medicos
                ORDER BY medico_id
                """)
                .query((rs, rowNum) -> Medico.builder()
                        .id(rs.getLong("medico_id"))
                        .nombre(rs.getString("nombre"))
                        .especialidad(rs.getString("especialidad"))
                        .telefono(rs.getString("telefono"))
                        .build())
                .list();
    }
}
