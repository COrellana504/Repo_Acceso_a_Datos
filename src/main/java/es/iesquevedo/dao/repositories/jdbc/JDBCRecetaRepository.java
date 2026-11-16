package es.iesquevedo.dao.repositories.jdbc;

import es.iesquevedo.common.Constantes;
import es.iesquevedo.dao.common.SQLQueries;
import es.iesquevedo.dao.mappers.RecetaRowMapper;
import es.iesquevedo.dao.model.Receta;
import es.iesquevedo.dao.repositories.RecetaRepository;
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
public class JDBCRecetaRepository implements RecetaRepository {

    private final DBConnection dbConnection;
    private final RecetaRowMapper recetaRowMapper;

    @Inject
    public JDBCRecetaRepository(DBConnection dbConnection, RecetaRowMapper recetaRowMapper) {
        this.dbConnection = dbConnection;
        this.recetaRowMapper = recetaRowMapper;
    }

    @Override
    public List<Receta> findByPacienteId(Long pacienteId) {
        List<Receta> recetas = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.FIND_RECETAS_BY_PACIENTE)) {
            pstmt.setLong(1, pacienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    recetas.add(recetaRowMapper.mapRow(rs, rs.getRow()));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
        return recetas;
    }
}