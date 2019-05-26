package com.blockware.spring.restclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Set;


/**
 * Reads all interfaces annotated with BlockwareRestClient and generates RestClientFactoryBean beans for them.
 *
 * The Retrofit clients are lazely initialised to only do work when work is needed.
 *
 * This class is invoked by @Import(RestClientBeanRegistrar.class) on a configuration class and will scan the
 * package that the configuration class is placed in.
 */
@Slf4j
public class RestClientBeanRegistrar implements ImportBeanDefinitionRegistrar {

    private AnnotationBeanNameGenerator annotationBeanNameGenerator = new AnnotationBeanNameGenerator();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        RestClientComponentProvider componentProvider = new RestClientComponentProvider();

        String className = importingClassMetadata.getClassName();
        String packageName = ClassUtils.getPackageName(className);

        Set<BeanDefinition> candidateComponents = componentProvider.findCandidateComponents(packageName);

        for(BeanDefinition candidate : candidateComponents) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);

                String beanName = annotationBeanNameGenerator.generateBeanName(candidate, registry);

                GenericBeanDefinition bean = new GenericBeanDefinition();

                bean.setBeanClass(RestClientFactoryBean.class);
                bean.setAutowireCandidate(true);
                bean.setLazyInit(true);
                bean.getConstructorArgumentValues().addIndexedArgumentValue(0, candidate.getBeanClassName());

                log.info("Found REST client {} for class {}", beanName, candidate.getBeanClassName());
                registry.registerBeanDefinition(beanName, bean);

            }
        }
    }
}
