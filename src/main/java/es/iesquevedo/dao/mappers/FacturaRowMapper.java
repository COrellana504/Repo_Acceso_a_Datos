package es.iesquevedo.dao.mappers;

import es.iesquevedo.dao.model.Factura;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FacturaRowMapper {

    public Factura mapRow(ResultSet rs, int rowNum) throws SQLException {
        Date fecha = rs.getDate("fecha_pago");

        return Factura.builder()
                .id(rs.getLong("factura_id"))
                .pacienteId(rs.getLong("paciente_id"))
                .fechaPago(fecha == null ? null : fecha.toLocalDate())
                .cantidad(rs.getBigDecimal("cantidad"))
                .build();
    }
}
