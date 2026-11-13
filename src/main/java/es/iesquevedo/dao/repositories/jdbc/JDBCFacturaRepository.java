package es.iesquevedo.dao.repositories.jdbc;

import es.iesquevedo.common.Constantes;
import es.iesquevedo.dao.common.SQLQueries;
import es.iesquevedo.dao.mappers.FacturaRowMapper;
import es.iesquevedo.dao.model.Factura;
import es.iesquevedo.dao.repositories.FacturaRepository;
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
public class JDBCFacturaRepository implements FacturaRepository {

    private final DBConnection dbConnection;
    private final FacturaRowMapper facturaRowMapper;

    @Inject
    public JDBCFacturaRepository(
            DBConnection dbConnection,
            FacturaRowMapper facturaRowMapper) {
        this.dbConnection = dbConnection;
        this.facturaRowMapper = facturaRowMapper;
    }

    @Override
    public List<Factura> findByPacienteId(Long pacienteId) {
        List<Factura> facturas = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(SQLQueries.FIND_FACTURAS_BY_PACIENTE)) {

            pstmt.setLong(1, pacienteId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(facturaRowMapper.mapRow(rs, rs.getRow()));
                }
            }

        } catch (SQLException e) {
            log.error("Error buscando facturas del paciente {}", pacienteId, e);
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }

        return facturas;
    }
}