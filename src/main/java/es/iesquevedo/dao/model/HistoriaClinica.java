
package es.iesquevedo.dao.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoriaClinica {

    @JsonProperty("historia_id")
    private Long id;

    @JsonProperty("paciente_id")
    private Long pacienteId;

    @JsonProperty("medico_id")
    private Long medicoId;

    @JsonProperty("fecha_admision")
    private LocalDate fechaAdmision;

    private String diagnostico;
}
