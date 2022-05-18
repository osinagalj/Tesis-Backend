package springfox.boot.starter.autoconfigure;

import java.lang.String;
import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerSwaggerUiWebMvcConfiguration(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("springfox.boot.starter.autoconfigure.SwaggerUiWebMvcConfiguration", SwaggerUiWebMvcConfiguration.class)
        .instanceSupplier((instanceContext) -> {
          SwaggerUiWebMvcConfiguration bean = new SwaggerUiWebMvcConfiguration();
          instanceContext.field("swaggerBaseUrl", String.class)
              .invoke(beanFactory, (attributes) -> {
                Field swaggerBaseUrlField = ReflectionUtils.findField(SwaggerUiWebMvcConfiguration.class, "swaggerBaseUrl", String.class);
                ReflectionUtils.makeAccessible(swaggerBaseUrlField);
                ReflectionUtils.setField(swaggerBaseUrlField, bean, attributes.get(0));
              });
                  return bean;
                }).register(beanFactory);
          }
        }
