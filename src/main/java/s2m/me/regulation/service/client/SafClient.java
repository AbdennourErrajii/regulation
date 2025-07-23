package s2m.me.regulation.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "saf-service", url = "${microservice.saf.api-url}")
public interface SafClient {

    @PostMapping("/api/v1/cut-off-id")
    void sendSessionIds(@RequestBody Map<String, String> sessionIds);
}