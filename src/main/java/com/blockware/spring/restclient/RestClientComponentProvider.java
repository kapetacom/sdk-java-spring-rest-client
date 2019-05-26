package com.blockware.spring.restclient;

import com.blockware.spring.annotation.BlockwareRestClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.Set;

/**
 * A classpath scanner implementation that finds interfaces that are annotated with InternalServiceClient
 *
 */
public class RestClientComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public RestClientComponentProvider() {
        super(false);

        addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            if (!metadataReader.getClassMetadata().isInterface()) {
                return false;
            }

            if (metadataReader.getAnnotationMetadata().hasAnnotation(BlockwareRestClient.class.getName())) {
                return true;
            }

            return false;
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        if (!beanDefinition.getMetadata().isInterface()) {
            return false;
        }

        return beanDefinition.getMetadata().hasAnnotation(BlockwareRestClient.class.getName());
    }

    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {

        Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);

        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
        }

        return candidates;
    }

}
