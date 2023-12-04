package com.tistory.jaimemin.springbatchdemo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByCity(String city, Pageable pageable);
}
