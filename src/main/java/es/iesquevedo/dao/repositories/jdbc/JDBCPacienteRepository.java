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
import es.iesquevedo.dao.model.HistoriaClinica;
import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import es.iesquevedo.dao.model.Receta;
import es.iesquevedo.dao.model.Cita;
import es.iesquevedo.dao.model.Factura;
import java.sql.Timestamp;

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
    public Optional<Paciente> findById(Long id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.FIND_PACIENTE_BY_ID)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(pacienteRowMapper.mapRow(rs, rs.getRow())) : Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmtCitas =
                     conn.prepareStatement(SQLQueries.DELETE_CITAS_BY_PACIENTE);
             PreparedStatement pstmtFacturas =
                     conn.prepareStatement(SQLQueries.DELETE_FACTURAS_BY_PACIENTE);
             PreparedStatement pstmtRecetas = conn.prepareStatement(
                     SQLQueries.DELETE_RECETAS_BY_PACIENTE);
             PreparedStatement pstmtHistorias = conn.prepareStatement(
                     SQLQueries.DELETE_HISTORIAS_BY_PACIENTE);
             PreparedStatement pstmtUsuario = conn.prepareStatement(
                     SQLQueries.DELETE_USUARIO_BY_PACIENTE);
             PreparedStatement pstmtPaciente = conn.prepareStatement(
                     SQLQueries.DELETE_PACIENTE)) {

            try {
                conn.setAutoCommit(false);

                // 1. Eliminar citas
                pstmtCitas.setLong(1, id);
                pstmtCitas.executeUpdate();

                // 2. Eliminar facturas
                pstmtFacturas.setLong(1, id);
                pstmtFacturas.executeUpdate();

                // 3. Eliminar recetas
                pstmtRecetas.setLong(1, id);
                pstmtRecetas.executeUpdate();

                // 4. Eliminar historias clínicas
                pstmtHistorias.setLong(1, id);
                pstmtHistorias.executeUpdate();

                // 5. Eliminar usuario
                pstmtUsuario.setLong(1, id);
                pstmtUsuario.executeUpdate();

                // 6. Eliminar paciente
                pstmtPaciente.setLong(1, id);
                int filas = pstmtPaciente.executeUpdate();
                conn.commit();
                return filas > 0;

            } catch (SQLException e) {
                conn.rollback();
                log.error(e.getMessage());

                if (e instanceof SQLIntegrityConstraintViolationException) {
                    throw new DatabaseError(
                            "No se puede borrar el paciente: tiene registros relacionados.");
                }

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
    public void restore(
            Paciente paciente,
            List<HistoriaClinica> historias,
            List<Receta> recetas,
            List<Cita> citas,
            List<Factura> facturas) {

        try (Connection conn = dbConnection.getConnection()) {

            try {
                conn.setAutoCommit(false);

                try (PreparedStatement pstmtPaciente =
                             conn.prepareStatement(SQLQueries.RESTORE_PACIENTE);
                     PreparedStatement pstmtHistoria =
                             conn.prepareStatement(SQLQueries.RESTORE_HISTORIA);
                     PreparedStatement pstmtReceta =
                             conn.prepareStatement(SQLQueries.RESTORE_RECETA);
                     PreparedStatement pstmtCita =
                             conn.prepareStatement(SQLQueries.RESTORE_CITA);
                     PreparedStatement pstmtFactura =
                             conn.prepareStatement(SQLQueries.RESTORE_FACTURA)) {

                    // 1. Restaurar paciente
                    pstmtPaciente.setLong(1, paciente.getId());
                    pstmtPaciente.setString(2, paciente.getNombre());
                    pstmtPaciente.setDate(3, Date.valueOf(paciente.getFechaNacimiento()));
                    pstmtPaciente.setString(4, paciente.getTelefono());
                    pstmtPaciente.executeUpdate();

                    // 2. Restaurar historias clínicas
                    for (HistoriaClinica historia : historias) {
                        pstmtHistoria.setLong(1, historia.getId());
                        pstmtHistoria.setLong(2, paciente.getId());
                        pstmtHistoria.setLong(3, historia.getMedicoId());
                        pstmtHistoria.setDate(4, Date.valueOf(historia.getFechaAdmision()));
                        pstmtHistoria.setString(5, historia.getDiagnostico());
                        pstmtHistoria.executeUpdate();
                    }

                    // 3. Restaurar recetas
                    for (Receta receta : recetas) {
                        pstmtReceta.setLong(1, receta.getId());
                        pstmtReceta.setLong(2, receta.getHistoriaId());
                        pstmtReceta.setString(3, receta.getMedicamento());
                        pstmtReceta.setString(4, receta.getDosis());
                        pstmtReceta.executeUpdate();
                    }

                    // 4. Restaurar citas
                    for (Cita cita : citas) {
                        pstmtCita.setLong(1, cita.getId());
                        pstmtCita.setLong(2, paciente.getId());
                        pstmtCita.setLong(3, cita.getMedicoId());

                        if (cita.getFechaCita() == null) {
                            pstmtCita.setTimestamp(4, null);
                        } else {
                            pstmtCita.setTimestamp(
                                    4,
                                    Timestamp.valueOf(cita.getFechaCita())
                            );
                        }

                        pstmtCita.executeUpdate();
                    }

                    // 5. Restaurar facturas
                    for (Factura factura : facturas) {
                        pstmtFactura.setLong(1, factura.getId());
                        pstmtFactura.setLong(2, paciente.getId());

                        if (factura.getFechaPago() == null) {
                            pstmtFactura.setDate(3, null);
                        } else {
                            pstmtFactura.setDate(
                                    3,
                                    Date.valueOf(factura.getFechaPago())
                            );
                        }

                        pstmtFactura.setBigDecimal(4, factura.getCantidad());
                        pstmtFactura.executeUpdate();
                    }
                }

                conn.commit();

            } catch (SQLIntegrityConstraintViolationException e) {
                conn.rollback();
                log.error("Error de integridad al restaurar paciente", e);

                if (e.getErrorCode() == 1062) {
                    throw new PacienteDuplicadoError();
                }

                throw new DatabaseError(Constantes.REFERENCIA_ERROR);

            } catch (SQLException e) {
                conn.rollback();
                log.error("Error restaurando paciente", e);
                throw new DatabaseError(Constantes.DATABASE_ERROR);

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            log.error("Error en la conexión al restaurar paciente", e);
            throw new DatabaseError(Constantes.DATABASE_ERROR);
        }
    }


}
