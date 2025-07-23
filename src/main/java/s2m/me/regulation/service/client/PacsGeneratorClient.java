package s2m.me.regulation.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import s2m.me.regulation.dto.PacsTriggerRequest;


@FeignClient(name = "pacs-file-generator", url = "${microservice.pacs-generator.api-url}")
public interface PacsGeneratorClient {

    @PostMapping("/api/v1/generate-pacs")
    void triggerPacsGeneration(@RequestBody PacsTriggerRequest request);
}