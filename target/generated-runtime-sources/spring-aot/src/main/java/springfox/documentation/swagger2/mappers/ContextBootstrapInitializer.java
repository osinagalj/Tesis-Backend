package springfox.documentation.swagger2.mappers;

import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public final class ContextBootstrapInitializer {
  public static void registerServiceModelToSwagger2MapperImpl(
      DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("serviceModelToSwagger2MapperImpl", ServiceModelToSwagger2MapperImpl.class)
        .instanceSupplier((instanceContext) -> {
          ServiceModelToSwagger2MapperImpl bean = new ServiceModelToSwagger2MapperImpl();
          instanceContext.field("useModelV3", boolean.class)
              .invoke(beanFactory, (attributes) -> bean.useModelV3 = attributes.get(0));
                  instanceContext.field("compatibilityModelMapper", CompatibilityModelMapper.class)
                      .invoke(beanFactory, (attributes) -> {
                        Field compatibilityModelMapperField = ReflectionUtils.findField(ServiceModelToSwagger2MapperImpl.class, "compatibilityModelMapper", CompatibilityModelMapper.class);
                        ReflectionUtils.makeAccessible(compatibilityModelMapperField);
                        ReflectionUtils.setField(compatibilityModelMapperField, bean, attributes.get(0));
                      });
                          instanceContext.field("securityMapper", SecurityMapper.class)
                              .invoke(beanFactory, (attributes) -> {
                                Field securityMapperField = ReflectionUtils.findField(ServiceModelToSwagger2MapperImpl.class, "securityMapper", SecurityMapper.class);
                                ReflectionUtils.makeAccessible(securityMapperField);
                                ReflectionUtils.setField(securityMapperField, bean, attributes.get(0));
                              });
                                  instanceContext.field("licenseMapper", LicenseMapper.class)
                                      .invoke(beanFactory, (attributes) -> {
                                        Field licenseMapperField = ReflectionUtils.findField(ServiceModelToSwagger2MapperImpl.class, "licenseMapper", LicenseMapper.class);
                                        ReflectionUtils.makeAccessible(licenseMapperField);
                                        ReflectionUtils.setField(licenseMapperField, bean, attributes.get(0));
                                      });
                                          instanceContext.field("vendorExtensionsMapper", VendorExtensionsMapper.class)
                                              .invoke(beanFactory, (attributes) -> {
                                                Field vendorExtensionsMapperField = ReflectionUtils.findField(ServiceModelToSwagger2MapperImpl.class, "vendorExtensionsMapper", VendorExtensionsMapper.class);
                                                ReflectionUtils.makeAccessible(vendorExtensionsMapperField);
                                                ReflectionUtils.setField(vendorExtensionsMapperField, bean, attributes.get(0));
                                              });
                                                  return bean;
                                                }).register(beanFactory);
                                          }

                                          public static void registerCompatibilityModelMapperImpl(
                                              DefaultListableBeanFactory beanFactory) {
                                            BeanDefinitionRegistrar.of("compatibilityModelMapperImpl", CompatibilityModelMapperImpl.class)
                                                .instanceSupplier((instanceContext) -> {
                                                  CompatibilityModelMapperImpl bean = new CompatibilityModelMapperImpl();
                                                  instanceContext.field("useModelV3", boolean.class)
                                                      .invoke(beanFactory, (attributes) -> {
                                                        Field useModelV3Field = ReflectionUtils.findField(CompatibilityModelMapper.class, "useModelV3", boolean.class);
                                                        ReflectionUtils.makeAccessible(useModelV3Field);
                                                        ReflectionUtils.setField(useModelV3Field, bean, attributes.get(0));
                                                      });
                                                          return bean;
                                                        }).register(beanFactory);
                                                  }
                                                }
