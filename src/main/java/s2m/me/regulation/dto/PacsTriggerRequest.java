package s2m.me.regulation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacsTriggerRequest {
    private String sessionId;
    private List<String> currencies;
}