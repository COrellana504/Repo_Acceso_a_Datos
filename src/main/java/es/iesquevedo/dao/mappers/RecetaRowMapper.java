package es.iesquevedo.dao.mappers;

import es.iesquevedo.dao.model.Receta;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecetaRowMapper {
    public Receta mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Receta.builder()
                .id(rs.getLong("receta_id"))
                .historiaId(rs.getLong("historia_id"))
                .medicamento(rs.getString("medicamento"))
                .dosis(rs.getString("dosis"))
                .build();
    }
}