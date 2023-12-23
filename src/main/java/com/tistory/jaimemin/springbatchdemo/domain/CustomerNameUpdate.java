package com.tistory.jaimemin.springbatchdemo.domain;

import com.tistory.jaimemin.springbatchdemo.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class CustomerNameUpdate extends CustomerUpdate {

    private final String firstName;

    private final String middleName;

    private final String lastName;

    public CustomerNameUpdate(long customerId
            , String firstName
            , String middleName
            , String lastName) {
        super(customerId);
        this.firstName = StringUtil.getTextOrNull(firstName);
        this.middleName = StringUtil.getTextOrNull(middleName);
        this.lastName = StringUtil.getTextOrNull(lastName);
    }
}
