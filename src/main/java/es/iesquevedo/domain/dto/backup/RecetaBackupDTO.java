package es.iesquevedo.domain.dto.backup;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlType(name = "receta", propOrder = {"medicamento", "dosis"})
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecetaBackupDTO {

    @XmlAttribute
    private Long id;

    @XmlElement
    private String medicamento;

    @XmlElement
    private String dosis;
}