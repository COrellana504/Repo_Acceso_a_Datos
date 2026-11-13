
package es.iesquevedo.domain.dto.backup;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlType(name = "cita", propOrder = {"fechaCita"})
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CitaBackupDTO {

    @XmlAttribute
    private Long id;

    @XmlAttribute
    private Long medicoId;

    @XmlElement
    private String fechaCita;
}
