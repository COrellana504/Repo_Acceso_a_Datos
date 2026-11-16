
package es.iesquevedo.dao.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cita {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDateTime fechaCita;
}
