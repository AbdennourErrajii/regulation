package s2m.me.regulation.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "processing-service", url = "${microservice.processing.api-url}")
public interface ProcessingClient {

    @PostMapping("/api/v1/cut-off-id")
    void sendSessionIds(@RequestBody Map<String, String> sessionIds);
}