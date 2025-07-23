package s2m.me.regulation.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Component
@ConfigurationProperties(prefix = "regulation.batch")
@Getter
@Setter
@Validated // Active la validation des propriétés
public class RegulationBatchProperties {

    /**
     * The path to the output directory for generated files like PACS.009.
     */
    //@NotBlank
    private String outputFilePath;

    /**
     * Default centerId to be used if not provided as a job parameter.
     */
    private String defaultCenterId;

    // Vous pouvez ajouter d'autres propriétés ici
}