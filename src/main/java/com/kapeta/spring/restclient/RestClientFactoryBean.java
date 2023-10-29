/*
 * Copyright 2023 Kapeta Inc.
 * SPDX-License-Identifier: MIT
 */

package com.kapeta.spring.restclient;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;

/**
 * FactoryBean for rest clients - this is what binds the Spring bean injection layer together with the
 * RestClientFactorySupport class that actually constructs the Retrofit clients
 *
 * @param <T>
 */
public class RestClientFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    @Autowired
    private RestClientFactorySupport restClientFactorySupport;

    private final Class<T> objectClass;

    private Lazy<T> restClient;

    public RestClientFactoryBean(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public T getObject() throws Exception {
        return restClient.get();
    }

    @Override
    public Class<T> getObjectType() {
        return objectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        restClient = Lazy.of(() -> restClientFactorySupport.makeClient(objectClass));
    }
}
