
package es.iesquevedo.dao.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Factura {
    private Long id;
    private Long pacienteId;
    private LocalDate fechaPago;
    private BigDecimal cantidad;
}

