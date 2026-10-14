package es.iesquevedo.dao.repositories.jdbc;

import es.iesquevedo.common.Constantes;
import es.iesquevedo.dao.common.SQLQueries;
import es.iesquevedo.dao.mappers.PacienteRowMapper;
import es.iesquevedo.dao.model.Paciente;
import es.iesquevedo.dao.repositories.PacienteRepository;
import es.iesquevedo.dao.utils.DBConnection;
import es.iesquevedo.domain.error.DatabaseError;
import es.iesquevedo.domain.error.PacienteDuplicadoError;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JDBCPacienteRepository implements PacienteRepository {

    private final DBConnection dbConnection;
    private final PacienteRowMapper pacienteRowMapper;

    @Inject
    public JDBCPacienteRepository(DBConnection dbConnection,
                                  PacienteRowMapper pacienteRowMapper) {
        this.dbConnection = dbConnection;
        this.pacienteRowMapper = pacienteRowMapper;
    }

    @Override
    public List<Paciente> findAll() {
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.FIND_ALL_PACIENTES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Paciente paciente = pacienteRowMapper.mapRow(rs, rs.getRow());
                pacientes.add(paciente);
            }
        } catch (SQLException e) {
            log.error("Se ha producido un error en la BD");
            throw new DatabaseError(e.getMessage());
        }
        return pacientes;
    }

    @Override
    public Long add(Paciente paciente) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmtPaciente = conn.prepareStatement(SQLQueries.ADD_PACIENTE, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtUsuario = conn.prepareStatement(SQLQueries.ADD_USUARIO)
        ) {
            try {
            conn.setAutoCommit(false);
            pstmtPaciente.setString(1, paciente.getNombre());
            pstmtPaciente.setDate(2, Date.valueOf(paciente.getFechaNacimiento()));
            pstmtPaciente.setString(3, paciente.getTelefono());

            int filasAfectadas = pstmtPaciente.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet idsGenerados = pstmtPaciente.getGeneratedKeys();
                if (idsGenerados.next()) {
                    paciente.setId(idsGenerados.getLong(1));
                    pstmtUsuario.setString(1, paciente.getUsuario().getUsername());
                    pstmtUsuario.setString(2, paciente.getUsuario().getPassword());
                    pstmtUsuario.setLong(3, paciente.getId());
                    pstmtUsuario.executeUpdate();
                    conn.commit();
                }
            }
            return paciente.getId();
        } catch (SQLIntegrityConstraintViolationException e) {
            conn.rollback();
            log.error(e.getMessage());
            throw new PacienteDuplicadoError();
        } catch (SQLException e) {
            conn.rollback();
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        } finally {
            conn.setAutoCommit(true);
        }
      } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }

    @Override
    public boolean update(Paciente paciente) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.UPDATE_PACIENTE)) {
            pstmt.setString(1, paciente.getNombre());
            pstmt.setDate(2, Date.valueOf(paciente.getFechaNacimiento()));
            pstmt.setString(3, paciente.getTelefono());
            pstmt.setLong(4, paciente.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmtUsuario = conn.prepareStatement(SQLQueries.DELETE_USUARIO_BY_PACIENTE);
             PreparedStatement pstmtPaciente = conn.prepareStatement(SQLQueries.DELETE_PACIENTE)) {
            try {
                conn.setAutoCommit(false);
                pstmtUsuario.setLong(1, id);
                pstmtUsuario.executeUpdate();
                pstmtPaciente.setLong(1, id);
                int filas = pstmtPaciente.executeUpdate();
                conn.commit();
                return filas > 0;
            } catch (SQLIntegrityConstraintViolationException e) {
                conn.rollback();
                log.error(e.getMessage());
                throw new DatabaseError("No se puede borrar el paciente: tiene registros en su historia.");
            } catch (SQLException e) {
                conn.rollback();
                log.error(e.getMessage());
                throw new DatabaseError(Constantes.DATABASE_ERROR);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }
}
