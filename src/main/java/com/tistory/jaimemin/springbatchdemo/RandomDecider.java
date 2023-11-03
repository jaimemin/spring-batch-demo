package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Random;

public class RandomDecider implements JobExecutionDecider {

    private Random random = new Random();

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        return new FlowExecutionStatus(random.nextBoolean()
                ? FlowExecutionStatus.COMPLETED.getName() : FlowExecutionStatus.FAILED.getName());
    }
}
