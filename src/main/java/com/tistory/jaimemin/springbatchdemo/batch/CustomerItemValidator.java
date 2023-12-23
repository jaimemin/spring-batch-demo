package com.tistory.jaimemin.springbatchdemo.batch;

import com.tistory.jaimemin.springbatchdemo.domain.CustomerUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@Component
public class CustomerItemValidator implements Validator<CustomerUpdate> {

    private final static String FIND_CUSTOMER = "SELECT COUNT(*) FROM customer WHERE customer_id = :id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    protected CustomerItemValidator(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public CustomerItemValidator(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void validate(CustomerUpdate customer) throws ValidationException {
        Map<String, Long> parameterMap = Collections.singletonMap("id", customer.getCustomerId());
        Long count = jdbcTemplate.queryForObject(FIND_CUSTOMER, parameterMap, Long.class);

        if (!ObjectUtils.isEmpty(count) && count == 0) {
            throw new ValidationException(String.format("Customer id %s was not able to be found", customer.getCustomerId()));
        }
    }
}
