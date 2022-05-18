package com.unicen.core.services;

import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerEmailService(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("emailService", EmailService.class)
        .instanceSupplier((instanceContext) -> {
          EmailService bean = new EmailService();
          instanceContext.field("javaMailSender", JavaMailSender.class)
              .invoke(beanFactory, (attributes) -> {
                Field javaMailSenderField = ReflectionUtils.findField(EmailService.class, "javaMailSender", JavaMailSender.class);
                ReflectionUtils.makeAccessible(javaMailSenderField);
                ReflectionUtils.setField(javaMailSenderField, bean, attributes.get(0));
              });
                  return bean;
                }).register(beanFactory);
          }
        }
