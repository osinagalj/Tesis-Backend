package springfox.documentation.spring.web.paths;

import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public final class ContextBootstrapInitializer {
  public static void registerQueryStringUriTemplateDecorator(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("queryStringUriTemplateDecorator", QueryStringUriTemplateDecorator.class)
        .instanceSupplier(QueryStringUriTemplateDecorator::new).register(beanFactory);
  }

  public static void registerPathMappingDecorator(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("pathMappingDecorator", PathMappingDecorator.class)
        .instanceSupplier(PathMappingDecorator::new).register(beanFactory);
  }

  public static void registerPathSanitizer(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("pathSanitizer", PathSanitizer.class)
        .instanceSupplier(PathSanitizer::new).register(beanFactory);
  }

  public static void registerOperationPathDecorator(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("operationPathDecorator", OperationPathDecorator.class)
        .instanceSupplier(OperationPathDecorator::new).register(beanFactory);
  }
}
