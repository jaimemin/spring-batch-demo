package com.tistory.jaimemin.springbatchdemo.domain;

import lombok.*;

@Data
public class CustomerUpdate {

    protected final long customerId;

    public CustomerUpdate(long customerId) {
        this.customerId = customerId;
    }
}
