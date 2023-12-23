package com.tistory.jaimemin.springbatchdemo.domain;

import com.tistory.jaimemin.springbatchdemo.util.StringUtil;
import lombok.Data;
import lombok.Getter;

@Getter
public class CustomerAddressUpdate extends CustomerUpdate {

    private final String address1;

    private final String address2;

    private final String city;

    private final String state;

    private final String postalCode;

    public CustomerAddressUpdate(long customerId
            , String address1
            , String address2
            , String city
            , String state
            , String postalCode) {
        super(customerId);
        this.address1 = StringUtil.getTextOrNull(address1);
        this.address2 = StringUtil.getTextOrNull(address2);
        this.city = StringUtil.getTextOrNull(city);
        this.state = StringUtil.getTextOrNull(state);
        this.postalCode = StringUtil.getTextOrNull(postalCode);
    }
}
