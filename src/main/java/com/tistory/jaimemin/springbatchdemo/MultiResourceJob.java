package com.tistory.jaimemin.springbatchdemo;

import com.tistory.jaimemin.springbatchdemo.batch.CustomerOutputFileSuffixCreator;
import com.tistory.jaimemin.springbatchdemo.batch.CustomerXmlHeaderCallback;
import com.tistory.jaimemin.springbatchdemo.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.batch.item.jms.builder.JmsItemReaderBuilder;
import org.springframework.batch.item.jms.builder.JmsItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Deprecated
//@EnableBatchProcessing
//@SpringBootApplication
@RequiredArgsConstructor
public class MultiResourceJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public JdbcCursorItemReader<Customer> customerJdbcCursorItemReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Customer>()
				.name("customerItemReader")
				.dataSource(dataSource)
				.sql("select * from customer")
				.rowMapper(new BeanPropertyRowMapper<>(Customer.class))
				.build();
	}

	@Bean
	public MultiResourceItemWriter<Customer> multiCustomerFileWriter(CustomerOutputFileSuffixCreator suffixCreator) throws Exception {
		return new MultiResourceItemWriterBuilder<Customer>()
				.name("multiCustomerFileWriter")
				.delegate(delegateItemWriter(null))
				.itemCountLimitPerResource(25)
				.resource(new FileSystemResource("C:\\Users\\USER\\IdeaProjects\\spring-batch-demo\\target\\customer"))
				.resourceSuffixCreator(suffixCreator)
				.build();
	}

	@Bean
	@StepScope
	public StaxEventItemWriter<Customer> delegateItemWriter(CustomerXmlHeaderCallback headerCallback) throws Exception {
		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);

		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliases);
		marshaller.afterPropertiesSet();

		return new StaxEventItemWriterBuilder<Customer>()
				.name("customerItemWriter")
				.marshaller(marshaller)
				.rootTagName("customers")
				.headerCallback(headerCallback)
				.build();
	}

	@Bean
	public Step multiXmlGeneratorStep() throws Exception {
		return this.stepBuilderFactory.get("multiXmlGeneratorStep")
				.<Customer, Customer>chunk(10)
				.reader(customerJdbcCursorItemReader(null))
				.writer(multiCustomerFileWriter(null))
				.build();
	}

	@Bean
	public Job xmlGeneratorJob() throws Exception {
		return this.jobBuilderFactory.get("xmlGeneratorJob")
				.start(multiXmlGeneratorStep())
				.incrementer(new RunIdIncrementer())
				.build();
	}

    public static void main(String[] args) {
        SpringApplication.run(MultiResourceJob.class);
    }
}
