/*
 * Copyright 2023 Kapeta Inc.
 * SPDX-License-Identifier: MIT
 */

package com.kapeta.spring.restclient;


import org.springframework.context.annotation.Bean;

public class RestClientConfig {

    @Bean
    public RestClientFactorySupport restClientFactorySupport() {
        return new RestClientFactorySupport();
    }
}
