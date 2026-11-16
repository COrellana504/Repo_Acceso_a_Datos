
package es.iesquevedo.dao.repositories.spring;

import es.iesquevedo.dao.model.Paciente;
import es.iesquevedo.dao.model.Usuario;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class PacienteJdbcRepository {

    private final JdbcClient jdbcClient;

    public PacienteJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Paciente> findAll() {
        return jdbcClient.sql("""
                SELECT paciente_id, nombre, fecha_nacimiento, telefono
                FROM pacientes
                ORDER BY paciente_id
                """)
                .query((rs, rowNum) -> Paciente.builder()
                        .id(rs.getLong("paciente_id"))
                        .nombre(rs.getString("nombre"))
                        .fechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate())
                        .telefono(rs.getString("telefono"))
                        .build())
                .list();
    }

    public Optional<Paciente> findById(Long id) {
        return jdbcClient.sql("""
                SELECT paciente_id, nombre, fecha_nacimiento, telefono
                FROM pacientes
                WHERE paciente_id = :id
                """)
                .param("id", id)
                .query((rs, rowNum) -> Paciente.builder()
                        .id(rs.getLong("paciente_id"))
                        .nombre(rs.getString("nombre"))
                        .fechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate())
                        .telefono(rs.getString("telefono"))
                        .build())
                .optional();
    }

    @Transactional
    public Long add(Paciente paciente, Usuario usuario) {
        jdbcClient.sql("""
                INSERT INTO pacientes (nombre, fecha_nacimiento, telefono)
                VALUES (:nombre, :fechaNacimiento, :telefono)
                """)
                .param("nombre", paciente.getNombre())
                .param("fechaNacimiento", Date.valueOf(paciente.getFechaNacimiento()))
                .param("telefono", paciente.getTelefono())
                .update();

        // Recuperamos el ID del paciente recién insertado.
        Long nuevoId = jdbcClient.sql("SELECT LAST_INSERT_ID()")
                .query(Long.class)
                .single();

        jdbcClient.sql("""
                INSERT INTO usuarios (username, password, paciente_id)
                VALUES (:username, :password, :pacienteId)
                """)
                .param("username", usuario.getUsername())
                .param("password", usuario.getPassword())
                .param("pacienteId", nuevoId)
                .update();

        return nuevoId;
    }

    public boolean update(Paciente paciente) {
        return jdbcClient.sql("""
                UPDATE pacientes
                SET nombre = :nombre,
                    fecha_nacimiento = :fechaNacimiento,
                    telefono = :telefono
                WHERE paciente_id = :id
                """)
                .param("nombre", paciente.getNombre())
                .param("fechaNacimiento", Date.valueOf(paciente.getFechaNacimiento()))
                .param("telefono", paciente.getTelefono())
                .param("id", paciente.getId())
                .update() > 0;
    }

    public boolean tieneHistorias(Long id) {
        Integer cantidad = jdbcClient.sql("""
                SELECT COUNT(*)
                FROM historia_clinica
                WHERE paciente_id = :id
                """)
                .param("id", id)
                .query(Integer.class)
                .single();

        return cantidad > 0;
    }

    @Transactional
    public boolean delete(Long id) {
        // Borramos primero las historias para respetar las claves foráneas.
        jdbcClient.sql("""
                DELETE FROM historia_clinica
                WHERE paciente_id = :id
                """)
                .param("id", id)
                .update();

        jdbcClient.sql("""
                DELETE FROM usuarios
                WHERE paciente_id = :id
                """)
                .param("id", id)
                .update();

        return jdbcClient.sql("""
                DELETE FROM pacientes
                WHERE paciente_id = :id
                """)
                .param("id", id)
                .update() > 0;
    }
}
