package com.tistory.jaimemin.springbatchdemo.domain;

import com.tistory.jaimemin.springbatchdemo.batch.JaxbDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "transaction")
public class Transaction {

    private long transactionId;

    private long accountId;

    private String description;

    private BigDecimal credit;

    private BigDecimal debit;

    private Date timestamp;

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    @XmlJavaTypeAdapter(JaxbDateSerializer.class)
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTransactionAmount() {
        if (credit != null) {
            return debit != null ? credit.add(debit) : credit;
        }
        else if (debit != null) {
            return debit;
        }

        return new BigDecimal(0);
    }
}
