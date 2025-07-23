package s2m.me.regulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RegulationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegulationApplication.class, args);
    }

}
