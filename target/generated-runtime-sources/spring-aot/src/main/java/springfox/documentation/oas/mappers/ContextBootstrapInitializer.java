package springfox.documentation.oas.mappers;

import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerServiceModelToOpenApiMapperImpl(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("serviceModelToOpenApiMapperImpl", ServiceModelToOpenApiMapperImpl.class)
        .instanceSupplier((instanceContext) -> {
          ServiceModelToOpenApiMapperImpl bean = new ServiceModelToOpenApiMapperImpl();
          instanceContext.field("vendorExtensionsMapper", VendorExtensionsMapper.class)
              .invoke(beanFactory, (attributes) -> {
                Field vendorExtensionsMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "vendorExtensionsMapper", VendorExtensionsMapper.class);
                ReflectionUtils.makeAccessible(vendorExtensionsMapperField);
                ReflectionUtils.setField(vendorExtensionsMapperField, bean, attributes.get(0));
              });
                  instanceContext.field("licenseMapper", LicenseMapper.class)
                      .invoke(beanFactory, (attributes) -> {
                        Field licenseMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "licenseMapper", LicenseMapper.class);
                        ReflectionUtils.makeAccessible(licenseMapperField);
                        ReflectionUtils.setField(licenseMapperField, bean, attributes.get(0));
                      });
                          instanceContext.field("examplesMapper", ExamplesMapper.class)
                              .invoke(beanFactory, (attributes) -> {
                                Field examplesMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "examplesMapper", ExamplesMapper.class);
                                ReflectionUtils.makeAccessible(examplesMapperField);
                                ReflectionUtils.setField(examplesMapperField, bean, attributes.get(0));
                              });
                                  instanceContext.field("securityMapper", SecurityMapper.class)
                                      .invoke(beanFactory, (attributes) -> {
                                        Field securityMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "securityMapper", SecurityMapper.class);
                                        ReflectionUtils.makeAccessible(securityMapperField);
                                        ReflectionUtils.setField(securityMapperField, bean, attributes.get(0));
                                      });
                                          instanceContext.field("schemaMapper", SchemaMapper.class)
                                              .invoke(beanFactory, (attributes) -> {
                                                Field schemaMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "schemaMapper", SchemaMapper.class);
                                                ReflectionUtils.makeAccessible(schemaMapperField);
                                                ReflectionUtils.setField(schemaMapperField, bean, attributes.get(0));
                                              });
                                                  instanceContext.field("styleEnumMapper", StyleEnumMapper.class)
                                                      .invoke(beanFactory, (attributes) -> {
                                                        Field styleEnumMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "styleEnumMapper", StyleEnumMapper.class);
                                                        ReflectionUtils.makeAccessible(styleEnumMapperField);
                                                        ReflectionUtils.setField(styleEnumMapperField, bean, attributes.get(0));
                                                      });
                                                          instanceContext.field("securitySchemeMapper", SecuritySchemeMapper.class)
                                                              .invoke(beanFactory, (attributes) -> {
                                                                Field securitySchemeMapperField = ReflectionUtils.findField(ServiceModelToOpenApiMapperImpl.class, "securitySchemeMapper", SecuritySchemeMapper.class);
                                                                ReflectionUtils.makeAccessible(securitySchemeMapperField);
                                                                ReflectionUtils.setField(securitySchemeMapperField, bean, attributes.get(0));
                                                              });
                                                                  return bean;
                                                                }).register(beanFactory);
                                                          }
                                                        }
