package es.iesquevedo.dao.mappers;

import es.iesquevedo.dao.model.Cita;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CitaRowMapper {

    public Cita mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp fecha = rs.getTimestamp("fecha_cita");

        return Cita.builder()
                .id(rs.getLong("cita_id"))
                .pacienteId(rs.getLong("paciente_id"))
                .medicoId(rs.getLong("medico_id"))
                .fechaCita(fecha == null ? null : fecha.toLocalDateTime())
                .build();
    }
}