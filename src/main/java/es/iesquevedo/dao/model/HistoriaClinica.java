package es.iesquevedo.dao.model;

import lombok.*;

import java.time.LocalDate;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoriaClinica {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDate fechaAdmision;
    private String diagnostico;
}
