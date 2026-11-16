
package es.iesquevedo.dao.repositories.spring;

import es.iesquevedo.dao.model.HistoriaClinica;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class HistoriaClinicaJdbcRepository {

    private final JdbcClient jdbcClient;

    public HistoriaClinicaJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<HistoriaClinica> findByPacienteId(Long pacienteId) {
        return jdbcClient.sql("""
                SELECT historia_id, paciente_id, medico_id,
                       fecha_admision, diagnostico
                FROM historia_clinica
                WHERE paciente_id = :pacienteId
                ORDER BY fecha_admision
                """)
                .param("pacienteId", pacienteId)
                .query((rs, rowNum) -> HistoriaClinica.builder()
                        .id(rs.getLong("historia_id"))
                        .pacienteId(rs.getLong("paciente_id"))
                        .medicoId(rs.getLong("medico_id"))
                        .fechaAdmision(rs.getDate("fecha_admision").toLocalDate())
                        .diagnostico(rs.getString("diagnostico"))
                        .build())
                .list();
    }

    public Optional<HistoriaClinica> findById(Long id) {
        return jdbcClient.sql("""
                SELECT historia_id, paciente_id, medico_id,
                       fecha_admision, diagnostico
                FROM historia_clinica
                WHERE historia_id = :id
                """)
                .param("id", id)
                .query((rs, rowNum) -> HistoriaClinica.builder()
                        .id(rs.getLong("historia_id"))
                        .pacienteId(rs.getLong("paciente_id"))
                        .medicoId(rs.getLong("medico_id"))
                        .fechaAdmision(rs.getDate("fecha_admision").toLocalDate())
                        .diagnostico(rs.getString("diagnostico"))
                        .build())
                .optional();
    }

    public Long add(HistoriaClinica historia) {
        jdbcClient.sql("""
                INSERT INTO historia_clinica
                    (paciente_id, medico_id, fecha_admision, diagnostico)
                VALUES
                    (:pacienteId, :medicoId, :fechaAdmision, :diagnostico)
                """)
                .param("pacienteId", historia.getPacienteId())
                .param("medicoId", historia.getMedicoId())
                .param("fechaAdmision", Date.valueOf(historia.getFechaAdmision()))
                .param("diagnostico", historia.getDiagnostico())
                .update();

        return jdbcClient.sql("SELECT LAST_INSERT_ID()")
                .query(Long.class)
                .single();
    }

    public boolean update(HistoriaClinica historia) {
        return jdbcClient.sql("""
                UPDATE historia_clinica
                SET medico_id = :medicoId,
                    fecha_admision = :fechaAdmision,
                    diagnostico = :diagnostico
                WHERE historia_id = :id
                """)
                .param("medicoId", historia.getMedicoId())
                .param("fechaAdmision", Date.valueOf(historia.getFechaAdmision()))
                .param("diagnostico", historia.getDiagnostico())
                .param("id", historia.getId())
                .update() > 0;
    }

    public boolean delete(Long id) {
        return jdbcClient.sql("""
                DELETE FROM historia_clinica
                WHERE historia_id = :id
                """)
                .param("id", id)
                .update() > 0;
    }
}
