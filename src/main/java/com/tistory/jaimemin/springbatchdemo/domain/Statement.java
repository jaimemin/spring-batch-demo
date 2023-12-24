package com.tistory.jaimemin.springbatchdemo.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Statement {

    private final Customer customer;

    private List<Account> accounts = new ArrayList<>();

    public Statement(Customer customer) {
        this.customer = customer;
    }

    public Statement(Customer customer, List<Account> accounts) {
        this.customer = customer;
        this.accounts.addAll(accounts);
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts.addAll(accounts);
    }
}
