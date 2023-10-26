package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

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
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        DefaultJobParametersValidator defaultJobParametersValidator
                = new DefaultJobParametersValidator(new String[] {"fileName"}, new String[] {"name", "currentDate"});
        defaultJobParametersValidator.afterPropertiesSet();
        validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));

        return validator;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .incrementer(new DailyJobTimestamper())
                .start(step1())
                .validator(validator())
                .listener(JobListenerFactoryBean.getListener(new JobLoggerListener()))
//                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null, null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name
            , @Value("#{jobParameters['fileName']}") String fileName) {
        return (stepContribution, chunkContext) -> {
//            String name = (String) chunkContext.getStepContext()
//                    .getJobParameters()
//                    .get("name");
            System.out.println(String.format("Hello, %s!", name));
            System.out.println(String.format("fileName = %s", fileName));

            return RepeatStatus.FINISHED;
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchDemoApplication.class, args);
    }

}
