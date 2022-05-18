package com.unicen.core.controller;

import com.unicen.core.services.AuthenticationService;
import com.unicen.core.services.PublicObjectCrudService;
import com.unicen.core.services.UserService;
import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerAuthController(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("authController", AuthController.class).withConstructor(AuthenticationService.class)
        .instanceSupplier((instanceContext) -> {
          AuthController bean = instanceContext.create(beanFactory, (attributes) -> new AuthController(attributes.get(0)));
          instanceContext.field("userService", UserService.class)
              .invoke(beanFactory, (attributes) -> {
                Field userServiceField = ReflectionUtils.findField(AuthController.class, "userService", UserService.class);
                ReflectionUtils.makeAccessible(userServiceField);
                ReflectionUtils.setField(userServiceField, bean, attributes.get(0));
              });
                  return bean;
                }).register(beanFactory);
          }

          public static void registerUserController(DefaultListableBeanFactory beanFactory) {
            BeanDefinitionRegistrar.of("userController", UserController.class)
                .instanceSupplier((instanceContext) -> {
                  UserController bean = new UserController();
                  instanceContext.field("service", PublicObjectCrudService.class)
                      .invoke(beanFactory, (attributes) -> bean.service = attributes.get(0));
                          return bean;
                        }).register(beanFactory);
                  }
                }
