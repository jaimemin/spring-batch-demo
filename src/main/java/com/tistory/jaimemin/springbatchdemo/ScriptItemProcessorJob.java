package com.tistory.jaimemin.springbatchdemo;

import com.tistory.jaimemin.springbatchdemo.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.ScriptItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@Deprecated
//@SpringBootApplication
//@EnableBatchProcessing
@RequiredArgsConstructor
public class ScriptItemProcessorJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .delimited()
                .names(new String[] {"firstName"
                        , "middleInitial"
                        , "lastName"
                        , "address"
                        , "city"
                        , "state"
                        , "zip"})
                .targetType(Customer.class)
                .resource(inputFile)
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    @StepScope
    public ScriptItemProcessor<Customer, Customer> itemProcessor(@Value("#{jobParameters['script']}") Resource script) {
        ScriptItemProcessor<Customer, Customer> itemProcessor = new ScriptItemProcessor<>();
        itemProcessor.setScript(script);

        return itemProcessor;
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(5)
                .reader(customerItemReader(null))
                .processor(itemProcessor(null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return this.jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(copyFileStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ScriptItemProcessorJob.class, "customerFile=/input/customer.csv", "script=/upperCase.js");
    }
}
