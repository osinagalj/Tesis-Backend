package org.springframework.boot.autoconfigure.mail;

import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public final class ContextBootstrapInitializer {
  public static void registerMailSenderPropertiesConfiguration(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.mail.MailSenderPropertiesConfiguration", MailSenderPropertiesConfiguration.class)
        .instanceSupplier(MailSenderPropertiesConfiguration::new).register(beanFactory);
  }

  public static void registerMailSenderPropertiesConfiguration_mailSender(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("mailSender", JavaMailSenderImpl.class).withFactoryMethod(MailSenderPropertiesConfiguration.class, "mailSender", MailProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(MailSenderPropertiesConfiguration.class).mailSender(attributes.get(0)))).register(beanFactory);
  }
}
