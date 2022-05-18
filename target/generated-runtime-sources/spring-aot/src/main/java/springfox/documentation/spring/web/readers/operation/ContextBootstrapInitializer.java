package springfox.documentation.spring.web.readers.operation;

import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

public final class ContextBootstrapInitializer {
  public static void registerOperationParameterReader(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("operationParameterReader", OperationParameterReader.class).withConstructor(ModelAttributeParameterExpander.class, EnumTypeDeterminer.class, ParameterAggregator.class)
        .instanceSupplier((instanceContext) -> {
          OperationParameterReader bean = instanceContext.create(beanFactory, (attributes) -> new OperationParameterReader(attributes.get(0), attributes.get(1), attributes.get(2)));
          instanceContext.field("pluginsManager", DocumentationPluginsManager.class)
              .invoke(beanFactory, (attributes) -> {
                Field pluginsManagerField = ReflectionUtils.findField(OperationParameterReader.class, "pluginsManager", DocumentationPluginsManager.class);
                ReflectionUtils.makeAccessible(pluginsManagerField);
                ReflectionUtils.setField(pluginsManagerField, bean, attributes.get(0));
              });
                  return bean;
                }).register(beanFactory);
          }
        }
