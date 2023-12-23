package com.tistory.jaimemin.springbatchdemo.configuration;

import com.tistory.jaimemin.springbatchdemo.batch.CustomerUpdateClassifier;
import com.tistory.jaimemin.springbatchdemo.batch.CustomerItemValidator;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerAddressUpdate;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerContactUpdate;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerNameUpdate;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerUpdate;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ImportJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() throws Exception {
        return this.jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .start(importCustomerUpdates())
                .build();
    }

    @Bean
    public Step importCustomerUpdates() throws Exception {
        return this.stepBuilderFactory.get("importCustomerUpdates")
                .<CustomerUpdate, CustomerUpdate>chunk(100)
                .reader(customerUpdateItemReader(null))
                .processor(customerValidatingItemProcessor(null))
                .writer(customerUpdateItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerUpdate> customerUpdateItemReader(
            @Value("#{jobParameters['customerUpdateFile']}") Resource inputFile
    ) throws Exception {
        return new FlatFileItemReaderBuilder<CustomerUpdate>()
                .name("customerUpdateItemReader")
                .resource(inputFile)
                .lineTokenizer(customerUpdatesLineTokenizer())
                .fieldSetMapper(customerUpdateFieldSetMapper())
                .build();
    }

    @Bean
    public LineTokenizer customerUpdatesLineTokenizer() throws Exception {
        DelimitedLineTokenizer recordType1 = getRecordType1();
        recordType1.afterPropertiesSet();

        DelimitedLineTokenizer recordType2 = getRecordType2();
        recordType2.afterPropertiesSet();

        DelimitedLineTokenizer recordType3 = getRecordType3();
        recordType3.afterPropertiesSet();

        PatternMatchingCompositeLineTokenizer lineTokenizer = new PatternMatchingCompositeLineTokenizer();
        lineTokenizer.setTokenizers(getTokenizers(recordType1, recordType2, recordType3));

        return lineTokenizer;
    }

    @Bean
    public FieldSetMapper<CustomerUpdate> customerUpdateFieldSetMapper() {
        return fieldSet -> {
            switch (fieldSet.readInt("recordId")) {
                case 1:
                    return new CustomerNameUpdate(fieldSet.readLong("customerId"),
                        fieldSet.readString("firstName"),
                        fieldSet.readString("middleName"),
                        fieldSet.readString("lastName"));
                case 2:
                    return new CustomerAddressUpdate(fieldSet.readLong("customerId"),
                        fieldSet.readString("address1"),
                        fieldSet.readString("address2"),
                        fieldSet.readString("city"),
                        fieldSet.readString("state"),
                        fieldSet.readString("postalCode"));
                case 3:
                    String rawPreference = fieldSet.readString("notificationPreference");
                    Integer notificationPreference = null;

                    if (StringUtils.hasText(rawPreference)) {
                        notificationPreference = Integer.parseInt(rawPreference);
                    }

                    return new CustomerContactUpdate(fieldSet.readLong("customerId"),
                            fieldSet.readString("emailAddress"),
                            fieldSet.readString("homePhone"),
                            fieldSet.readString("cellPhone"),
                            fieldSet.readString("workPhone"),
                            notificationPreference);
                default:
                    throw new IllegalArgumentException("Invalid record type was found:" + fieldSet.readInt("recordId"));
            }
        };
    }

    @Bean
    public ValidatingItemProcessor<CustomerUpdate> customerValidatingItemProcessor(CustomerItemValidator validator) {
        ValidatingItemProcessor<CustomerUpdate> customerValidatingItemProcessor
                = new ValidatingItemProcessor<>(validator);
        customerValidatingItemProcessor.setFilter(true);

        return customerValidatingItemProcessor;
    }

    @Bean
    public ItemWriter<CustomerUpdate> itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public ClassifierCompositeItemWriter<CustomerUpdate> customerUpdateItemWriter() {
        ClassifierCompositeItemWriter<CustomerUpdate> compositeItemWriter =
                new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(getClassifier());

        return compositeItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerNameUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE customer " +
                        "SET first_name = COALESCE(:firstName, first_name), " +
                        "middle_name = COALESCE(:middleName, middle_name), " +
                        "last_name = COALESCE(:lastName, last_name) " +
                        "WHERE customer_id = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerAddressUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE customer SET " +
                        "address1 = COALESCE(:address1, address1), " +
                        "address2 = COALESCE(:address2, address2), " +
                        "city = COALESCE(:city, city), " +
                        "state = COALESCE(:state, state), " +
                        "postal_code = COALESCE(:postalCode, postal_code) " +
                        "WHERE customer_id = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerContactUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE customer SET " +
                        "email_address = COALESCE(:emailAddress, email_address), " +
                        "home_phone = COALESCE(:homePhone, home_phone), " +
                        "cell_phone = COALESCE(:cellPhone, cell_phone), " +
                        "work_phone = COALESCE(:workPhone, work_phone), " +
                        "notification_pref = COALESCE(:notificationPreferences, notification_pref) " +
                        "WHERE customer_id = :customerId")
                .dataSource(dataSource)
                .build();
    }

    private Map<String, LineTokenizer> getTokenizers(DelimitedLineTokenizer recordType1, DelimitedLineTokenizer recordType2, DelimitedLineTokenizer recordType3) {
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("1*", recordType1);
        tokenizers.put("2*", recordType2);
        tokenizers.put("3*", recordType3);

        return tokenizers;
    }

    private DelimitedLineTokenizer getRecordType3() {
        DelimitedLineTokenizer recordType3 = new DelimitedLineTokenizer();
        recordType3.setNames("recordId", "customerId", "emailAddress", "homePhone", "cellPhone", "workPhone", "notificationPreference");

        return recordType3;
    }

    private DelimitedLineTokenizer getRecordType2() {
        DelimitedLineTokenizer recordType2 = new DelimitedLineTokenizer();
        recordType2.setNames("recordId", "customerId", "address1", "address2", "city", "state", "postalCode");

        return recordType2;
    }

    private DelimitedLineTokenizer getRecordType1() {
        DelimitedLineTokenizer recordType1 = new DelimitedLineTokenizer();
        recordType1.setNames("recordId", "customerId", "firstName", "middleName", "lastName");

        return recordType1;
    }

    private CustomerUpdateClassifier getClassifier() {
        CustomerUpdateClassifier classifier =
                new CustomerUpdateClassifier(customerNameUpdateItemWriter(null),
                        customerAddressUpdateItemWriter(null),
                        customerContactUpdateItemWriter(null));

        return classifier;
    }
}
