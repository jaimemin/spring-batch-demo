package com.tistory.jaimemin.springbatchdemo.domain;

import com.tistory.jaimemin.springbatchdemo.util.StringUtil;
import lombok.Getter;

@Getter
public class CustomerContactUpdate extends CustomerUpdate{

    private final String emailAddress;

    private final String homePhone;

    private final String cellPhone;

    private final String workPhone;

    private final Integer notificationPreferences;

    public CustomerContactUpdate(long customerId
            , String emailAddress
            , String homePhone
            , String cellPhone
            , String workPhone
            , Integer notificationPreferences) {
        super(customerId);
        this.emailAddress = StringUtil.getTextOrNull(emailAddress);
        this.homePhone = StringUtil.getTextOrNull(homePhone);
        this.cellPhone = StringUtil.getTextOrNull(cellPhone);
        this.workPhone = StringUtil.getTextOrNull(workPhone);
        this.notificationPreferences = notificationPreferences;
    }
}
