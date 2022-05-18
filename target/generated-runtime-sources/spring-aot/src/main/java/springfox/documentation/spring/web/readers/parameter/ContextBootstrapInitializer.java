package springfox.documentation.spring.web.readers.parameter;

import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

public final class ContextBootstrapInitializer {
  public static void registerModelAttributeParameterExpander(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("modelAttributeParameterExpander", ModelAttributeParameterExpander.class).withConstructor(FieldProvider.class, AccessorsProvider.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> {
          ModelAttributeParameterExpander bean = instanceContext.create(beanFactory, (attributes) -> new ModelAttributeParameterExpander(attributes.get(0), attributes.get(1), attributes.get(2)));
          instanceContext.field("pluginsManager", DocumentationPluginsManager.class)
              .invoke(beanFactory, (attributes) -> {
                Field pluginsManagerField = ReflectionUtils.findField(ModelAttributeParameterExpander.class, "pluginsManager", DocumentationPluginsManager.class);
                ReflectionUtils.makeAccessible(pluginsManagerField);
                ReflectionUtils.setField(pluginsManagerField, bean, attributes.get(0));
              });
                  return bean;
                }).register(beanFactory);
          }
        }
