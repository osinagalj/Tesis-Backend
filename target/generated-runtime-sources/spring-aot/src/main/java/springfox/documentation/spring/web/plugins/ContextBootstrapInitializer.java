package springfox.documentation.spring.web.plugins;

import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerDocumentationPluginsManager(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("documentationPluginsManager", DocumentationPluginsManager.class)
        .instanceSupplier((instanceContext) -> {
          DocumentationPluginsManager bean = new DocumentationPluginsManager();
          instanceContext.field("documentationPlugins", PluginRegistry.class)
              .invoke(beanFactory, (attributes) -> {
                Field documentationPluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "documentationPlugins", PluginRegistry.class);
                ReflectionUtils.makeAccessible(documentationPluginsField);
                ReflectionUtils.setField(documentationPluginsField, bean, attributes.get(0));
              });
                  instanceContext.field("apiListingPlugins", PluginRegistry.class)
                      .invoke(beanFactory, (attributes) -> {
                        Field apiListingPluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "apiListingPlugins", PluginRegistry.class);
                        ReflectionUtils.makeAccessible(apiListingPluginsField);
                        ReflectionUtils.setField(apiListingPluginsField, bean, attributes.get(0));
                      });
                          instanceContext.field("parameterPlugins", PluginRegistry.class)
                              .invoke(beanFactory, (attributes) -> {
                                Field parameterPluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "parameterPlugins", PluginRegistry.class);
                                ReflectionUtils.makeAccessible(parameterPluginsField);
                                ReflectionUtils.setField(parameterPluginsField, bean, attributes.get(0));
                              });
                                  instanceContext.field("parameterExpanderPlugins", PluginRegistry.class)
                                      .invoke(beanFactory, (attributes) -> {
                                        Field parameterExpanderPluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "parameterExpanderPlugins", PluginRegistry.class);
                                        ReflectionUtils.makeAccessible(parameterExpanderPluginsField);
                                        ReflectionUtils.setField(parameterExpanderPluginsField, bean, attributes.get(0));
                                      });
                                          instanceContext.field("operationBuilderPlugins", PluginRegistry.class)
                                              .invoke(beanFactory, (attributes) -> {
                                                Field operationBuilderPluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "operationBuilderPlugins", PluginRegistry.class);
                                                ReflectionUtils.makeAccessible(operationBuilderPluginsField);
                                                ReflectionUtils.setField(operationBuilderPluginsField, bean, attributes.get(0));
                                              });
                                                  instanceContext.field("operationModelsProviders", PluginRegistry.class)
                                                      .invoke(beanFactory, (attributes) -> {
                                                        Field operationModelsProvidersField = ReflectionUtils.findField(DocumentationPluginsManager.class, "operationModelsProviders", PluginRegistry.class);
                                                        ReflectionUtils.makeAccessible(operationModelsProvidersField);
                                                        ReflectionUtils.setField(operationModelsProvidersField, bean, attributes.get(0));
                                                      });
                                                          instanceContext.field("defaultsProviders", PluginRegistry.class)
                                                              .invoke(beanFactory, (attributes) -> {
                                                                Field defaultsProvidersField = ReflectionUtils.findField(DocumentationPluginsManager.class, "defaultsProviders", PluginRegistry.class);
                                                                ReflectionUtils.makeAccessible(defaultsProvidersField);
                                                                ReflectionUtils.setField(defaultsProvidersField, bean, attributes.get(0));
                                                              });
                                                                  instanceContext.field("pathDecorators", PluginRegistry.class)
                                                                      .invoke(beanFactory, (attributes) -> {
                                                                        Field pathDecoratorsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "pathDecorators", PluginRegistry.class);
                                                                        ReflectionUtils.makeAccessible(pathDecoratorsField);
                                                                        ReflectionUtils.setField(pathDecoratorsField, bean, attributes.get(0));
                                                                      });
                                                                          instanceContext.field("apiListingScanners", PluginRegistry.class)
                                                                              .invoke(beanFactory, (attributes) -> {
                                                                                Field apiListingScannersField = ReflectionUtils.findField(DocumentationPluginsManager.class, "apiListingScanners", PluginRegistry.class);
                                                                                ReflectionUtils.makeAccessible(apiListingScannersField);
                                                                                ReflectionUtils.setField(apiListingScannersField, bean, attributes.get(0));
                                                                              });
                                                                                  instanceContext.field("responsePlugins", PluginRegistry.class)
                                                                                      .invoke(beanFactory, (attributes) -> {
                                                                                        Field responsePluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "responsePlugins", PluginRegistry.class);
                                                                                        ReflectionUtils.makeAccessible(responsePluginsField);
                                                                                        ReflectionUtils.setField(responsePluginsField, bean, attributes.get(0));
                                                                                      });
                                                                                          instanceContext.field("modelNameRegistryFactoryPlugins", PluginRegistry.class)
                                                                                              .invoke(beanFactory, (attributes) -> {
                                                                                                Field modelNameRegistryFactoryPluginsField = ReflectionUtils.findField(DocumentationPluginsManager.class, "modelNameRegistryFactoryPlugins", PluginRegistry.class);
                                                                                                ReflectionUtils.makeAccessible(modelNameRegistryFactoryPluginsField);
                                                                                                ReflectionUtils.setField(modelNameRegistryFactoryPluginsField, bean, attributes.get(0));
                                                                                              });
                                                                                                  return bean;
                                                                                                }).register(beanFactory);
                                                                                          }
                                                                                        }
