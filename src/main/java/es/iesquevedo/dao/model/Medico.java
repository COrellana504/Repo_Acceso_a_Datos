
package es.iesquevedo.dao.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Medico {

    @JsonProperty("medico_id")
    private Long id;

    private String nombre;
    private String especialidad;
    private String telefono;
}
