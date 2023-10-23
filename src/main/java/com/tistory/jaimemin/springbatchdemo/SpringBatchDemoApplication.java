package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * JobRepository: 실행 중인 잡의 상태를 기록하는데 사용
 * JobLauncher: 잡을 구동하는데 사용
 * JobExplorer: JobRepository를 사용해 읽기 전용 작업을 수행하는데 사용
 * JobRegistry: 특정한 런처 구현체를 사용할 때 잡을 찾는 용도로 사용
 * PlatformTransactionManager: 잡 진행 과정에서 트랜잭션을 다루는데 사용
 * JobBuilderFactory: 잡을 생성하는 빌더
 * StepBuilderFactory: 스탭을 생성하는 빌더
 */
@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchDemoApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return this.stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Hello, World!");

                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchDemoApplication.class, args);
    }

}
