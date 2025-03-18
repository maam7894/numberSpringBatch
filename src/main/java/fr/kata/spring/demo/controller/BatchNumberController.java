package fr.kata.spring.demo.controller;


import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.kata.spring.demo.batch.NumberProcessor;
import fr.kata.spring.demo.dto.NumberDto;
import fr.kata.spring.demo.service.FileLaunchService;
import fr.kata.spring.demo.service.RuleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BatchNumberController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job importCustomerJob;
    @Autowired
    private FileLaunchService fileLaunchService;
    @Autowired
    private NumberProcessor numberProcessor;


    @PostMapping("/launch")
    @CrossOrigin
    public ResponseEntity<String> launch(@RequestParam("filename") String filename) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {


        if(!fileLaunchService.fileExistInputFolder(filename)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseEnum.BAD_REQUEST.getMessage());

        }

        if(fileLaunchService.fileExistProcessedFolder(filename)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseEnum.ALREADY_PROCESSED.getMessage());

        }

        String pathToResource = fileLaunchService.resolvedInFilePath(filename);

        JobParameters params = new JobParametersBuilder()
                .addString("filePath", pathToResource)
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(importCustomerJob, params);
        return ResponseEntity.ok().body("Batch job has been invoked");
    }

    @GetMapping("/transform")
    @CrossOrigin
    public ResponseEntity<String> transform(@RequestParam("number") int param) {
        try {
            NumberDto dto = new NumberDto();
            dto.setInput(param);
            numberProcessor.process(dto);
            return ResponseEntity.ok().body(dto.getResult());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
