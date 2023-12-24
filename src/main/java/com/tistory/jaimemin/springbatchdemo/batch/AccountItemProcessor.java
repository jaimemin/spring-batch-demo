package com.tistory.jaimemin.springbatchdemo.batch;

import com.tistory.jaimemin.springbatchdemo.domain.AccountResultSetExtractor;
import com.tistory.jaimemin.springbatchdemo.domain.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountItemProcessor implements ItemProcessor<Statement, Statement> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Statement process(Statement item) throws Exception {
        item.setAccounts(this.jdbcTemplate.query("select a.account_id," +
                        "       a.balance," +
                        "       a.last_statement_date," +
                        "       t.transaction_id," +
                        "       t.description," +
                        "       t.credit," +
                        "       t.debit," +
                        "       t.timestamp " +
                        "from account a left join " +
                        "    transaction t on a.account_id = t.account_account_id " +
                        "where a.account_id in " +
                        "	(select account_account_id " +
                        "	from customer_account " +
                        "	where customer_customer_id = ?) " +
                        "order by t.timestamp",
                new Object[] {item.getCustomer().getId()},
                new AccountResultSetExtractor()));

        return item;
    }
}
