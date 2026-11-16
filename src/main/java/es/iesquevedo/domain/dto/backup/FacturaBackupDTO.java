
package es.iesquevedo.domain.dto.backup;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlType(name = "factura", propOrder = {"fechaPago", "cantidad"})
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacturaBackupDTO {

    @XmlAttribute
    private Long id;

    @XmlElement
    private String fechaPago;

    @XmlElement
    private String cantidad;
}
