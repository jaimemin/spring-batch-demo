package com.tistory.jaimemin.springbatchdemo;

import com.tistory.jaimemin.springbatchdemo.domain.Customer;
import com.tistory.jaimemin.springbatchdemo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.adapter.PropertyExtractingDelegatingItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@Deprecated
//@EnableBatchProcessing
//@SpringBootApplication
@RequiredArgsConstructor
public class PropertyExtractingJob {

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
    public PropertyExtractingDelegatingItemWriter<Customer> itemWriter(CustomerService customerService) {
        PropertyExtractingDelegatingItemWriter<Customer> itemWriter
                = new PropertyExtractingDelegatingItemWriter<>();
        itemWriter.setTargetObject(customerService);
        itemWriter.setTargetMethod("logCustomerAddress");
        itemWriter.setFieldsUsedAsTargetMethodArguments(new String[] {"address", "city", "state", "zip"});

        return itemWriter;
    }

    @Bean
    public Step formatStep() throws Exception {
        return this.stepBuilderFactory.get("repositoryFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(customerFileReader(null))
                .writer(itemWriter(null))
                .build();
    }

    @Bean
    public Job formatJob() throws Exception {
        return this.jobBuilderFactory.get("repositoryFormatJob")
                .start(formatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PropertyExtractingJob.class, "customerFile=/data/customer.csv");
    }
}
