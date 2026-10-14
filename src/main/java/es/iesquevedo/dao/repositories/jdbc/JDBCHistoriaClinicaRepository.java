package es.iesquevedo.dao.repositories.jdbc;

import es.iesquevedo.common.Constantes;
import es.iesquevedo.dao.common.SQLQueries;
import es.iesquevedo.dao.mappers.HistoriaClinicaRowMapper;
import es.iesquevedo.dao.model.HistoriaClinica;
import es.iesquevedo.dao.repositories.HistoriaClinicaRepository;
import es.iesquevedo.dao.utils.DBConnection;
import es.iesquevedo.domain.error.DatabaseError;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JDBCHistoriaClinicaRepository implements HistoriaClinicaRepository {

    private final DBConnection dbConnection;
    private final HistoriaClinicaRowMapper historiaRowMapper;

    @Inject
    public JDBCHistoriaClinicaRepository(DBConnection dbConnection, HistoriaClinicaRowMapper historiaRowMapper) {
        this.dbConnection = dbConnection;
        this.historiaRowMapper = historiaRowMapper;
    }

    @Override
    public List<HistoriaClinica> findByPacienteId(Long pacienteId) {
        List<HistoriaClinica> historias = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.FIND_HISTORIAS_BY_PACIENTE)) {
            pstmt.setLong(1, pacienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    historias.add(historiaRowMapper.mapRow(rs, rs.getRow()));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
        return historias;
    }

    @Override
    public Long add(HistoriaClinica historia) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.ADD_HISTORIA, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, historia.getPacienteId());
            pstmt.setLong(2, historia.getMedicoId());
            pstmt.setDate(3, Date.valueOf(historia.getFechaAdmision()));
            pstmt.setString(4, historia.getDiagnostico());
            pstmt.executeUpdate();
            try (ResultSet ids = pstmt.getGeneratedKeys()) {
                if (ids.next()) {
                    historia.setId(ids.getLong(1));
                }
            }
            return historia.getId();
        } catch (SQLIntegrityConstraintViolationException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.REFERENCIA_ERROR);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }

    @Override
    public boolean update(HistoriaClinica historia) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.UPDATE_HISTORIA)) {
            pstmt.setLong(1, historia.getMedicoId());
            pstmt.setDate(2, Date.valueOf(historia.getFechaAdmision()));
            pstmt.setString(3, historia.getDiagnostico());
            pstmt.setLong(4, historia.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.REFERENCIA_ERROR);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.DELETE_HISTORIA)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }
}
