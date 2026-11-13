package es.iesquevedo.dao.repositories.jdbc;

import es.iesquevedo.common.Constantes;
import es.iesquevedo.dao.common.SQLQueries;
import es.iesquevedo.dao.mappers.CitaRowMapper;
import es.iesquevedo.dao.model.Cita;
import es.iesquevedo.dao.repositories.CitaRepository;
import es.iesquevedo.dao.utils.DBConnection;
import es.iesquevedo.domain.error.DatabaseError;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JDBCCitaRepository implements CitaRepository {

    private final DBConnection dbConnection;
    private final CitaRowMapper citaRowMapper;

    @Inject
    public JDBCCitaRepository(
            DBConnection dbConnection,
            CitaRowMapper citaRowMapper) {
        this.dbConnection = dbConnection;
        this.citaRowMapper = citaRowMapper;
    }

    @Override
    public List<Cita> findByPacienteId(Long pacienteId) {
        List<Cita> citas = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(SQLQueries.FIND_CITAS_BY_PACIENTE)) {

            pstmt.setLong(1, pacienteId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    citas.add(citaRowMapper.mapRow(rs, rs.getRow()));
                }
            }

        } catch (SQLException e) {
            log.error("Error buscando citas del paciente {}", pacienteId, e);
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }

        return citas;
    }
}