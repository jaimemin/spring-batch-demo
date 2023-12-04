package com.tistory.jaimemin.springbatchdemo.batch;

import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CustomerByCityQueryProvider extends AbstractJpaQueryProvider {

    private String cityName;

    @Override
    public Query createQuery() {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.city = :city");
        query.setParameter("city", cityName);

        return query;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cityName, "City name is required");
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
