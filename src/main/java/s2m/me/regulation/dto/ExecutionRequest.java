package s2m.me.regulation.dto;

import jakarta.validation.constraints.NotBlank; // ou javax.validation.constraints.NotBlank
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExecutionRequest {

    /**
     * The unique identifier of the settlement session to be processed.
     * Must not be null or empty.
     */
    @NotBlank(message = "sessionId cannot be null or empty")
    private String sessionId;

    /**
     * The identifier of the processing center.
     * Must not be null or empty.
     */
    @NotBlank(message = "centerId cannot be null or empty")
    private String centerId;
}