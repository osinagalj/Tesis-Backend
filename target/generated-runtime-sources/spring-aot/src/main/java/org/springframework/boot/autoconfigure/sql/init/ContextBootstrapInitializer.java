package org.springframework.boot.autoconfigure.sql.init;

import java.lang.String;
import javax.sql.DataSource;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public final class ContextBootstrapInitializer {
  public static void registerDataSourceInitializationConfiguration(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.sql.init.DataSourceInitializationConfiguration", DataSourceInitializationConfiguration.class)
        .instanceSupplier(DataSourceInitializationConfiguration::new).register(beanFactory);
  }

  public static void registerDataSourceInitializationConfiguration_dataSourceScriptDatabaseInitializer(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("dataSourceScriptDatabaseInitializer", SqlDataSourceScriptDatabaseInitializer.class).withFactoryMethod(DataSourceInitializationConfiguration.class, "dataSourceScriptDatabaseInitializer", DataSource.class, SqlInitializationProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(DataSourceInitializationConfiguration.class).dataSourceScriptDatabaseInitializer(attributes.get(0), attributes.get(1)))).customize((bd) -> bd.setDependsOn(new String[] { "liquibase" })).register(beanFactory);
  }
}
