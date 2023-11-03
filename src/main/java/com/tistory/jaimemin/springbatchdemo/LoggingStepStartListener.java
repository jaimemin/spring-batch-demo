package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class LoggingStepStartListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        System.out.println(String.format("%s has begun!", stepExecution.getStepName()));
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println(String.format("%s has ended!", stepExecution.getStepName()));

        return ExitStatus.COMPLETED;
    }
}
