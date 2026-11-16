
package es.iesquevedo.domain.dto.backup;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name = "paciente")
@XmlType(propOrder = {
        "nombre",
        "fechaNacimiento",
        "telefono",
        "historias",
        "citas",
        "facturas"
})
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacienteBackupDTO {

    @XmlAttribute
    private Long id;

    @XmlElement
    private String nombre;

    @XmlElement
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    private LocalDate fechaNacimiento;

    @XmlElement
    private String telefono;

    @XmlElementWrapper(name = "historias")
    @XmlElement(name = "historia")
    private List<HistoriaBackupDTO> historias;

    @XmlElementWrapper(name = "citas")
    @XmlElement(name = "cita")
    private List<CitaBackupDTO> citas;

    @XmlElementWrapper(name = "facturas")
    @XmlElement(name = "factura")
    private List<FacturaBackupDTO> facturas;
}
