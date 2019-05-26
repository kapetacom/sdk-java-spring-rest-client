package com.blockware.spring.restclient;


import com.blockware.spring.annotation.BlockwareRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating Retrofit clients from an interface.
 *
 * Also sets up the clients with the proper headers, timeouts, retry mechanism etc.
 *
 */
@Slf4j
public class RestClientFactorySupport {

    @Value("${client.timeout_connect:15000}")
    private int clientConnectTimeout;

    @Value("${client.timeout_read:120000}")
    private int clientReadTimeout;

    @Value("${client.timeout_write:120000}")
    private int clientWriteTimeout;

    @Value("${client.max_retries:100}")
    private int clientMaxRetries;

    @Value("${client.retry_wait:5000}")
    private long clientRetryWait;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> T makeClient(Class<T> restClientInterface) {

        if (!restClientInterface.isInterface()) {
            throw new IllegalArgumentException("Expected interface but got class: " + restClientInterface);
        }

        BlockwareRestClient restClient = restClientInterface.getAnnotation(BlockwareRestClient.class);

        if (restClient == null) {
            throw new IllegalArgumentException("Expected interface with InternalServiceClient annotation for class: " + restClientInterface);
        }

        String serviceName = restClient.value();


        final OkHttpClient okHttpClient = buildInternalServiceClient();
        final Retrofit retrofit = buildRetrofit(serviceName, okHttpClient);

        T out = retrofit.create(restClientInterface);

        log.info("Created Retrofit client for {} - service name: {}", restClientInterface, serviceName);

        return out;
    }

    private Retrofit buildRetrofit(String serviceName, OkHttpClient client) {

        return new Retrofit.Builder()
                .addConverterFactory(defaultConverter())
                .addCallAdapterFactory(new SimpleCallAdapterFactory())
                .client(client)
                .baseUrl("http://" + serviceName.toLowerCase() + "/")
                .build();
    }

    private OkHttpClient buildInternalServiceClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(clientConnectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(clientReadTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(clientWriteTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    private Converter.Factory defaultConverter() {
        return JacksonConverterFactory.create(objectMapper);
    }

}
