package com.unicen;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerAppApplication(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("com.unicen.AppApplication", AppApplication.class)
        .instanceSupplier((instanceContext) -> {
          AppApplication bean = new AppApplication();
          instanceContext.field("globalApplicationContext", GlobalApplicationContext.class)
              .invoke(beanFactory, (attributes) -> {
                Field globalApplicationContextField = ReflectionUtils.findField(AppApplication.class, "globalApplicationContext", GlobalApplicationContext.class);
                ReflectionUtils.makeAccessible(globalApplicationContextField);
                ReflectionUtils.setField(globalApplicationContextField, bean, attributes.get(0));
              });
                  return bean;
                }).register(beanFactory);
          }
        }
