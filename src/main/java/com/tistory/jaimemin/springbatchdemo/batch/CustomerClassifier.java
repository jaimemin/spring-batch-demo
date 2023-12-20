package com.tistory.jaimemin.springbatchdemo.batch;

import com.tistory.jaimemin.springbatchdemo.domain.Customer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.classify.Classifier;

public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {

    private ItemWriter<Customer> fileItemWriter;

    private ItemWriter<Customer> jdbcItemWriter;

    public CustomerClassifier(StaxEventItemWriter<Customer> fileItemWriter
            , JdbcBatchItemWriter<Customer> jdbcItemWriter) {
        this.fileItemWriter = fileItemWriter;
        this.jdbcItemWriter = jdbcItemWriter;
    }

    @Override
    public ItemWriter<Customer> classify(Customer customer) {
        return customer.getState().matches("^[A-M].*") ? fileItemWriter : jdbcItemWriter;
    }
}
