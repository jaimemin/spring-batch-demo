package com.tistory.jaimemin.springbatchdemo.domain;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.HashSet;
import java.util.Set;

public class UniqueLastNameValidator extends ItemStreamSupport implements Validator<Customer> {

    private static final String KEY = "lastNames";

    private Set<String> lastNames = new HashSet<>();

    @Override
    public void validate(Customer value) throws ValidationException {
        if (lastNames.contains(value.getLastName())) {
            throw new ValidationException(String.format("Duplicate last name was found: %s", value.getLastName()));
        }

        this.lastNames.add(value.getLastName());
    }

    @Override
    public void update(ExecutionContext executionContext) {
        executionContext.put(getExecutionContextKey(KEY), this.lastNames);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        String lastNames = getExecutionContextKey(KEY);

        if (executionContext.containsKey(KEY)) {
            this.lastNames = (Set<String>) executionContext.get(lastNames);
        }
    }
}
