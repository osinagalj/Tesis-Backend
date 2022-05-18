package com.unicen.core.configuration;

import com.unicen.core.services.AuthenticationService;
import java.lang.reflect.Field;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.cors.CorsConfigurationSource;
import springfox.documentation.swagger.web.UiConfiguration;

public final class ContextBootstrapInitializer {
  public static void registerApiDocumentationConfiguration(DefaultListableBeanFactory beanFactory) {
    BeanDefinitionRegistrar.of("apiDocumentationConfiguration", ApiDocumentationConfiguration.class)
        .instanceSupplier((instanceContext) -> {
          ApiDocumentationConfiguration bean = new ApiDocumentationConfiguration();
          instanceContext.field("apiDocumentationConfiguration", EnsolversCoreApiDocumentationConfiguration.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> bean.apiDocumentationConfiguration = attributes.get(0));
                  return bean;
                }).register(beanFactory);
          }

          public static void registerApiDocumentationConfiguration_uiConfig(
              DefaultListableBeanFactory beanFactory) {
            BeanDefinitionRegistrar.of("uiConfig", UiConfiguration.class).withFactoryMethod(ApiDocumentationConfiguration.class, "uiConfig")
                .instanceSupplier(() -> beanFactory.getBean(ApiDocumentationConfiguration.class).uiConfig()).register(beanFactory);
          }

          public static void registerSecurityConfiguration_ApiDocumentationSecurityConfiguration(
              DefaultListableBeanFactory beanFactory) {
            BeanDefinitionRegistrar.of("com.unicen.core.configuration.SecurityConfiguration$ApiDocumentationSecurityConfiguration", SecurityConfiguration.ApiDocumentationSecurityConfiguration.class)
                .instanceSupplier((instanceContext) -> {
                  SecurityConfiguration.ApiDocumentationSecurityConfiguration bean = beanFactory.getBean(SecurityConfiguration.class).new ApiDocumentationSecurityConfiguration();
                  instanceContext.method("setApplicationContext", ApplicationContext.class)
                      .invoke(beanFactory, (attributes) -> bean.setApplicationContext(attributes.get(0)));
                  instanceContext.method("setObjectPostProcessor", ObjectPostProcessor.class)
                      .invoke(beanFactory, (attributes) -> bean.setObjectPostProcessor(attributes.get(0)));
                  instanceContext.method("setAuthenticationConfiguration", AuthenticationConfiguration.class)
                      .invoke(beanFactory, (attributes) -> bean.setAuthenticationConfiguration(attributes.get(0)));
                  instanceContext.method("setTrustResolver", AuthenticationTrustResolver.class)
                      .resolve(beanFactory, false).ifResolved((attributes) -> bean.setTrustResolver(attributes.get(0)));
                  instanceContext.method("setContentNegotationStrategy", ContentNegotiationStrategy.class)
                      .resolve(beanFactory, false).ifResolved((attributes) -> bean.setContentNegotationStrategy(attributes.get(0)));
                  instanceContext.field("apiDocumentationConfiguration", EnsolversCoreApiDocumentationConfiguration.class)
                      .resolve(beanFactory, false).ifResolved((attributes) -> {
                        Field apiDocumentationConfigurationField = ReflectionUtils.findField(SecurityConfiguration.ApiDocumentationSecurityConfiguration.class, "apiDocumentationConfiguration", EnsolversCoreApiDocumentationConfiguration.class);
                        ReflectionUtils.makeAccessible(apiDocumentationConfigurationField);
                        ReflectionUtils.setField(apiDocumentationConfigurationField, bean, attributes.get(0));
                      });
                          return bean;
                        }).register(beanFactory);
                  }

                  public static void registerSecurityConfiguration_CustomWebSecurityConfigurerAdapter(
                      DefaultListableBeanFactory beanFactory) {
                    BeanDefinitionRegistrar.of("com.unicen.core.configuration.SecurityConfiguration$CustomWebSecurityConfigurerAdapter", SecurityConfiguration.CustomWebSecurityConfigurerAdapter.class)
                        .instanceSupplier((instanceContext) -> {
                          SecurityConfiguration.CustomWebSecurityConfigurerAdapter bean = beanFactory.getBean(SecurityConfiguration.class).new CustomWebSecurityConfigurerAdapter();
                          instanceContext.method("setApplicationContext", ApplicationContext.class)
                              .invoke(beanFactory, (attributes) -> bean.setApplicationContext(attributes.get(0)));
                          instanceContext.method("setObjectPostProcessor", ObjectPostProcessor.class)
                              .invoke(beanFactory, (attributes) -> bean.setObjectPostProcessor(attributes.get(0)));
                          instanceContext.method("setAuthenticationConfiguration", AuthenticationConfiguration.class)
                              .invoke(beanFactory, (attributes) -> bean.setAuthenticationConfiguration(attributes.get(0)));
                          instanceContext.method("setTrustResolver", AuthenticationTrustResolver.class)
                              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setTrustResolver(attributes.get(0)));
                          instanceContext.method("setContentNegotationStrategy", ContentNegotiationStrategy.class)
                              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setContentNegotationStrategy(attributes.get(0)));
                          instanceContext.field("authenticationService", AuthenticationService.class)
                              .invoke(beanFactory, (attributes) -> {
                                Field authenticationServiceField = ReflectionUtils.findField(SecurityConfiguration.CustomWebSecurityConfigurerAdapter.class, "authenticationService", AuthenticationService.class);
                                ReflectionUtils.makeAccessible(authenticationServiceField);
                                ReflectionUtils.setField(authenticationServiceField, bean, attributes.get(0));
                              });
                                  instanceContext.field("securityConfiguration", EnsolversCoreSecurityConfiguration.class)
                                      .resolve(beanFactory, false).ifResolved((attributes) -> {
                                        Field securityConfigurationField = ReflectionUtils.findField(SecurityConfiguration.CustomWebSecurityConfigurerAdapter.class, "securityConfiguration", EnsolversCoreSecurityConfiguration.class);
                                        ReflectionUtils.makeAccessible(securityConfigurationField);
                                        ReflectionUtils.setField(securityConfigurationField, bean, attributes.get(0));
                                      });
                                          return bean;
                                        }).register(beanFactory);
                                  }

                                  public static void registerCustomWebSecurityConfigurerAdapter_corsConfigurationSource(
                                      DefaultListableBeanFactory beanFactory) {
                                    BeanDefinitionRegistrar.of("corsConfigurationSource", CorsConfigurationSource.class).withFactoryMethod(SecurityConfiguration.CustomWebSecurityConfigurerAdapter.class, "corsConfigurationSource")
                                        .instanceSupplier(() -> beanFactory.getBean(SecurityConfiguration.CustomWebSecurityConfigurerAdapter.class).corsConfigurationSource()).register(beanFactory);
                                  }

                                  public static void registerSecurityConfiguration_grantedAuthorityDefaults(
                                      DefaultListableBeanFactory beanFactory) {
                                    BeanDefinitionRegistrar.of("grantedAuthorityDefaults", GrantedAuthorityDefaults.class).withFactoryMethod(SecurityConfiguration.class, "grantedAuthorityDefaults")
                                        .instanceSupplier(() -> beanFactory.getBean(SecurityConfiguration.class).grantedAuthorityDefaults()).register(beanFactory);
                                  }
                                }
