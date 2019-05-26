package com.blockware.spring.annotation;

import com.blockware.spring.restclient.RestClientBeanRegistrar;
import com.blockware.spring.restclient.RestClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RestClientBeanRegistrar.class, RestClientConfig.class})
public @interface BlockwareEnableRestClient {
}
