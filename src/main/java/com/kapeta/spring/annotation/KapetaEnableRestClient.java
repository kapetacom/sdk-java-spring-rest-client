package com.kapeta.spring.annotation;

import com.kapeta.spring.restclient.RestClientBeanRegistrar;
import com.kapeta.spring.restclient.RestClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RestClientBeanRegistrar.class, RestClientConfig.class})
public @interface KapetaEnableRestClient {
}
