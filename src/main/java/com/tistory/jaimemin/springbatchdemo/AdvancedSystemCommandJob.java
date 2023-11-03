package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

//@Configuration
public class AdvancedSystemCommandJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("systemCommandJob")
                .start(systemCommandStep())
                .build();
    }

    @Bean
    public Step systemCommandStep() {
        return this.stepBuilderFactory.get("systemCommandStep")
                .tasklet(systemCommandTasklet())
                .build();
    }

    @Bean
    public SystemCommandTasklet systemCommandTasklet() {
        SystemCommandTasklet tasklet = new SystemCommandTasklet();
        tasklet.setCommand("fsutil file createnew tmp.txt 0");
        tasklet.setTimeout(5000);
        tasklet.setInterruptOnCancel(true);
        tasklet.setWorkingDirectory("C:\\Users\\USER\\spring-batch");
        tasklet.setSystemProcessExitCodeMapper(touchCodeMapper());
        tasklet.setTerminationCheckInterval(5000);
        tasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());
        tasklet.setEnvironmentParams(new String[] {
                "JAVA_HOME=C:\\Program Files\\Java\\jdk-11.0.16",
                "BATCH_HOME=C:\\Users\\USER\\spring-batch"
        });

        return tasklet;
    }

    @Bean
    public SimpleSystemProcessExitCodeMapper touchCodeMapper() {
        return new SimpleSystemProcessExitCodeMapper();
    }
}
