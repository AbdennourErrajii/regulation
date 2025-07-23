package s2m.me.regulation.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO representing a single unit of work for the report generation step.
 * A report will be generated for each unique pair of institution and currency.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InstitutionCurrencyPair implements Serializable {

    private static final long serialVersionUID = 1L;

    private String institutionId;
    private String currency;
}
