package com.tistory.jaimemin.springbatchdemo;

import com.tistory.jaimemin.springbatchdemo.domain.Customer;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Deprecated
//@EnableBatchProcessing
//@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackageClasses = Customer.class)
public class RepositoryImportJob {

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
    public RepositoryItemWriter<Customer> repositoryItemWriter(CustomerRepository repository) {
        return new RepositoryItemWriterBuilder<Customer>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step repositoryFormatStep() throws Exception {
        return this.stepBuilderFactory.get("repositoryFormatStep")
                .<Customer, Customer>chunk(10)
                .reader(customerFileReader(null))
                .writer(repositoryItemWriter(null))
                .build();
    }

    @Bean
    public Job repositoryFormatJob() throws Exception {
        return this.jobBuilderFactory.get("repositoryFormatJob")
                .start(repositoryFormatStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(RepositoryImportJob.class, "customerFile=/data/customer.csv");
    }
}
