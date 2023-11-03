package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@Configuration
public class ChunkJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("chunkBasedJob")
                .start(chunkStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step chunkStep() {
        return this.stepBuilderFactory.get("chunkStep")
                .<String, String>chunk(completionPolicy())
                .reader(itemReader())
                .writer(itemWriter())
                .listener(new LoggingStepStartListener())
                .build();
    }

    @Bean
    public ListItemReader<String> itemReader() {
        List<String> items = new ArrayList<>(100000);

        for (int i = 0; i < 100000; i++) {
            items.add(UUID.randomUUID().toString());
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(String.format(">> current item = %s", item));
            }
        };
    }

    @Bean
    public CompletionPolicy completionPolicy() {
//        CompositeCompletionPolicy policy = new CompositeCompletionPolicy();
//        policy.setPolicies(new CompletionPolicy[] {
//                new TimeoutTerminationPolicy(3),
//                new SimpleCompletionPolicy(1000)
//        });
//
//        return policy;
        return new RandomChunkSizePolicy();
    }

}
