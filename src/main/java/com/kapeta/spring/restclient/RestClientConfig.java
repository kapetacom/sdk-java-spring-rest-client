/*
 * Copyright 2023 Kapeta Inc.
 * SPDX-License-Identifier: MIT
 */

package com.kapeta.spring.restclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kapeta.spring.config.providers.KapetaConfigurationProvider;
import com.kapeta.spring.security.AuthorizationForwarderSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class RestClientConfig {

    @Bean
    public RestClientFactorySupport restClientFactorySupport(ObjectMapper objectMapper, Environment environment, KapetaConfigurationProvider configurationProvider, AuthorizationForwarderSupplier authorizationForwarder) {
        return new RestClientFactorySupport(objectMapper, environment, configurationProvider, authorizationForwarder.get());
    }
}
