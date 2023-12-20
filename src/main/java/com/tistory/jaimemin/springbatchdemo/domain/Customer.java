package com.tistory.jaimemin.springbatchdemo.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "customer")
// @NodeEntity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//	@GeneratedValue(strategy = UuidStrategy.class)
    private long id;

    private String firstName;

    private String middleInitial;

    private String lastName;

    private String address;

    private String city;

    private String state;

    private String zip;

    private String email;
}
