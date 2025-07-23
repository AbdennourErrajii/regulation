package s2m.me.regulation.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import s2m.me.regulation.dto.ExecutionRequest;

/**
 * REST Controller responsible for launching the settlement batch job.
 * This is the entry point triggered by the front-end application.
 */
@RestController
@RequestMapping("/api/v1/settlements")
@Slf4j
public class SettlementExecutionController {

    private final JobLauncher jobLauncher;
    private final Job regulationJob;

    @Autowired
    public SettlementExecutionController(
            JobLauncher jobLauncher,
            @Qualifier("RegulationJob") Job regulationJob
    ) {
        this.jobLauncher = jobLauncher;
        this.regulationJob = regulationJob;
    }


    @PostMapping("/execute")
    public ResponseEntity<String> executeSettlement(@Valid @RequestBody ExecutionRequest request) {
        log.info("Received request to execute settlement for sessionId: {}", request.getSessionId());

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startTime", System.currentTimeMillis())
                    .addString("sessionId", request.getSessionId())
                    .addString("centerId", request.getCenterId())
                    .toJobParameters();
            jobLauncher.run(regulationJob, jobParameters);


            String message = "Settlement job for session " + request.getSessionId() + " has been successfully launched.";
            log.info(message);
            return ResponseEntity.ok(message);

        } catch (JobExecutionAlreadyRunningException e) {
            String errorMessage = "A job instance for session " + request.getSessionId() + " is already running.";
            log.warn(errorMessage, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (JobInstanceAlreadyCompleteException e) {
            String errorMessage = "A job instance for session " + request.getSessionId() + " has already completed successfully. It cannot be run again with the exact same parameters.";
            log.info(errorMessage); // C'est une information, pas une erreur critique.
            return ResponseEntity.status(HttpStatus.OK).body(errorMessage);
        } catch (JobParametersInvalidException | JobRestartException e) {
            String errorMessage = "Failed to launch settlement job due to invalid parameters or restart issues.";
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }
}