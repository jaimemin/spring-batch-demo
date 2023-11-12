package com.tistory.jaimemin.springbatchdemo.domain;

import java.util.List;

public interface TransactionDao {

    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}
