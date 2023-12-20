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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Deprecated
//@EnableBatchProcessing
//@SpringBootApplication
@RequiredArgsConstructor
public class DelimitedFileJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName"
                        , "middleInitial"
                        , "lastName"
                        , "address"
                        , "city"
                        , "state"
                        , "zip"})
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> customerItemWriter(
            @Value("#{jobParameters['outputFile']}") Resource outputFile
    ) {
        try {
            File file = outputFile.getFile();
            file.setReadable(true, false);
            file.setExecutable(true, false);
            file.setWritable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FlatFileItemWriterBuilder<Customer>()
                .name("customerItemWriter")
                .resource(outputFile)
                .delimited()
                .delimiter(";")
                .names(new String[] {"firstName", "lastName", "address", "city", "state", "zip"})
                .append(true)
                .build();
    }

    @Bean
    public Step formatStep() {
        return this.stepBuilderFactory.get("delimitedStep")
                .<Customer, Customer>chunk(10)
                .reader(customerFileReader(null))
//                .writer(itemWriter())
                .writer(customerItemWriter(null))
                .build();
    }

    @Bean
    public Job formatJob() {
        return this.jobBuilderFactory.get("delimitedJob")
                .incrementer(new RunIdIncrementer())
                .start(formatStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DelimitedFileJob.class, "customerFile=/data/customer.csv", "outputFile=/output/delimitedCustomers.txt");
    }
}
