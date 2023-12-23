package com.tistory.jaimemin.springbatchdemo.batch;

import com.tistory.jaimemin.springbatchdemo.domain.CustomerAddressUpdate;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerContactUpdate;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerNameUpdate;
import com.tistory.jaimemin.springbatchdemo.domain.CustomerUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.classify.Classifier;

@RequiredArgsConstructor
public class CustomerUpdateClassifier implements Classifier<CustomerUpdate, ItemWriter<? super CustomerUpdate>> {

    private final JdbcBatchItemWriter<CustomerUpdate> recordType1ItemWriter;

    private final JdbcBatchItemWriter<CustomerUpdate> recordType2ItemWriter;

    private final JdbcBatchItemWriter<CustomerUpdate> recordType3ItemWriter;

    @Override
    public ItemWriter<? super CustomerUpdate> classify(CustomerUpdate classifiable) {
        if (classifiable instanceof CustomerNameUpdate) {
            return recordType1ItemWriter;
        }

        if (classifiable instanceof CustomerAddressUpdate) {
            return recordType2ItemWriter;
        }

        if (classifiable instanceof CustomerContactUpdate) {
            return recordType3ItemWriter;
        }

        throw new IllegalArgumentException(String.format("Invalid type: %s", classifiable.getClass().getCanonicalName()));
    }
}
