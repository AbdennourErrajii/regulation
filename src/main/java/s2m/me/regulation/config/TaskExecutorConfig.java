package s2m.me.regulation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TaskExecutorConfig {

    @Bean("partitionTaskExecutor")
    public TaskExecutor partitionTaskExecutor(
            @Value("${spring.datasource.hikari.maximum-pool-size}") int maxDbConnections
    ) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        log.info("Machine has {} available processors.", availableProcessors);
        log.info("Database connection pool has a max size of {}.", maxDbConnections);

        int optimalThreadPoolSize = Math.min(availableProcessors, maxDbConnections - 1);

        if (optimalThreadPoolSize < 1) {
            optimalThreadPoolSize = 1;
        }

        log.info("Setting partitionTaskExecutor pool size to {}.", optimalThreadPoolSize);
        taskExecutor.setCorePoolSize(optimalThreadPoolSize);
        taskExecutor.setMaxPoolSize(optimalThreadPoolSize);

        taskExecutor.setQueueCapacity(optimalThreadPoolSize * 2);
        taskExecutor.setThreadNamePrefix("partition-worker-");

        // Configuration pour la gestion des threads inactifs (bonnes pratiques)
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setKeepAliveSeconds(60);

        taskExecutor.setThreadFactory(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        return taskExecutor;
    }
}
