package com.kapeta.spring.restclient;


import com.kapeta.spring.annotation.KapetaRestClient;
import com.blockware.spring.cluster.BlockwareClusterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    private static final String RESTCLIENT_PREFIX = "blockware.clients.";
    public static final String SERVICE_TYPE = "rest";


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

    @Autowired
    private BlockwareClusterService blockwareConfigSource;

    public <T> T makeClient(Class<T> restClientInterface) {

        if (!restClientInterface.isInterface()) {
            throw new IllegalArgumentException("Expected interface but got class: " + restClientInterface);
        }

        KapetaRestClient restClient = restClientInterface.getAnnotation(KapetaRestClient.class);

        if (restClient == null) {
            throw new IllegalArgumentException("Expected interface with InternalServiceClient annotation for class: " + restClientInterface);
        }

        String serviceName = restClient.value();

        final String baseUrl = getBaseUrlForService(serviceName);
        final OkHttpClient okHttpClient = buildInternalServiceClient(serviceName);
        final Retrofit retrofit = buildRetrofit(baseUrl, okHttpClient);

        T out = retrofit.create(restClientInterface);


        log.info("Created Retrofit client for {} - service name: {}, base url: {}", restClientInterface, serviceName, baseUrl);

        return out;
    }

    public String getBaseUrlForService(String serviceName) {

        String base = getString(serviceName, "base", null);

        if (base == null || base.isEmpty()) {
            base = blockwareConfigSource.getServiceAddress(serviceName, SERVICE_TYPE);
        }

        if (base == null || base.isEmpty()) {
            base = "http://" + serviceName.toLowerCase();
        }

        return base;
    }

    private Retrofit buildRetrofit(String baseUrl, OkHttpClient client) {

        return new Retrofit.Builder()
                .addConverterFactory(defaultConverter())
                .addCallAdapterFactory(new SimpleCallAdapterFactory())
                .client(client)
                .baseUrl(baseUrl)
                .build();
    }

    private OkHttpClient buildInternalServiceClient(String serviceName) {

        long defaultConnectTimeout = getDefaultLong( "client.timeout_connect", 15000);
        long defaultReadTimeout = getDefaultLong("client.timeout_read", 120000);
        long defaultWriteTimeout = getDefaultLong("client.timeout_write", 120000);

        long connectTimeout = getLong(serviceName, "client.timeout_connect", defaultConnectTimeout);
        long readTimeout = getLong(serviceName, "client.timeout_read", defaultReadTimeout);
        long writeTimeout = getLong(serviceName, "client.timeout_write", defaultWriteTimeout);

        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    private String getString(String serviceName, String key, String defaultValue) {

        String configId = RESTCLIENT_PREFIX + serviceName.toLowerCase() + ".rest." + key;

        return environment.getProperty(configId, defaultValue);
    }

    private long getLong(String serviceName, String key, long defaultValue) {

        String configId = RESTCLIENT_PREFIX + serviceName.toLowerCase() + ".rest." + key;

        return environment.getProperty(configId, Long.class, defaultValue);
    }

    private long getDefaultLong(String key, long defaultValue) {

        String configId = RESTCLIENT_PREFIX +  "default.rest." + key;

        return environment.getProperty(configId, Long.class, defaultValue);
    }


    private Converter.Factory defaultConverter() {
        return JacksonConverterFactory.create(objectMapper);
    }

}
