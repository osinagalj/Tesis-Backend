package com.unicen.app.controller;

import com.unicen.core.services.EmailService;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public final class ContextBootstrapInitializer {
  public static void registerSendEmail(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("sendEmail", SendEmail.class)
        .instanceSupplier((instanceContext) -> {
          SendEmail bean = new SendEmail();
          instanceContext.field("sendEmailService", EmailService.class)
              .invoke(beanFactory, (attributes) -> bean.sendEmailService = attributes.get(0));
                  return bean;
                }).register(beanFactory);
          }
        }
