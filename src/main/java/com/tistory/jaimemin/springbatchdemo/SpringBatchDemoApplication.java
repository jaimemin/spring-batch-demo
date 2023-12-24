package com.tistory.jaimemin.springbatchdemo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchDemoApplication {

    public static void main(String[] args) {
        String[] realArgs = {"customerUpdateFile=/data/customer_update_shuffled.csv",
                "transactionFile=/data/transactions.xml",
                "outputDirectory=file:\\C:\\Users\\USER\\IdeaProjects\\spring-batch-demo\\src\\main\\resources\\output\\"};
//        String [] realArgs = {
//            "customerUpdateFile=/data/customer_update_shuffled.csv",
//            "transactionFile=/data/transactions.xml"
//		};

        SpringApplication.run(SpringBatchDemoApplication.class, realArgs);
    }
}
