package org.springframework.boot.autoconfigure.liquibase;

import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public final class ContextBootstrapInitializer {
  public static void registerLiquibaseAutoConfiguration_liquibaseDefaultDdlModeProvider(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("liquibaseDefaultDdlModeProvider", LiquibaseSchemaManagementProvider.class).withFactoryMethod(LiquibaseAutoConfiguration.class, "liquibaseDefaultDdlModeProvider", ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(LiquibaseAutoConfiguration.class).liquibaseDefaultDdlModeProvider(attributes.get(0)))).register(beanFactory);
  }
}
