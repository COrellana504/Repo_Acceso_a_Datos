package es.iesquevedo.domain.dto.backup;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@XmlType(name = "historia", propOrder = {"fechaAdmision", "diagnostico", "recetas"})
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoriaBackupDTO {

    @XmlAttribute
    private Long id;

    @XmlAttribute
    private Long medicoId;

    @XmlElement
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    private LocalDate fechaAdmision;

    @XmlElementWrapper(name = "recetas")
    @XmlElement(name = "receta")
    private List<RecetaBackupDTO> recetas;

    @XmlElement
    private String diagnostico;
}