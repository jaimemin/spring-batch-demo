package com.tistory.jaimemin.springbatchdemo.service;

import com.tistory.jaimemin.springbatchdemo.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerService {

    public void logCustomer(Customer customer) {
        log.info("{}", customer);
    }

    public void logCustomerAddress(String address,
                                   String city,
                                   String state,
                                   String zip) {
        log.info(
                String.format("I just saved the address:\n%s\n%s, %s\n%s",
                        address,
                        city,
                        state,
                        zip));
    }
}
