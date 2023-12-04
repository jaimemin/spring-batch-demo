package com.tistory.jaimemin.springbatchdemo.batch;

import org.hibernate.SessionFactory;
import org.springframework.batch.core.Entity;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Deprecated
@Configuration
public class HibernateBatchConfigurer extends DefaultBatchConfigurer {

    private DataSource dataSource;

    private SessionFactory sessionFactory;

    private PlatformTransactionManager transactionManager;

    public HibernateBatchConfigurer(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        super(dataSource);
        this.dataSource = dataSource;
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.transactionManager = new HibernateTransactionManager(this.sessionFactory);
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }
}
