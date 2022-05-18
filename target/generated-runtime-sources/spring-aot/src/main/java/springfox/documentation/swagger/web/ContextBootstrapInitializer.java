package springfox.documentation.swagger.web;

import java.lang.String;
import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerApiResourceController(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("apiResourceController", ApiResourceController.class).withConstructor(SwaggerResourcesProvider.class, String.class)
        .instanceSupplier((instanceContext) -> {
          ApiResourceController bean = instanceContext.create(beanFactory, (attributes) -> new ApiResourceController(attributes.get(0), attributes.get(1)));
          instanceContext.field("securityConfiguration", SecurityConfiguration.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> {
                Field securityConfigurationField = ReflectionUtils.findField(ApiResourceController.class, "securityConfiguration", SecurityConfiguration.class);
                ReflectionUtils.makeAccessible(securityConfigurationField);
                ReflectionUtils.setField(securityConfigurationField, bean, attributes.get(0));
              });
                  instanceContext.field("uiConfiguration", UiConfiguration.class)
                      .resolve(beanFactory, false).ifResolved((attributes) -> {
                        Field uiConfigurationField = ReflectionUtils.findField(ApiResourceController.class, "uiConfiguration", UiConfiguration.class);
                        ReflectionUtils.makeAccessible(uiConfigurationField);
                        ReflectionUtils.setField(uiConfigurationField, bean, attributes.get(0));
                      });
                          return bean;
                        }).register(beanFactory);
                  }
                }
