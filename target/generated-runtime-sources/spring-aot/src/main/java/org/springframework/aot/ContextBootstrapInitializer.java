package org.springframework.aot;

import com.fasterxml.classmate.TypeResolver;
import com.unicen.core.configuration.ApiDocumentationConfiguration;
import com.unicen.core.configuration.DTOMapperConfiguration;
import com.unicen.core.configuration.PasswordRulesConfiguration;
import com.unicen.core.configuration.SecurityConfiguration;
import com.unicen.core.controller.ControllerExceptionHandler;
import com.unicen.core.controller.StatusController;
import com.unicen.core.dto.utils.DTOMapper;
import com.unicen.core.model.AccessRole;
import com.unicen.core.model.AuthenticationToken;
import com.unicen.core.model.User;
import com.unicen.core.model.ValidationCode;
import com.unicen.core.repositories.AccessRoleRepository;
import com.unicen.core.repositories.AuthenticationTokenRepository;
import com.unicen.core.repositories.RoleRepository;
import com.unicen.core.repositories.UserRepository;
import com.unicen.core.repositories.ValidationCodeRepository;
import com.unicen.core.security.BackendApplicationPasswordEncoder;
import com.unicen.core.security.UserPasswordEncoder;
import com.unicen.core.services.AccessRoleService;
import com.unicen.core.services.ApplicationPropertiesService;
import com.unicen.core.services.AuthenticationService;
import com.unicen.core.services.AuthenticationTokenService;
import com.unicen.core.services.EmailService;
import com.unicen.core.services.EnvironmentPropertiesService;
import com.unicen.core.services.SecretsService;
import com.unicen.core.services.UserService;
import com.unicen.core.services.ValidationCodeService;
import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import java.lang.Class;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import liquibase.integration.spring.SpringLiquibase;
import org.aopalliance.intercept.MethodInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aot.beans.factory.BeanDefinitionRegistrar;
import org.springframework.aot.context.annotation.ImportAwareBeanPostProcessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration;
import org.springframework.boot.autoconfigure.context.LifecycleProperties;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.TomcatWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.availability.ApplicationAvailabilityBean;
import org.springframework.boot.context.properties.BoundConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.jpa.repository.config.JpaMetamodelMappingContextFactoryBean;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.DefaultJpaContext;
import org.springframework.data.jpa.repository.support.EntityManagerBeanDefinitionRegistrarPostProcessor;
import org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.PropertiesBasedNamedQueries;
import org.springframework.data.repository.core.support.RepositoryFragmentsFactoryBean;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.ProjectingArgumentResolverRegistrar;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.support.PluginRegistryFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.AutowiredWebSecurityConfigurersIgnoreParents;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;
import springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties;
import springfox.boot.starter.autoconfigure.SwaggerUiWebMvcConfiguration;
import springfox.boot.starter.autoconfigure.SwaggerUiWebMvcConfigurer;
import springfox.documentation.OperationNameGenerator;
import springfox.documentation.PathProvider;
import springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration;
import springfox.documentation.oas.configuration.OpenApiMappingConfiguration;
import springfox.documentation.oas.configuration.OpenApiWebMvcConfiguration;
import springfox.documentation.oas.mappers.ExamplesMapperImpl;
import springfox.documentation.oas.mappers.OasLicenceMapper;
import springfox.documentation.oas.mappers.OasSecurityMapperImpl;
import springfox.documentation.oas.mappers.OasVendorExtensionsMapperImpl;
import springfox.documentation.oas.mappers.SchemaMapperImpl;
import springfox.documentation.oas.mappers.SecuritySchemeMapperImpl;
import springfox.documentation.oas.mappers.ServiceModelToOpenApiMapper;
import springfox.documentation.oas.mappers.StyleEnumMapperImpl;
import springfox.documentation.oas.web.OpenApiControllerWebMvc;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.schema.CachingModelDependencyProvider;
import springfox.documentation.schema.CachingModelProvider;
import springfox.documentation.schema.DefaultModelDependencyProvider;
import springfox.documentation.schema.DefaultModelProvider;
import springfox.documentation.schema.DefaultModelSpecificationProvider;
import springfox.documentation.schema.JacksonEnumTypeDeterminer;
import springfox.documentation.schema.JacksonJsonViewProvider;
import springfox.documentation.schema.ModelDependencyProvider;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.schema.ModelSpecificationProvider;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.configuration.ModelsConfiguration;
import springfox.documentation.schema.plugins.JsonIgnorePropertiesModelPlugin;
import springfox.documentation.schema.plugins.PropertyDiscriminatorBasedInheritancePlugin;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.plugins.XmlModelPlugin;
import springfox.documentation.schema.property.BeanPropertyNamingStrategy;
import springfox.documentation.schema.property.CachingModelPropertiesProvider;
import springfox.documentation.schema.property.FactoryMethodProvider;
import springfox.documentation.schema.property.ModelPropertiesProvider;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy;
import springfox.documentation.schema.property.OptimizedModelPropertiesProvider;
import springfox.documentation.schema.property.XmlPropertyPlugin;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin;
import springfox.documentation.spi.schema.TypeNameProviderPlugin;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.ModelNamesRegistryFactoryPlugin;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.RequestHandlerCombiner;
import springfox.documentation.spi.service.ResponseBuilderPlugin;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.WebMvcObjectMapperConfigurer;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.DefaultResponseTypeReader;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.spring.web.readers.operation.ApiOperationReader;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.spring.web.readers.operation.ContentParameterAggregator;
import springfox.documentation.spring.web.readers.operation.DefaultOperationReader;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.spring.web.readers.operation.OperationDeprecatedReader;
import springfox.documentation.spring.web.readers.operation.OperationModelsProvider;
import springfox.documentation.spring.web.readers.operation.OperationParameterHeadersConditionReader;
import springfox.documentation.spring.web.readers.operation.OperationParameterRequestConditionReader;
import springfox.documentation.spring.web.readers.operation.OperationReader;
import springfox.documentation.spring.web.readers.operation.OperationResponseClassReader;
import springfox.documentation.spring.web.readers.operation.OperationTagsReader;
import springfox.documentation.spring.web.readers.operation.ResponseMessagesReader;
import springfox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder;
import springfox.documentation.spring.web.readers.parameter.ParameterDataTypeReader;
import springfox.documentation.spring.web.readers.parameter.ParameterDefaultReader;
import springfox.documentation.spring.web.readers.parameter.ParameterMultiplesReader;
import springfox.documentation.spring.web.readers.parameter.ParameterNameReader;
import springfox.documentation.spring.web.readers.parameter.ParameterRequiredReader;
import springfox.documentation.spring.web.readers.parameter.ParameterTypeReader;
import springfox.documentation.spring.web.scanners.ApiDescriptionLookup;
import springfox.documentation.spring.web.scanners.ApiDescriptionReader;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;
import springfox.documentation.spring.web.scanners.ApiListingReader;
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner;
import springfox.documentation.spring.web.scanners.ApiListingScanner;
import springfox.documentation.spring.web.scanners.ApiModelReader;
import springfox.documentation.spring.web.scanners.ApiModelSpecificationReader;
import springfox.documentation.spring.web.scanners.CachingOperationReader;
import springfox.documentation.spring.web.scanners.MediaTypeReader;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;
import springfox.documentation.swagger.readers.operation.OpenApiOperationAuthReader;
import springfox.documentation.swagger.readers.operation.OpenApiOperationTagsReader;
import springfox.documentation.swagger.readers.operation.OpenApiResponseReader;
import springfox.documentation.swagger.readers.operation.OperationAuthReader;
import springfox.documentation.swagger.readers.operation.OperationHiddenReader;
import springfox.documentation.swagger.readers.operation.OperationHttpMethodReader;
import springfox.documentation.swagger.readers.operation.OperationImplicitParameterReader;
import springfox.documentation.swagger.readers.operation.OperationImplicitParametersReader;
import springfox.documentation.swagger.readers.operation.OperationNicknameIntoUniqueIdReader;
import springfox.documentation.swagger.readers.operation.OperationNotesReader;
import springfox.documentation.swagger.readers.operation.OperationPositionReader;
import springfox.documentation.swagger.readers.operation.OperationSummaryReader;
import springfox.documentation.swagger.readers.operation.SwaggerMediaTypeReader;
import springfox.documentation.swagger.readers.operation.SwaggerOperationModelsProvider;
import springfox.documentation.swagger.readers.operation.SwaggerOperationResponseClassReader;
import springfox.documentation.swagger.readers.operation.SwaggerOperationTagsReader;
import springfox.documentation.swagger.readers.operation.SwaggerResponseMessageReader;
import springfox.documentation.swagger.readers.operation.VendorExtensionsReader;
import springfox.documentation.swagger.readers.parameter.ApiParamParameterBuilder;
import springfox.documentation.swagger.readers.parameter.OpenApiParameterBuilder;
import springfox.documentation.swagger.readers.parameter.SwaggerExpandedParameterBuilder;
import springfox.documentation.swagger.schema.ApiModelBuilder;
import springfox.documentation.swagger.schema.ApiModelPropertyPropertyBuilder;
import springfox.documentation.swagger.schema.ApiModelTypeNameProvider;
import springfox.documentation.swagger.schema.OpenApiSchemaPropertyBuilder;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerApiListingReader;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2WebMvcConfiguration;
import springfox.documentation.swagger2.mappers.LicenseMapperImpl;
import springfox.documentation.swagger2.mappers.ModelMapperImpl;
import springfox.documentation.swagger2.mappers.ModelSpecificationMapperImpl;
import springfox.documentation.swagger2.mappers.ParameterMapperImpl;
import springfox.documentation.swagger2.mappers.PropertyMapperImpl;
import springfox.documentation.swagger2.mappers.RequestParameterMapperImpl;
import springfox.documentation.swagger2.mappers.SecurityMapperImpl;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.mappers.VendorExtensionsMapperImpl;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;
import springfox.documentation.swagger2.web.WebMvcSwaggerTransformationFilter;

public class ContextBootstrapInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  private ImportAwareBeanPostProcessor createImportAwareBeanPostProcessor() {
    Map<String, String> mappings = new LinkedHashMap<>();
    mappings.put("org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration", "com.unicen.core.configuration.SecurityConfiguration");
    mappings.put("org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration", "com.unicen.core.configuration.SecurityConfiguration");
    mappings.put("org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration", "org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration$JdkDynamicAutoProxyConfiguration");
    return new ImportAwareBeanPostProcessor(mappings);
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    // infrastructure
    DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
    beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
    beanFactory.addBeanPostProcessor(createImportAwareBeanPostProcessor());

    BeanDefinitionRegistrar.of("org.springframework.context.annotation.internalPersistenceAnnotationProcessor", PersistenceAnnotationBeanPostProcessor.class)
        .instanceSupplier(PersistenceAnnotationBeanPostProcessor::new).customize((bd) -> bd.setRole(2)).register(beanFactory);
    com.unicen.ContextBootstrapInitializer.registerAppApplication(beanFactory);
    com.unicen.app.controller.ContextBootstrapInitializer.registerSendEmail(beanFactory);
    com.unicen.core.configuration.ContextBootstrapInitializer.registerApiDocumentationConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("DTOMapperConfiguration", DTOMapperConfiguration.class)
        .instanceSupplier(DTOMapperConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("securityConfiguration", SecurityConfiguration.class)
        .instanceSupplier(SecurityConfiguration::new).register(beanFactory);
    com.unicen.core.controller.ContextBootstrapInitializer.registerAuthController(beanFactory);
    BeanDefinitionRegistrar.of("controllerExceptionHandler", ControllerExceptionHandler.class)
        .instanceSupplier(ControllerExceptionHandler::new).register(beanFactory);
    BeanDefinitionRegistrar.of("statusController", StatusController.class)
        .instanceSupplier(StatusController::new).register(beanFactory);
    com.unicen.core.controller.ContextBootstrapInitializer.registerUserController(beanFactory);
    BeanDefinitionRegistrar.of("backendApplicationPasswordEncoder", BackendApplicationPasswordEncoder.class).withConstructor(PasswordEncoder.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new BackendApplicationPasswordEncoder(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("accessRoleService", AccessRoleService.class).withConstructor(AccessRoleRepository.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new AccessRoleService(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("applicationPropertiesService", ApplicationPropertiesService.class).withConstructor(Environment.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApplicationPropertiesService(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("authenticationService", AuthenticationService.class).withConstructor(AuthenticationTokenRepository.class, UserService.class, ValidationCodeService.class, ApplicationPropertiesService.class, EmailService.class, PasswordRulesConfiguration.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new AuthenticationService(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4), attributes.get(5)))).register(beanFactory);
    BeanDefinitionRegistrar.of("authenticationTokenService", AuthenticationTokenService.class).withConstructor(AuthenticationTokenRepository.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new AuthenticationTokenService(attributes.get(0)))).register(beanFactory);
    com.unicen.core.services.ContextBootstrapInitializer.registerEmailService(beanFactory);
    BeanDefinitionRegistrar.of("environmentPropertiesService", EnvironmentPropertiesService.class).withConstructor(Environment.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new EnvironmentPropertiesService(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("secretsService", SecretsService.class)
        .instanceSupplier(SecretsService::new).register(beanFactory);
    BeanDefinitionRegistrar.of("userService", UserService.class).withConstructor(UserRepository.class, UserPasswordEncoder.class, AccessRoleService.class, AuthenticationTokenService.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new UserService(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("validationCodeService", ValidationCodeService.class).withConstructor(ValidationCodeRepository.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ValidationCodeService(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("globalApplicationContext", GlobalApplicationContext.class)
        .instanceSupplier(GlobalApplicationContext::new).register(beanFactory);
    springfox.documentation.swagger2.mappers.ContextBootstrapInitializer.registerServiceModelToSwagger2MapperImpl(beanFactory);
    BeanDefinitionRegistrar.of("vendorExtensionsMapperImpl", VendorExtensionsMapperImpl.class)
        .instanceSupplier(VendorExtensionsMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("propertyMapperImpl", PropertyMapperImpl.class)
        .instanceSupplier(PropertyMapperImpl::new).register(beanFactory);
    springfox.documentation.swagger2.mappers.ContextBootstrapInitializer.registerCompatibilityModelMapperImpl(beanFactory);
    BeanDefinitionRegistrar.of("parameterMapperImpl", ParameterMapperImpl.class)
        .instanceSupplier(ParameterMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("modelMapperImpl", ModelMapperImpl.class)
        .instanceSupplier(ModelMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("requestParameterMapperImpl", RequestParameterMapperImpl.class)
        .instanceSupplier(RequestParameterMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("modelSpecificationMapperImpl", ModelSpecificationMapperImpl.class)
        .instanceSupplier(ModelSpecificationMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("licenseMapperImpl", LicenseMapperImpl.class)
        .instanceSupplier(LicenseMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("securityMapperImpl", SecurityMapperImpl.class)
        .instanceSupplier(SecurityMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("swagger2ControllerWebMvc", Swagger2ControllerWebMvc.class).withConstructor(DocumentationCache.class, ServiceModelToSwagger2Mapper.class, JsonSerializer.class, PluginRegistry.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new Swagger2ControllerWebMvc(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiSchemaPropertyBuilder", OpenApiSchemaPropertyBuilder.class).withConstructor(DescriptionResolver.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OpenApiSchemaPropertyBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("apiModelPropertyPropertyBuilder", ApiModelPropertyPropertyBuilder.class).withConstructor(DescriptionResolver.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiModelPropertyPropertyBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("apiModelTypeNameProvider", ApiModelTypeNameProvider.class)
        .instanceSupplier(ApiModelTypeNameProvider::new).register(beanFactory);
    BeanDefinitionRegistrar.of("apiModelBuilder", ApiModelBuilder.class).withConstructor(TypeResolver.class, TypeNameExtractor.class, EnumTypeDeterminer.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiModelBuilder(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("operationImplicitParameterReader", OperationImplicitParameterReader.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationImplicitParameterReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("vendorExtensionsReader", VendorExtensionsReader.class)
        .instanceSupplier(VendorExtensionsReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerOperationResponseClassReader", SwaggerOperationResponseClassReader.class).withConstructor(TypeResolver.class, EnumTypeDeterminer.class, TypeNameExtractor.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SwaggerOperationResponseClassReader(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerOperationModelsProvider", SwaggerOperationModelsProvider.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SwaggerOperationModelsProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiOperationAuthReader", OpenApiOperationAuthReader.class)
        .instanceSupplier(OpenApiOperationAuthReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerMediaTypeReader", SwaggerMediaTypeReader.class)
        .instanceSupplier(SwaggerMediaTypeReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationHttpMethodReader", OperationHttpMethodReader.class)
        .instanceSupplier(OperationHttpMethodReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationImplicitParametersReader", OperationImplicitParametersReader.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationImplicitParametersReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("operationAuthReader", OperationAuthReader.class)
        .instanceSupplier(OperationAuthReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationHiddenReader", OperationHiddenReader.class)
        .instanceSupplier(OperationHiddenReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationSummaryReader", OperationSummaryReader.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationSummaryReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiOperationTagsReader", OpenApiOperationTagsReader.class)
        .instanceSupplier(OpenApiOperationTagsReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerResponseMessageReader", SwaggerResponseMessageReader.class).withConstructor(EnumTypeDeterminer.class, TypeNameExtractor.class, TypeResolver.class, ModelSpecificationFactory.class, DocumentationPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SwaggerResponseMessageReader(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4)))).register(beanFactory);
    BeanDefinitionRegistrar.of("operationNicknameIntoUniqueIdReader", OperationNicknameIntoUniqueIdReader.class)
        .instanceSupplier(OperationNicknameIntoUniqueIdReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationPositionReader", OperationPositionReader.class)
        .instanceSupplier(OperationPositionReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiResponseReader", OpenApiResponseReader.class).withConstructor(TypeResolver.class, ModelSpecificationFactory.class, DocumentationPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OpenApiResponseReader(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("operationNotesReader", OperationNotesReader.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationNotesReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerOperationTagsReader", SwaggerOperationTagsReader.class)
        .instanceSupplier(SwaggerOperationTagsReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerParameterDescriptionReader", ApiParamParameterBuilder.class).withConstructor(DescriptionResolver.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiParamParameterBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerExpandedParameterBuilder", SwaggerExpandedParameterBuilder.class).withConstructor(DescriptionResolver.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SwaggerExpandedParameterBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiParameterBuilder", OpenApiParameterBuilder.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OpenApiParameterBuilder(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("swaggerApiListingReader", SwaggerApiListingReader.class)
        .instanceSupplier(SwaggerApiListingReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("inMemorySwaggerResourcesProvider", InMemorySwaggerResourcesProvider.class).withConstructor(Environment.class, DocumentationCache.class, DocumentationPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new InMemorySwaggerResourcesProvider(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    springfox.documentation.swagger.web.ContextBootstrapInitializer.registerApiResourceController(beanFactory);
    BeanDefinitionRegistrar.of("apiListingReferenceScanner", ApiListingReferenceScanner.class)
        .instanceSupplier(ApiListingReferenceScanner::new).register(beanFactory);
    BeanDefinitionRegistrar.of("apiDocumentationScanner", ApiDocumentationScanner.class).withConstructor(ApiListingReferenceScanner.class, ApiListingScanner.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiDocumentationScanner(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("apiDescriptionReader", ApiDescriptionReader.class).withConstructor(OperationReader.class, DocumentationPluginsManager.class, ApiDescriptionLookup.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiDescriptionReader(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("apiListingReader", ApiListingReader.class)
        .instanceSupplier(ApiListingReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("apiModelSpecificationReader", ApiModelSpecificationReader.class).withConstructor(ModelSpecificationProvider.class, DocumentationPluginsManager.class, TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiModelSpecificationReader(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("cachingOperationReader", CachingOperationReader.class).withConstructor(OperationReader.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new CachingOperationReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("mediaTypeReader", MediaTypeReader.class)
        .instanceSupplier(MediaTypeReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("apiListingScanner", ApiListingScanner.class).withConstructor(ApiDescriptionReader.class, ApiModelReader.class, ApiModelSpecificationReader.class, DocumentationPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiListingScanner(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("apiModelReader", ApiModelReader.class).withConstructor(ModelProvider.class, TypeResolver.class, DocumentationPluginsManager.class, EnumTypeDeterminer.class, TypeNameExtractor.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiModelReader(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4)))).register(beanFactory);
    BeanDefinitionRegistrar.of("apiDescriptionLookup", ApiDescriptionLookup.class)
        .instanceSupplier(ApiDescriptionLookup::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationModelsProvider", OperationModelsProvider.class).withConstructor(SchemaPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationModelsProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("operationDeprecatedReader", OperationDeprecatedReader.class)
        .instanceSupplier(OperationDeprecatedReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("responseMessagesReader", ResponseMessagesReader.class).withConstructor(EnumTypeDeterminer.class, TypeNameExtractor.class, SchemaPluginsManager.class, ModelSpecificationFactory.class, DocumentationPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ResponseMessagesReader(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4)))).register(beanFactory);
    springfox.documentation.spring.web.readers.operation.ContextBootstrapInitializer.registerOperationParameterReader(beanFactory);
    BeanDefinitionRegistrar.of("operationTagsReader", OperationTagsReader.class)
        .instanceSupplier(OperationTagsReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("apiOperationReader", ApiOperationReader.class).withConstructor(DocumentationPluginsManager.class, OperationNameGenerator.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ApiOperationReader(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultOperationReader", DefaultOperationReader.class)
        .instanceSupplier(DefaultOperationReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationParameterRequestConditionReader", OperationParameterRequestConditionReader.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationParameterRequestConditionReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("operationParameterHeadersConditionReader", OperationParameterHeadersConditionReader.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationParameterHeadersConditionReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("contentParameterAggregator", ContentParameterAggregator.class)
        .instanceSupplier(ContentParameterAggregator::new).register(beanFactory);
    BeanDefinitionRegistrar.of("operationResponseClassReader", OperationResponseClassReader.class).withConstructor(SchemaPluginsManager.class, EnumTypeDeterminer.class, TypeNameExtractor.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OperationResponseClassReader(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("cachingOperationNameGenerator", CachingOperationNameGenerator.class)
        .instanceSupplier(CachingOperationNameGenerator::new).register(beanFactory);
    BeanDefinitionRegistrar.of("parameterMultiplesReader", ParameterMultiplesReader.class)
        .instanceSupplier(ParameterMultiplesReader::new).register(beanFactory);
    springfox.documentation.spring.web.readers.parameter.ContextBootstrapInitializer.registerModelAttributeParameterExpander(beanFactory);
    BeanDefinitionRegistrar.of("parameterTypeReader", ParameterTypeReader.class)
        .instanceSupplier(ParameterTypeReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("parameterRequiredReader", ParameterRequiredReader.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ParameterRequiredReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("parameterDataTypeReader", ParameterDataTypeReader.class).withConstructor(SchemaPluginsManager.class, TypeNameExtractor.class, EnumTypeDeterminer.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ParameterDataTypeReader(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("parameterDefaultReader", ParameterDefaultReader.class).withConstructor(DescriptionResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ParameterDefaultReader(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("parameterNameReader", ParameterNameReader.class)
        .instanceSupplier(ParameterNameReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("expandedParameterBuilder", ExpandedParameterBuilder.class).withConstructor(TypeResolver.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ExpandedParameterBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultResponseTypeReader", DefaultResponseTypeReader.class)
        .instanceSupplier(DefaultResponseTypeReader::new).register(beanFactory);
    BeanDefinitionRegistrar.of("documentationPluginsBootstrapper", DocumentationPluginsBootstrapper.class).withConstructor(DocumentationPluginsManager.class, List.class, DocumentationCache.class, ApiDocumentationScanner.class, TypeResolver.class, Defaults.class, PathProvider.class, Environment.class)
        .instanceSupplier((instanceContext) -> {
          DocumentationPluginsBootstrapper bean = instanceContext.create(beanFactory, (attributes) -> new DocumentationPluginsBootstrapper(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4), attributes.get(5), attributes.get(6), attributes.get(7)));
          instanceContext.method("setCombiner", RequestHandlerCombiner.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setCombiner(attributes.get(0)));
          instanceContext.method("setTypeConventions", List.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setTypeConventions(attributes.get(0)));
          return bean;
        }).register(beanFactory);
    springfox.documentation.spring.web.plugins.ContextBootstrapInitializer.registerDocumentationPluginsManager(beanFactory);
    BeanDefinitionRegistrar.of("webMvcRequestHandlerProvider", WebMvcRequestHandlerProvider.class).withConstructor(Optional.class, HandlerMethodResolver.class, List.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new WebMvcRequestHandlerProvider(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    springfox.documentation.spring.web.paths.ContextBootstrapInitializer.registerQueryStringUriTemplateDecorator(beanFactory);
    springfox.documentation.spring.web.paths.ContextBootstrapInitializer.registerPathMappingDecorator(beanFactory);
    springfox.documentation.spring.web.paths.ContextBootstrapInitializer.registerPathSanitizer(beanFactory);
    springfox.documentation.spring.web.paths.ContextBootstrapInitializer.registerOperationPathDecorator(beanFactory);
    BeanDefinitionRegistrar.of("jacksonJsonViewProvider", JacksonJsonViewProvider.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new JacksonJsonViewProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("cachingModelDependencyProvider", CachingModelDependencyProvider.class).withConstructor(ModelDependencyProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new CachingModelDependencyProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("typeNameExtractor", TypeNameExtractor.class).withConstructor(TypeResolver.class, PluginRegistry.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new TypeNameExtractor(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("propertyDiscriminatorBasedInheritancePlugin", PropertyDiscriminatorBasedInheritancePlugin.class).withConstructor(TypeResolver.class, EnumTypeDeterminer.class, TypeNameExtractor.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new PropertyDiscriminatorBasedInheritancePlugin(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("xmlModelPlugin", XmlModelPlugin.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new XmlModelPlugin(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("schemaPluginsManager", SchemaPluginsManager.class).withConstructor(PluginRegistry.class, PluginRegistry.class, PluginRegistry.class, PluginRegistry.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SchemaPluginsManager(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("jsonIgnorePropertiesModelPlugin", JsonIgnorePropertiesModelPlugin.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new JsonIgnorePropertiesModelPlugin(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("cachingModelPropertiesProvider", CachingModelPropertiesProvider.class).withConstructor(TypeResolver.class, ModelPropertiesProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new CachingModelPropertiesProvider(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("objectMapperBeanPropertyNamingStrategy", ObjectMapperBeanPropertyNamingStrategy.class)
        .instanceSupplier(ObjectMapperBeanPropertyNamingStrategy::new).register(beanFactory);
    BeanDefinitionRegistrar.of("accessorsProvider", AccessorsProvider.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new AccessorsProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("fieldProvider", FieldProvider.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new FieldProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("xmlPropertyPlugin", XmlPropertyPlugin.class)
        .instanceSupplier(XmlPropertyPlugin::new).register(beanFactory);
    BeanDefinitionRegistrar.of("optimized", OptimizedModelPropertiesProvider.class).withConstructor(AccessorsProvider.class, FieldProvider.class, FactoryMethodProvider.class, TypeResolver.class, BeanPropertyNamingStrategy.class, SchemaPluginsManager.class, EnumTypeDeterminer.class, TypeNameExtractor.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OptimizedModelPropertiesProvider(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4), attributes.get(5), attributes.get(6), attributes.get(7), attributes.get(8)))).customize((bd) -> bd.setPrimary(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("factoryMethodProvider", FactoryMethodProvider.class).withConstructor(TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new FactoryMethodProvider(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("modelSpecificationFactory", ModelSpecificationFactory.class).withConstructor(TypeNameExtractor.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ModelSpecificationFactory(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("cachingModelProvider", CachingModelProvider.class).withConstructor(ModelProvider.class, ModelSpecificationProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new CachingModelProvider(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultModelDependencyProvider", DefaultModelDependencyProvider.class).withConstructor(TypeResolver.class, ModelPropertiesProvider.class, TypeNameExtractor.class, EnumTypeDeterminer.class, SchemaPluginsManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new DefaultModelDependencyProvider(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4)))).register(beanFactory);
    BeanDefinitionRegistrar.of("jacksonEnumTypeDeterminer", JacksonEnumTypeDeterminer.class)
        .instanceSupplier(JacksonEnumTypeDeterminer::new).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultModelProvider", DefaultModelProvider.class).withConstructor(ModelPropertiesProvider.class, ModelDependencyProvider.class, SchemaPluginsManager.class, TypeNameExtractor.class, EnumTypeDeterminer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new DefaultModelProvider(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4)))).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultModelSpecificationProvider", DefaultModelSpecificationProvider.class).withConstructor(TypeResolver.class, ModelPropertiesProvider.class, ModelDependencyProvider.class, SchemaPluginsManager.class, TypeNameExtractor.class, EnumTypeDeterminer.class, ModelSpecificationFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new DefaultModelSpecificationProvider(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4), attributes.get(5), attributes.get(6)))).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiControllerWebMvc", OpenApiControllerWebMvc.class).withConstructor(DocumentationCache.class, ServiceModelToOpenApiMapper.class, JsonSerializer.class, PluginRegistry.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new OpenApiControllerWebMvc(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(3)))).register(beanFactory);
    BeanDefinitionRegistrar.of("oasVendorExtensionsMapperImpl", OasVendorExtensionsMapperImpl.class)
        .instanceSupplier(OasVendorExtensionsMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("styleEnumMapperImpl", StyleEnumMapperImpl.class)
        .instanceSupplier(StyleEnumMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("securitySchemeMapperImpl", SecuritySchemeMapperImpl.class)
        .instanceSupplier(SecuritySchemeMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("oasSecurityMapperImpl", OasSecurityMapperImpl.class)
        .instanceSupplier(OasSecurityMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("schemaMapperImpl", SchemaMapperImpl.class)
        .instanceSupplier(SchemaMapperImpl::new).register(beanFactory);
    BeanDefinitionRegistrar.of("examplesMapperImpl", ExamplesMapperImpl.class)
        .instanceSupplier(ExamplesMapperImpl::new).register(beanFactory);
    springfox.documentation.oas.mappers.ContextBootstrapInitializer.registerServiceModelToOpenApiMapperImpl(beanFactory);
    BeanDefinitionRegistrar.of("oasLicenceMapper", OasLicenceMapper.class)
        .instanceSupplier(OasLicenceMapper::new).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.swagger.configuration.SwaggerCommonConfiguration", SwaggerCommonConfiguration.class)
        .instanceSupplier(SwaggerCommonConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.schema.configuration.ModelsConfiguration", ModelsConfiguration.class)
        .instanceSupplier(ModelsConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("typeResolver", TypeResolver.class).withFactoryMethod(ModelsConfiguration.class, "typeResolver")
        .instanceSupplier(() -> beanFactory.getBean(ModelsConfiguration.class).typeResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("modelBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ModelBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ModelBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("modelPropertyBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ModelPropertyBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ModelPropertyBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("typeNameProviderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, TypeNameProviderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", TypeNameProviderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("syntheticModelProviderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, SyntheticModelProviderPlugin.class, ModelContext.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", SyntheticModelProviderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("viewProviderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ViewProviderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ViewProviderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.spring.web.SpringfoxWebConfiguration", SpringfoxWebConfiguration.class)
        .instanceSupplier(SpringfoxWebConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("defaults", Defaults.class).withFactoryMethod(SpringfoxWebConfiguration.class, "defaults")
        .instanceSupplier(() -> beanFactory.getBean(SpringfoxWebConfiguration.class).defaults()).register(beanFactory);
    BeanDefinitionRegistrar.of("resourceGroupCache", DocumentationCache.class).withFactoryMethod(SpringfoxWebConfiguration.class, "resourceGroupCache")
        .instanceSupplier(() -> beanFactory.getBean(SpringfoxWebConfiguration.class).resourceGroupCache()).register(beanFactory);
    BeanDefinitionRegistrar.of("jsonSerializer", JsonSerializer.class).withFactoryMethod(SpringfoxWebConfiguration.class, "jsonSerializer", List.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(SpringfoxWebConfiguration.class).jsonSerializer(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("descriptionResolver", DescriptionResolver.class).withFactoryMethod(SpringfoxWebConfiguration.class, "descriptionResolver", Environment.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(SpringfoxWebConfiguration.class).descriptionResolver(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("methodResolver", HandlerMethodResolver.class).withFactoryMethod(SpringfoxWebConfiguration.class, "methodResolver", TypeResolver.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(SpringfoxWebConfiguration.class).methodResolver(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("pathProvider", PathProvider.class).withFactoryMethod(SpringfoxWebConfiguration.class, "pathProvider")
        .instanceSupplier(() -> beanFactory.getBean(SpringfoxWebConfiguration.class).pathProvider()).register(beanFactory);
    BeanDefinitionRegistrar.of("documentationPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, DocumentationPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", DocumentationPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("apiListingBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ApiListingBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ApiListingBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("operationBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, OperationBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", OperationBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("parameterBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ParameterBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ParameterBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("responseBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ResponseBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ResponseBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("expandedParameterBuilderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ExpandedParameterBuilderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ExpandedParameterBuilderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("operationModelsProviderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, OperationModelsProviderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", OperationModelsProviderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultsProviderPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, DefaultsProviderPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", DefaultsProviderPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("pathDecoratorRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, PathDecorator.class, DocumentationContext.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", PathDecorator.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("apiListingScannerPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ApiListingScannerPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ApiListingScannerPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("modelNamesRegistryFactoryPluginRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, ModelNamesRegistryFactoryPlugin.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", ModelNamesRegistryFactoryPlugin.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.spring.web.SpringfoxWebMvcConfiguration", SpringfoxWebMvcConfiguration.class)
        .instanceSupplier(SpringfoxWebMvcConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("webMvcObjectMapperConfigurer", WebMvcObjectMapperConfigurer.class).withFactoryMethod(SpringfoxWebMvcConfiguration.class, "webMvcObjectMapperConfigurer")
        .instanceSupplier(() -> SpringfoxWebMvcConfiguration.webMvcObjectMapperConfigurer()).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.swagger2.configuration.Swagger2WebMvcConfiguration", Swagger2WebMvcConfiguration.class)
        .instanceSupplier(Swagger2WebMvcConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("webMvcSwaggerTransformer", WebMvcSwaggerTransformationFilter.class).withFactoryMethod(Swagger2WebMvcConfiguration.class, "webMvcSwaggerTransformer", Environment.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(Swagger2WebMvcConfiguration.class).webMvcSwaggerTransformer(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("webMvcSwaggerTransformationFilterRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, WebMvcSwaggerTransformationFilter.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", WebMvcSwaggerTransformationFilter.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration", Swagger2DocumentationConfiguration.class)
        .instanceSupplier(Swagger2DocumentationConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("swagger2Module", JacksonModuleRegistrar.class).withFactoryMethod(Swagger2DocumentationConfiguration.class, "swagger2Module")
        .instanceSupplier(() -> beanFactory.getBean(Swagger2DocumentationConfiguration.class).swagger2Module()).register(beanFactory);
    BeanDefinitionRegistrar.of("api", Docket.class).withFactoryMethod(ApiDocumentationConfiguration.class, "api")
        .instanceSupplier(() -> beanFactory.getBean(ApiDocumentationConfiguration.class).api()).register(beanFactory);
    com.unicen.core.configuration.ContextBootstrapInitializer.registerApiDocumentationConfiguration_uiConfig(beanFactory);
    BeanDefinitionRegistrar.of("dtoMapper", ModelMapper.class).withFactoryMethod(DTOMapperConfiguration.class, "dtoMapper")
        .instanceSupplier(() -> beanFactory.getBean(DTOMapperConfiguration.class).dtoMapper()).register(beanFactory);
    BeanDefinitionRegistrar.of("mapper", DTOMapper.class).withFactoryMethod(DTOMapperConfiguration.class, "mapper")
        .instanceSupplier(() -> beanFactory.getBean(DTOMapperConfiguration.class).mapper()).register(beanFactory);
    com.unicen.core.configuration.ContextBootstrapInitializer.registerSecurityConfiguration_ApiDocumentationSecurityConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("userDetailsService", UserDetailsService.class).withFactoryMethod(SecurityConfiguration.ApiDocumentationSecurityConfiguration.class, "userDetailsService")
        .instanceSupplier(() -> beanFactory.getBean(SecurityConfiguration.ApiDocumentationSecurityConfiguration.class).userDetailsService()).register(beanFactory);
    com.unicen.core.configuration.ContextBootstrapInitializer.registerSecurityConfiguration_CustomWebSecurityConfigurerAdapter(beanFactory);
    com.unicen.core.configuration.ContextBootstrapInitializer.registerCustomWebSecurityConfigurerAdapter_corsConfigurationSource(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration", ObjectPostProcessorConfiguration.class)
        .instanceSupplier(ObjectPostProcessorConfiguration::new).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("objectPostProcessor", ResolvableType.forClassWithGenerics(ObjectPostProcessor.class, Object.class)).withFactoryMethod(ObjectPostProcessorConfiguration.class, "objectPostProcessor", AutowireCapableBeanFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(ObjectPostProcessorConfiguration.class).objectPostProcessor(attributes.get(0)))).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration", AuthenticationConfiguration.class)
        .instanceSupplier((instanceContext) -> {
          AuthenticationConfiguration bean = new AuthenticationConfiguration();
          instanceContext.method("setApplicationContext", ApplicationContext.class)
              .invoke(beanFactory, (attributes) -> bean.setApplicationContext(attributes.get(0)));
          instanceContext.method("setObjectPostProcessor", ObjectPostProcessor.class)
              .invoke(beanFactory, (attributes) -> bean.setObjectPostProcessor(attributes.get(0)));
          instanceContext.method("setGlobalAuthenticationConfigurers", List.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setGlobalAuthenticationConfigurers(attributes.get(0)));
          return bean;
        }).register(beanFactory);
    BeanDefinitionRegistrar.of("authenticationManagerBuilder", AuthenticationManagerBuilder.class).withFactoryMethod(AuthenticationConfiguration.class, "authenticationManagerBuilder", ObjectPostProcessor.class, ApplicationContext.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(AuthenticationConfiguration.class).authenticationManagerBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("enableGlobalAuthenticationAutowiredConfigurer", GlobalAuthenticationConfigurerAdapter.class).withFactoryMethod(AuthenticationConfiguration.class, "enableGlobalAuthenticationAutowiredConfigurer", ApplicationContext.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> AuthenticationConfiguration.enableGlobalAuthenticationAutowiredConfigurer(attributes.get(0)))).register(beanFactory);
    org.springframework.security.config.annotation.authentication.configuration.ContextBootstrapInitializer.registerAuthenticationConfiguration_initializeUserDetailsBeanManagerConfigurer(beanFactory);
    org.springframework.security.config.annotation.authentication.configuration.ContextBootstrapInitializer.registerAuthenticationConfiguration_initializeAuthenticationProviderBeanManagerConfigurer(beanFactory);
    org.springframework.security.config.annotation.web.configuration.ContextBootstrapInitializer.registerWebSecurityConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("delegatingApplicationListener", DelegatingApplicationListener.class).withFactoryMethod(WebSecurityConfiguration.class, "delegatingApplicationListener")
        .instanceSupplier(() -> WebSecurityConfiguration.delegatingApplicationListener()).register(beanFactory);
    BeanDefinitionRegistrar.of("webSecurityExpressionHandler", ResolvableType.forClassWithGenerics(SecurityExpressionHandler.class, FilterInvocation.class)).withFactoryMethod(WebSecurityConfiguration.class, "webSecurityExpressionHandler")
        .instanceSupplier(() -> beanFactory.getBean(WebSecurityConfiguration.class).webSecurityExpressionHandler()).customize((bd) -> bd.setDependsOn(new String[] { "springSecurityFilterChain" })).register(beanFactory);
    BeanDefinitionRegistrar.of("springSecurityFilterChain", Filter.class).withFactoryMethod(WebSecurityConfiguration.class, "springSecurityFilterChain")
        .instanceSupplier(() -> beanFactory.getBean(WebSecurityConfiguration.class).springSecurityFilterChain()).register(beanFactory);
    BeanDefinitionRegistrar.of("privilegeEvaluator", WebInvocationPrivilegeEvaluator.class).withFactoryMethod(WebSecurityConfiguration.class, "privilegeEvaluator")
        .instanceSupplier(() -> beanFactory.getBean(WebSecurityConfiguration.class).privilegeEvaluator()).customize((bd) -> bd.setDependsOn(new String[] { "springSecurityFilterChain" })).register(beanFactory);
    BeanDefinitionRegistrar.of("conversionServicePostProcessor", BeanFactoryPostProcessor.class).withFactoryMethod(WebSecurityConfiguration.class, "conversionServicePostProcessor")
        .instanceSupplier(() -> WebSecurityConfiguration.conversionServicePostProcessor()).register(beanFactory);
    BeanDefinitionRegistrar.of("autowiredWebSecurityConfigurersIgnoreParents", AutowiredWebSecurityConfigurersIgnoreParents.class).withFactoryMethod(WebSecurityConfiguration.class, "autowiredWebSecurityConfigurersIgnoreParents", ConfigurableListableBeanFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> WebSecurityConfiguration.autowiredWebSecurityConfigurersIgnoreParents(attributes.get(0)))).register(beanFactory);
    org.springframework.security.config.annotation.web.configuration.ContextBootstrapInitializer.registerWebMvcSecurityConfiguration(beanFactory);
    org.springframework.security.config.annotation.web.configuration.ContextBootstrapInitializer.registerWebMvcSecurityConfiguration_requestDataValueProcessor(beanFactory);
    org.springframework.security.config.annotation.web.configuration.ContextBootstrapInitializer.registerHttpSecurityConfiguration(beanFactory);
    org.springframework.security.config.annotation.web.configuration.ContextBootstrapInitializer.registerHttpSecurityConfiguration_httpSecurity(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration", GlobalMethodSecurityConfiguration.class)
        .instanceSupplier((instanceContext) -> {
          GlobalMethodSecurityConfiguration bean = new GlobalMethodSecurityConfiguration();
          instanceContext.method("setObjectPostProcessor", ObjectPostProcessor.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setObjectPostProcessor(attributes.get(0)));
          instanceContext.method("setMethodSecurityExpressionHandler", List.class)
              .resolve(beanFactory, false).ifResolved((attributes) -> bean.setMethodSecurityExpressionHandler(attributes.get(0)));
          return bean;
        }).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("methodSecurityInterceptor", MethodInterceptor.class).withFactoryMethod(GlobalMethodSecurityConfiguration.class, "methodSecurityInterceptor", MethodSecurityMetadataSource.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(GlobalMethodSecurityConfiguration.class).methodSecurityInterceptor(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("methodSecurityMetadataSource", MethodSecurityMetadataSource.class).withFactoryMethod(GlobalMethodSecurityConfiguration.class, "methodSecurityMetadataSource")
        .instanceSupplier(() -> beanFactory.getBean(GlobalMethodSecurityConfiguration.class).methodSecurityMetadataSource()).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("preInvocationAuthorizationAdvice", PreInvocationAuthorizationAdvice.class).withFactoryMethod(GlobalMethodSecurityConfiguration.class, "preInvocationAuthorizationAdvice")
        .instanceSupplier(() -> beanFactory.getBean(GlobalMethodSecurityConfiguration.class).preInvocationAuthorizationAdvice()).register(beanFactory);
    BeanDefinitionRegistrar.of("encoder", PasswordEncoder.class).withFactoryMethod(SecurityConfiguration.class, "encoder")
        .instanceSupplier(() -> beanFactory.getBean(SecurityConfiguration.class).encoder()).register(beanFactory);
    com.unicen.core.configuration.ContextBootstrapInitializer.registerSecurityConfiguration_grantedAuthorityDefaults(beanFactory);
    BeanDefinitionRegistrar.of("metaDataSourceAdvisor", MethodSecurityMetadataSourceAdvisor.class).withConstructor(String.class, MethodSecurityMetadataSource.class, String.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new MethodSecurityMetadataSourceAdvisor(attributes.get(0), attributes.get(1), attributes.get(2)))).customize((bd) -> {
      bd.setRole(2);
      ConstructorArgumentValues argumentValues = bd.getConstructorArgumentValues();
      argumentValues.addIndexedArgumentValue(0, "methodSecurityInterceptor");
      argumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference("methodSecurityMetadataSource"));
      argumentValues.addIndexedArgumentValue(2, "methodSecurityMetadataSource");
      bd.getPropertyValues().addPropertyValue("order", 2147483647);
    }).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.aop.config.internalAutoProxyCreator", AnnotationAwareAspectJAutoProxyCreator.class)
        .instanceSupplier(AnnotationAwareAspectJAutoProxyCreator::new).customize((bd) -> {
      bd.setRole(2);
      bd.getPropertyValues().addPropertyValue("order", -2147483648);
    }).register(beanFactory);
    org.springframework.boot.autoconfigure.ContextBootstrapInitializer.registerAutoConfigurationPackages_BasePackages(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration", PropertyPlaceholderAutoConfiguration.class)
        .instanceSupplier(PropertyPlaceholderAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("propertySourcesPlaceholderConfigurer", PropertySourcesPlaceholderConfigurer.class).withFactoryMethod(PropertyPlaceholderAutoConfiguration.class, "propertySourcesPlaceholderConfigurer")
        .instanceSupplier(() -> PropertyPlaceholderAutoConfiguration.propertySourcesPlaceholderConfigurer()).register(beanFactory);
    org.springframework.boot.autoconfigure.websocket.servlet.ContextBootstrapInitializer.registerWebSocketServletAutoConfiguration_TomcatWebSocketConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.websocket.servlet.ContextBootstrapInitializer.registerTomcatWebSocketConfiguration_websocketServletWebServerCustomizer(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration", WebSocketServletAutoConfiguration.class)
        .instanceSupplier(WebSocketServletAutoConfiguration::new).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerServletWebServerFactoryConfiguration_EmbeddedTomcat(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerEmbeddedTomcat_tomcatServletWebServerFactory(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration", ServletWebServerFactoryAutoConfiguration.class)
        .instanceSupplier(ServletWebServerFactoryAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("servletWebServerFactoryCustomizer", ServletWebServerFactoryCustomizer.class).withFactoryMethod(ServletWebServerFactoryAutoConfiguration.class, "servletWebServerFactoryCustomizer", ServerProperties.class, ObjectProvider.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(ServletWebServerFactoryAutoConfiguration.class).servletWebServerFactoryCustomizer(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("tomcatServletWebServerFactoryCustomizer", TomcatServletWebServerFactoryCustomizer.class).withFactoryMethod(ServletWebServerFactoryAutoConfiguration.class, "tomcatServletWebServerFactoryCustomizer", ServerProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(ServletWebServerFactoryAutoConfiguration.class).tomcatServletWebServerFactoryCustomizer(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor", ConfigurationPropertiesBindingPostProcessor.class)
        .instanceSupplier(ConfigurationPropertiesBindingPostProcessor::new).customize((bd) -> bd.setRole(2)).register(beanFactory);
    org.springframework.boot.context.properties.ContextBootstrapInitializer.registerConfigurationPropertiesBinder_Factory(beanFactory);
    org.springframework.boot.context.properties.ContextBootstrapInitializer.registerConfigurationPropertiesBinder(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.context.properties.BoundConfigurationProperties", BoundConfigurationProperties.class)
        .instanceSupplier(BoundConfigurationProperties::new).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.context.properties.EnableConfigurationPropertiesRegistrar.methodValidationExcludeFilter", MethodValidationExcludeFilter.class)
        .instanceSupplier(() -> MethodValidationExcludeFilter.byAnnotation(ConfigurationProperties.class)).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("server-org.springframework.boot.autoconfigure.web.ServerProperties", ServerProperties.class)
        .instanceSupplier(ServerProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor.class)
        .instanceSupplier(WebServerFactoryCustomizerBeanPostProcessor::new).customize((bd) -> bd.setSynthetic(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("errorPageRegistrarBeanPostProcessor", ErrorPageRegistrarBeanPostProcessor.class)
        .instanceSupplier(ErrorPageRegistrarBeanPostProcessor::new).customize((bd) -> bd.setSynthetic(true)).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerDispatcherServletAutoConfiguration_DispatcherServletConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerDispatcherServletConfiguration_dispatcherServlet(beanFactory);
    BeanDefinitionRegistrar.of("spring.mvc-org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties", WebMvcProperties.class)
        .instanceSupplier(WebMvcProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerDispatcherServletAutoConfiguration_DispatcherServletRegistrationConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerDispatcherServletRegistrationConfiguration_dispatcherServletRegistration(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration", DispatcherServletAutoConfiguration.class)
        .instanceSupplier(DispatcherServletAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration", TaskExecutionAutoConfiguration.class)
        .instanceSupplier(TaskExecutionAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("taskExecutorBuilder", TaskExecutorBuilder.class).withFactoryMethod(TaskExecutionAutoConfiguration.class, "taskExecutorBuilder", TaskExecutionProperties.class, ObjectProvider.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(TaskExecutionAutoConfiguration.class).taskExecutorBuilder(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("applicationTaskExecutor", ThreadPoolTaskExecutor.class).withFactoryMethod(TaskExecutionAutoConfiguration.class, "applicationTaskExecutor", TaskExecutorBuilder.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(TaskExecutionAutoConfiguration.class).applicationTaskExecutor(attributes.get(0)))).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.task.execution-org.springframework.boot.autoconfigure.task.TaskExecutionProperties", TaskExecutionProperties.class)
        .instanceSupplier(TaskExecutionProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerErrorMvcAutoConfiguration_WhitelabelErrorViewConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerWhitelabelErrorViewConfiguration_error(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerWhitelabelErrorViewConfiguration_beanNameViewResolver(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerErrorMvcAutoConfiguration_DefaultErrorViewResolverConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerDefaultErrorViewResolverConfiguration_conventionErrorViewResolver(beanFactory);
    BeanDefinitionRegistrar.of("spring.web-org.springframework.boot.autoconfigure.web.WebProperties", WebProperties.class)
        .instanceSupplier(WebProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration", ErrorMvcAutoConfiguration.class).withConstructor(ServerProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ErrorMvcAutoConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("errorAttributes", DefaultErrorAttributes.class).withFactoryMethod(ErrorMvcAutoConfiguration.class, "errorAttributes")
        .instanceSupplier(() -> beanFactory.getBean(ErrorMvcAutoConfiguration.class).errorAttributes()).register(beanFactory);
    BeanDefinitionRegistrar.of("basicErrorController", BasicErrorController.class).withFactoryMethod(ErrorMvcAutoConfiguration.class, "basicErrorController", ErrorAttributes.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(ErrorMvcAutoConfiguration.class).basicErrorController(attributes.get(0), attributes.get(1)))).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerErrorMvcAutoConfiguration_errorPageCustomizer(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.error.ContextBootstrapInitializer.registerErrorMvcAutoConfiguration_preserveErrorControllerTargetClassPostProcessor(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerWebMvcAutoConfiguration_EnableWebMvcConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("requestMappingHandlerAdapter", RequestMappingHandlerAdapter.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "requestMappingHandlerAdapter", ContentNegotiationManager.class, FormattingConversionService.class, Validator.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).requestMappingHandlerAdapter(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("requestMappingHandlerMapping", RequestMappingHandlerMapping.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "requestMappingHandlerMapping", ContentNegotiationManager.class, FormattingConversionService.class, ResourceUrlProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).requestMappingHandlerMapping(attributes.get(0), attributes.get(1), attributes.get(2)))).customize((bd) -> bd.setPrimary(true)).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerEnableWebMvcConfiguration_welcomePageHandlerMapping(beanFactory);
    BeanDefinitionRegistrar.of("localeResolver", LocaleResolver.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "localeResolver")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).localeResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("themeResolver", ThemeResolver.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "themeResolver")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).themeResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("flashMapManager", FlashMapManager.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "flashMapManager")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).flashMapManager()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcConversionService", FormattingConversionService.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "mvcConversionService")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).mvcConversionService()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcValidator", Validator.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "mvcValidator")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).mvcValidator()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcContentNegotiationManager", ContentNegotiationManager.class).withFactoryMethod(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class, "mvcContentNegotiationManager")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.EnableWebMvcConfiguration.class).mvcContentNegotiationManager()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcPatternParser", PathPatternParser.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcPatternParser")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcPatternParser()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcUrlPathHelper", UrlPathHelper.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcUrlPathHelper")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcUrlPathHelper()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcPathMatcher", PathMatcher.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcPathMatcher")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcPathMatcher()).register(beanFactory);
    BeanDefinitionRegistrar.of("viewControllerHandlerMapping", HandlerMapping.class).withFactoryMethod(WebMvcConfigurationSupport.class, "viewControllerHandlerMapping", FormattingConversionService.class, ResourceUrlProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).viewControllerHandlerMapping(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("beanNameHandlerMapping", BeanNameUrlHandlerMapping.class).withFactoryMethod(WebMvcConfigurationSupport.class, "beanNameHandlerMapping", FormattingConversionService.class, ResourceUrlProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).beanNameHandlerMapping(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("routerFunctionMapping", RouterFunctionMapping.class).withFactoryMethod(WebMvcConfigurationSupport.class, "routerFunctionMapping", FormattingConversionService.class, ResourceUrlProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).routerFunctionMapping(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("resourceHandlerMapping", HandlerMapping.class).withFactoryMethod(WebMvcConfigurationSupport.class, "resourceHandlerMapping", ContentNegotiationManager.class, FormattingConversionService.class, ResourceUrlProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).resourceHandlerMapping(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcResourceUrlProvider", ResourceUrlProvider.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcResourceUrlProvider")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcResourceUrlProvider()).register(beanFactory);
    BeanDefinitionRegistrar.of("defaultServletHandlerMapping", HandlerMapping.class).withFactoryMethod(WebMvcConfigurationSupport.class, "defaultServletHandlerMapping")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).defaultServletHandlerMapping()).register(beanFactory);
    BeanDefinitionRegistrar.of("handlerFunctionAdapter", HandlerFunctionAdapter.class).withFactoryMethod(WebMvcConfigurationSupport.class, "handlerFunctionAdapter")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).handlerFunctionAdapter()).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcUriComponentsContributor", CompositeUriComponentsContributor.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcUriComponentsContributor", FormattingConversionService.class, RequestMappingHandlerAdapter.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcUriComponentsContributor(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("httpRequestHandlerAdapter", HttpRequestHandlerAdapter.class).withFactoryMethod(WebMvcConfigurationSupport.class, "httpRequestHandlerAdapter")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).httpRequestHandlerAdapter()).register(beanFactory);
    BeanDefinitionRegistrar.of("simpleControllerHandlerAdapter", SimpleControllerHandlerAdapter.class).withFactoryMethod(WebMvcConfigurationSupport.class, "simpleControllerHandlerAdapter")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).simpleControllerHandlerAdapter()).register(beanFactory);
    BeanDefinitionRegistrar.of("handlerExceptionResolver", HandlerExceptionResolver.class).withFactoryMethod(WebMvcConfigurationSupport.class, "handlerExceptionResolver", ContentNegotiationManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).handlerExceptionResolver(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcViewResolver", ViewResolver.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcViewResolver", ContentNegotiationManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcViewResolver(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("mvcHandlerMappingIntrospector", HandlerMappingIntrospector.class).withFactoryMethod(WebMvcConfigurationSupport.class, "mvcHandlerMappingIntrospector")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).mvcHandlerMappingIntrospector()).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("viewNameTranslator", RequestToViewNameTranslator.class).withFactoryMethod(WebMvcConfigurationSupport.class, "viewNameTranslator")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcConfigurationSupport.class).viewNameTranslator()).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerWebMvcAutoConfiguration_WebMvcAutoConfigurationAdapter(beanFactory);
    BeanDefinitionRegistrar.of("defaultViewResolver", InternalResourceViewResolver.class).withFactoryMethod(WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.class, "defaultViewResolver")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.class).defaultViewResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("viewResolver", ContentNegotiatingViewResolver.class).withFactoryMethod(WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.class, "viewResolver", BeanFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.class).viewResolver(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("requestContextFilter", RequestContextFilter.class).withFactoryMethod(WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.class, "requestContextFilter")
        .instanceSupplier(() -> WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.requestContextFilter()).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration", WebMvcAutoConfiguration.class)
        .instanceSupplier(WebMvcAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("formContentFilter", OrderedFormContentFilter.class).withFactoryMethod(WebMvcAutoConfiguration.class, "formContentFilter")
        .instanceSupplier(() -> beanFactory.getBean(WebMvcAutoConfiguration.class).formContentFilter()).register(beanFactory);
    org.springframework.boot.autoconfigure.aop.ContextBootstrapInitializer.registerAspectJAutoProxyingConfiguration_JdkDynamicAutoProxyConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.aop.ContextBootstrapInitializer.registerAopAutoConfiguration_AspectJAutoProxyingConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.aop.AopAutoConfiguration", AopAutoConfiguration.class)
        .instanceSupplier(AopAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration", ApplicationAvailabilityAutoConfiguration.class)
        .instanceSupplier(ApplicationAvailabilityAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("applicationAvailability", ApplicationAvailabilityBean.class).withFactoryMethod(ApplicationAvailabilityAutoConfiguration.class, "applicationAvailability")
        .instanceSupplier(() -> beanFactory.getBean(ApplicationAvailabilityAutoConfiguration.class).applicationAvailability()).register(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJacksonAutoConfiguration_Jackson2ObjectMapperBuilderCustomizerConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJackson2ObjectMapperBuilderCustomizerConfiguration_standardJacksonObjectMapperBuilderCustomizer(beanFactory);
    BeanDefinitionRegistrar.of("spring.jackson-org.springframework.boot.autoconfigure.jackson.JacksonProperties", JacksonProperties.class)
        .instanceSupplier(JacksonProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJacksonAutoConfiguration_JacksonObjectMapperBuilderConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJacksonObjectMapperBuilderConfiguration_jacksonObjectMapperBuilder(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJacksonAutoConfiguration_ParameterNamesModuleConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerParameterNamesModuleConfiguration_parameterNamesModule(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJacksonAutoConfiguration_JacksonObjectMapperConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jackson.ContextBootstrapInitializer.registerJacksonObjectMapperConfiguration_jacksonObjectMapper(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration", JacksonAutoConfiguration.class)
        .instanceSupplier(JacksonAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("jsonComponentModule", JsonComponentModule.class).withFactoryMethod(JacksonAutoConfiguration.class, "jsonComponentModule")
        .instanceSupplier(() -> beanFactory.getBean(JacksonAutoConfiguration.class).jsonComponentModule()).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration", JtaAutoConfiguration.class)
        .instanceSupplier(JtaAutoConfiguration::new).register(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerDataSourceConfiguration_Hikari(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerHikari_dataSource(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerDataSourceJmxConfiguration_Hikari(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerDataSourceJmxConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerDataSourceAutoConfiguration_PooledDataSourceConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.metadata.ContextBootstrapInitializer.registerDataSourcePoolMetadataProvidersConfiguration_HikariPoolDataSourceMetadataProviderConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.metadata.ContextBootstrapInitializer.registerHikariPoolDataSourceMetadataProviderConfiguration_hikariPoolDataSourceMetadataProvider(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration", DataSourcePoolMetadataProvidersConfiguration.class)
        .instanceSupplier(DataSourcePoolMetadataProvidersConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration", DataSourceAutoConfiguration.class)
        .instanceSupplier(DataSourceAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.datasource-org.springframework.boot.autoconfigure.jdbc.DataSourceProperties", DataSourceProperties.class)
        .instanceSupplier(DataSourceProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.orm.jpa.ContextBootstrapInitializer.registerJpaBaseConfiguration_JpaWebConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.orm.jpa.ContextBootstrapInitializer.registerJpaWebConfiguration_openEntityManagerInViewInterceptor(beanFactory);
    org.springframework.boot.autoconfigure.orm.jpa.ContextBootstrapInitializer.registerJpaWebConfiguration_openEntityManagerInViewInterceptorConfigurer(beanFactory);
    org.springframework.boot.autoconfigure.orm.jpa.ContextBootstrapInitializer.registerHibernateJpaConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("transactionManager", PlatformTransactionManager.class).withFactoryMethod(JpaBaseConfiguration.class, "transactionManager", ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(JpaBaseConfiguration.class).transactionManager(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("jpaVendorAdapter", JpaVendorAdapter.class).withFactoryMethod(JpaBaseConfiguration.class, "jpaVendorAdapter")
        .instanceSupplier(() -> beanFactory.getBean(JpaBaseConfiguration.class).jpaVendorAdapter()).register(beanFactory);
    BeanDefinitionRegistrar.of("entityManagerFactoryBuilder", EntityManagerFactoryBuilder.class).withFactoryMethod(JpaBaseConfiguration.class, "entityManagerFactoryBuilder", JpaVendorAdapter.class, ObjectProvider.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(JpaBaseConfiguration.class).entityManagerFactoryBuilder(attributes.get(0), attributes.get(1), attributes.get(2)))).register(beanFactory);
    BeanDefinitionRegistrar.of("entityManagerFactory", LocalContainerEntityManagerFactoryBean.class).withFactoryMethod(JpaBaseConfiguration.class, "entityManagerFactory", EntityManagerFactoryBuilder.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(JpaBaseConfiguration.class).entityManagerFactory(attributes.get(0)))).customize((bd) -> {
      bd.setPrimary(true);
      bd.setDependsOn(new String[] { "liquibase", "dataSourceScriptDatabaseInitializer" });
    }).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.jpa.hibernate-org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties", HibernateProperties.class)
        .instanceSupplier(HibernateProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.jpa-org.springframework.boot.autoconfigure.orm.jpa.JpaProperties", JpaProperties.class)
        .instanceSupplier(JpaProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", HibernateJpaAutoConfiguration.class)
        .instanceSupplier(HibernateJpaAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration", ConfigurationPropertiesAutoConfiguration.class)
        .instanceSupplier(ConfigurationPropertiesAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration", LifecycleAutoConfiguration.class)
        .instanceSupplier(LifecycleAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("lifecycleProcessor", DefaultLifecycleProcessor.class).withFactoryMethod(LifecycleAutoConfiguration.class, "defaultLifecycleProcessor", LifecycleProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(LifecycleAutoConfiguration.class).defaultLifecycleProcessor(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.lifecycle-org.springframework.boot.autoconfigure.context.LifecycleProperties", LifecycleProperties.class)
        .instanceSupplier(LifecycleProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration", PersistenceExceptionTranslationAutoConfiguration.class)
        .instanceSupplier(PersistenceExceptionTranslationAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("persistenceExceptionTranslationPostProcessor", PersistenceExceptionTranslationPostProcessor.class).withFactoryMethod(PersistenceExceptionTranslationAutoConfiguration.class, "persistenceExceptionTranslationPostProcessor", Environment.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> PersistenceExceptionTranslationAutoConfiguration.persistenceExceptionTranslationPostProcessor(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration", JpaRepositoriesAutoConfiguration.class)
        .instanceSupplier(JpaRepositoriesAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("emBeanDefinitionRegistrarPostProcessor", EntityManagerBeanDefinitionRegistrarPostProcessor.class)
        .instanceSupplier(EntityManagerBeanDefinitionRegistrarPostProcessor::new).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("jpaMappingContext", JpaMetamodelMappingContextFactoryBean.class)
        .instanceSupplier(JpaMetamodelMappingContextFactoryBean::new).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("jpaContext", DefaultJpaContext.class).withConstructor(Set.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new DefaultJpaContext(attributes.get(0)))).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    org.springframework.data.jpa.util.ContextBootstrapInitializer.registerJpaMetamodelCacheCleanup(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension", JpaEvaluationContextExtension.class).withConstructor(char.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new JpaEvaluationContextExtension(attributes.get(0)))).customize((bd) -> bd.getConstructorArgumentValues().addIndexedArgumentValue(0, '\\')).register(beanFactory);
    BeanDefinitionRegistrar.of("accessRoleRepository", ResolvableType.forClassWithGenerics(JpaRepositoryFactoryBean.class, AccessRoleRepository.class, AccessRole.class, Long.class)).withConstructor(Class.class)
        .instanceSupplier((instanceContext) -> {
          JpaRepositoryFactoryBean bean = instanceContext.create(beanFactory, (attributes) -> new JpaRepositoryFactoryBean(attributes.get(0)));
          instanceContext.method("setEntityPathResolver", ObjectProvider.class)
              .invoke(beanFactory, (attributes) -> bean.setEntityPathResolver(attributes.get(0)));
          instanceContext.method("setQueryMethodFactory", JpaQueryMethodFactory.class)
              .invoke(beanFactory, (attributes) -> bean.setQueryMethodFactory(attributes.get(0)));
          return bean;
        }).customize((bd) -> {
      bd.getConstructorArgumentValues().addIndexedArgumentValue(0, "com.unicen.core.repositories.AccessRoleRepository");
      MutablePropertyValues propertyValues = bd.getPropertyValues();
      propertyValues.addPropertyValue("queryLookupStrategyKey", QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND);
      propertyValues.addPropertyValue("lazyInit", false);
      propertyValues.addPropertyValue("namedQueries", BeanDefinitionRegistrar.inner(PropertiesBasedNamedQueries.class).withConstructor(Properties.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new PropertiesBasedNamedQueries(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, BeanDefinitionRegistrar.inner(PropertiesFactoryBean.class)
          .instanceSupplier(PropertiesFactoryBean::new).customize((bd__) -> {
        MutablePropertyValues propertyValues__ = bd__.getPropertyValues();
        propertyValues__.addPropertyValue("locations", "classpath*:META-INF/jpa-named-queries.properties");
        propertyValues__.addPropertyValue("ignoreResourceNotFound", true);
      }).toBeanDefinition())).toBeanDefinition());
      propertyValues.addPropertyValue("repositoryFragments", BeanDefinitionRegistrar.inner(RepositoryFragmentsFactoryBean.class).withConstructor(List.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new RepositoryFragmentsFactoryBean(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, Collections.emptyList())).toBeanDefinition());
      propertyValues.addPropertyValue("transactionManager", "transactionManager");
      propertyValues.addPropertyValue("entityManager", BeanDefinitionRegistrar.inner(SharedEntityManagerCreator.class).withFactoryMethod(SharedEntityManagerCreator.class, "createSharedEntityManager", EntityManagerFactory.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> SharedEntityManagerCreator.createSharedEntityManager(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("entityManagerFactory"))).toBeanDefinition());
      propertyValues.addPropertyValue("escapeCharacter", '\\');
      propertyValues.addPropertyValue("mappingContext", new RuntimeBeanReference("jpaMappingContext"));
      propertyValues.addPropertyValue("enableDefaultTransactions", true);
    }).register(beanFactory);
    BeanDefinitionRegistrar.of("roleRepository", ResolvableType.forClassWithGenerics(JpaRepositoryFactoryBean.class, RoleRepository.class, AccessRole.class, Long.class)).withConstructor(Class.class)
        .instanceSupplier((instanceContext) -> {
          JpaRepositoryFactoryBean bean = instanceContext.create(beanFactory, (attributes) -> new JpaRepositoryFactoryBean(attributes.get(0)));
          instanceContext.method("setEntityPathResolver", ObjectProvider.class)
              .invoke(beanFactory, (attributes) -> bean.setEntityPathResolver(attributes.get(0)));
          instanceContext.method("setQueryMethodFactory", JpaQueryMethodFactory.class)
              .invoke(beanFactory, (attributes) -> bean.setQueryMethodFactory(attributes.get(0)));
          return bean;
        }).customize((bd) -> {
      bd.getConstructorArgumentValues().addIndexedArgumentValue(0, "com.unicen.core.repositories.RoleRepository");
      MutablePropertyValues propertyValues = bd.getPropertyValues();
      propertyValues.addPropertyValue("queryLookupStrategyKey", QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND);
      propertyValues.addPropertyValue("lazyInit", false);
      propertyValues.addPropertyValue("namedQueries", BeanDefinitionRegistrar.inner(PropertiesBasedNamedQueries.class).withConstructor(Properties.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new PropertiesBasedNamedQueries(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, BeanDefinitionRegistrar.inner(PropertiesFactoryBean.class)
          .instanceSupplier(PropertiesFactoryBean::new).customize((bd__) -> {
        MutablePropertyValues propertyValues__ = bd__.getPropertyValues();
        propertyValues__.addPropertyValue("locations", "classpath*:META-INF/jpa-named-queries.properties");
        propertyValues__.addPropertyValue("ignoreResourceNotFound", true);
      }).toBeanDefinition())).toBeanDefinition());
      propertyValues.addPropertyValue("repositoryFragments", BeanDefinitionRegistrar.inner(RepositoryFragmentsFactoryBean.class).withConstructor(List.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new RepositoryFragmentsFactoryBean(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, Collections.emptyList())).toBeanDefinition());
      propertyValues.addPropertyValue("transactionManager", "transactionManager");
      propertyValues.addPropertyValue("entityManager", BeanDefinitionRegistrar.inner(SharedEntityManagerCreator.class).withFactoryMethod(SharedEntityManagerCreator.class, "createSharedEntityManager", EntityManagerFactory.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> SharedEntityManagerCreator.createSharedEntityManager(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("entityManagerFactory"))).toBeanDefinition());
      propertyValues.addPropertyValue("escapeCharacter", '\\');
      propertyValues.addPropertyValue("mappingContext", new RuntimeBeanReference("jpaMappingContext"));
      propertyValues.addPropertyValue("enableDefaultTransactions", true);
    }).register(beanFactory);
    BeanDefinitionRegistrar.of("validationCodeRepository", ResolvableType.forClassWithGenerics(JpaRepositoryFactoryBean.class, ValidationCodeRepository.class, ValidationCode.class, Long.class)).withConstructor(Class.class)
        .instanceSupplier((instanceContext) -> {
          JpaRepositoryFactoryBean bean = instanceContext.create(beanFactory, (attributes) -> new JpaRepositoryFactoryBean(attributes.get(0)));
          instanceContext.method("setEntityPathResolver", ObjectProvider.class)
              .invoke(beanFactory, (attributes) -> bean.setEntityPathResolver(attributes.get(0)));
          instanceContext.method("setQueryMethodFactory", JpaQueryMethodFactory.class)
              .invoke(beanFactory, (attributes) -> bean.setQueryMethodFactory(attributes.get(0)));
          return bean;
        }).customize((bd) -> {
      bd.getConstructorArgumentValues().addIndexedArgumentValue(0, "com.unicen.core.repositories.ValidationCodeRepository");
      MutablePropertyValues propertyValues = bd.getPropertyValues();
      propertyValues.addPropertyValue("queryLookupStrategyKey", QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND);
      propertyValues.addPropertyValue("lazyInit", false);
      propertyValues.addPropertyValue("namedQueries", BeanDefinitionRegistrar.inner(PropertiesBasedNamedQueries.class).withConstructor(Properties.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new PropertiesBasedNamedQueries(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, BeanDefinitionRegistrar.inner(PropertiesFactoryBean.class)
          .instanceSupplier(PropertiesFactoryBean::new).customize((bd__) -> {
        MutablePropertyValues propertyValues__ = bd__.getPropertyValues();
        propertyValues__.addPropertyValue("locations", "classpath*:META-INF/jpa-named-queries.properties");
        propertyValues__.addPropertyValue("ignoreResourceNotFound", true);
      }).toBeanDefinition())).toBeanDefinition());
      propertyValues.addPropertyValue("repositoryFragments", BeanDefinitionRegistrar.inner(RepositoryFragmentsFactoryBean.class).withConstructor(List.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new RepositoryFragmentsFactoryBean(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, Collections.emptyList())).toBeanDefinition());
      propertyValues.addPropertyValue("transactionManager", "transactionManager");
      propertyValues.addPropertyValue("entityManager", BeanDefinitionRegistrar.inner(SharedEntityManagerCreator.class).withFactoryMethod(SharedEntityManagerCreator.class, "createSharedEntityManager", EntityManagerFactory.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> SharedEntityManagerCreator.createSharedEntityManager(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("entityManagerFactory"))).toBeanDefinition());
      propertyValues.addPropertyValue("escapeCharacter", '\\');
      propertyValues.addPropertyValue("mappingContext", new RuntimeBeanReference("jpaMappingContext"));
      propertyValues.addPropertyValue("enableDefaultTransactions", true);
    }).register(beanFactory);
    BeanDefinitionRegistrar.of("authenticationTokenRepository", ResolvableType.forClassWithGenerics(JpaRepositoryFactoryBean.class, AuthenticationTokenRepository.class, AuthenticationToken.class, Long.class)).withConstructor(Class.class)
        .instanceSupplier((instanceContext) -> {
          JpaRepositoryFactoryBean bean = instanceContext.create(beanFactory, (attributes) -> new JpaRepositoryFactoryBean(attributes.get(0)));
          instanceContext.method("setEntityPathResolver", ObjectProvider.class)
              .invoke(beanFactory, (attributes) -> bean.setEntityPathResolver(attributes.get(0)));
          instanceContext.method("setQueryMethodFactory", JpaQueryMethodFactory.class)
              .invoke(beanFactory, (attributes) -> bean.setQueryMethodFactory(attributes.get(0)));
          return bean;
        }).customize((bd) -> {
      bd.getConstructorArgumentValues().addIndexedArgumentValue(0, "com.unicen.core.repositories.AuthenticationTokenRepository");
      MutablePropertyValues propertyValues = bd.getPropertyValues();
      propertyValues.addPropertyValue("queryLookupStrategyKey", QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND);
      propertyValues.addPropertyValue("lazyInit", false);
      propertyValues.addPropertyValue("namedQueries", BeanDefinitionRegistrar.inner(PropertiesBasedNamedQueries.class).withConstructor(Properties.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new PropertiesBasedNamedQueries(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, BeanDefinitionRegistrar.inner(PropertiesFactoryBean.class)
          .instanceSupplier(PropertiesFactoryBean::new).customize((bd__) -> {
        MutablePropertyValues propertyValues__ = bd__.getPropertyValues();
        propertyValues__.addPropertyValue("locations", "classpath*:META-INF/jpa-named-queries.properties");
        propertyValues__.addPropertyValue("ignoreResourceNotFound", true);
      }).toBeanDefinition())).toBeanDefinition());
      propertyValues.addPropertyValue("repositoryFragments", BeanDefinitionRegistrar.inner(RepositoryFragmentsFactoryBean.class).withConstructor(List.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new RepositoryFragmentsFactoryBean(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, Collections.emptyList())).toBeanDefinition());
      propertyValues.addPropertyValue("transactionManager", "transactionManager");
      propertyValues.addPropertyValue("entityManager", BeanDefinitionRegistrar.inner(SharedEntityManagerCreator.class).withFactoryMethod(SharedEntityManagerCreator.class, "createSharedEntityManager", EntityManagerFactory.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> SharedEntityManagerCreator.createSharedEntityManager(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("entityManagerFactory"))).toBeanDefinition());
      propertyValues.addPropertyValue("escapeCharacter", '\\');
      propertyValues.addPropertyValue("mappingContext", new RuntimeBeanReference("jpaMappingContext"));
      propertyValues.addPropertyValue("enableDefaultTransactions", true);
    }).register(beanFactory);
    BeanDefinitionRegistrar.of("userRepository", ResolvableType.forClassWithGenerics(JpaRepositoryFactoryBean.class, UserRepository.class, User.class, Long.class)).withConstructor(Class.class)
        .instanceSupplier((instanceContext) -> {
          JpaRepositoryFactoryBean bean = instanceContext.create(beanFactory, (attributes) -> new JpaRepositoryFactoryBean(attributes.get(0)));
          instanceContext.method("setEntityPathResolver", ObjectProvider.class)
              .invoke(beanFactory, (attributes) -> bean.setEntityPathResolver(attributes.get(0)));
          instanceContext.method("setQueryMethodFactory", JpaQueryMethodFactory.class)
              .invoke(beanFactory, (attributes) -> bean.setQueryMethodFactory(attributes.get(0)));
          return bean;
        }).customize((bd) -> {
      bd.getConstructorArgumentValues().addIndexedArgumentValue(0, "com.unicen.core.repositories.UserRepository");
      MutablePropertyValues propertyValues = bd.getPropertyValues();
      propertyValues.addPropertyValue("queryLookupStrategyKey", QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND);
      propertyValues.addPropertyValue("lazyInit", false);
      propertyValues.addPropertyValue("namedQueries", BeanDefinitionRegistrar.inner(PropertiesBasedNamedQueries.class).withConstructor(Properties.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new PropertiesBasedNamedQueries(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, BeanDefinitionRegistrar.inner(PropertiesFactoryBean.class)
          .instanceSupplier(PropertiesFactoryBean::new).customize((bd__) -> {
        MutablePropertyValues propertyValues__ = bd__.getPropertyValues();
        propertyValues__.addPropertyValue("locations", "classpath*:META-INF/jpa-named-queries.properties");
        propertyValues__.addPropertyValue("ignoreResourceNotFound", true);
      }).toBeanDefinition())).toBeanDefinition());
      propertyValues.addPropertyValue("repositoryFragments", BeanDefinitionRegistrar.inner(RepositoryFragmentsFactoryBean.class).withConstructor(List.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new RepositoryFragmentsFactoryBean(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, Collections.emptyList())).toBeanDefinition());
      propertyValues.addPropertyValue("transactionManager", "transactionManager");
      propertyValues.addPropertyValue("entityManager", BeanDefinitionRegistrar.inner(SharedEntityManagerCreator.class).withFactoryMethod(SharedEntityManagerCreator.class, "createSharedEntityManager", EntityManagerFactory.class)
          .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> SharedEntityManagerCreator.createSharedEntityManager(attributes.get(0)))).customize((bd_) -> bd_.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("entityManagerFactory"))).toBeanDefinition());
      propertyValues.addPropertyValue("escapeCharacter", '\\');
      propertyValues.addPropertyValue("mappingContext", new RuntimeBeanReference("jpaMappingContext"));
      propertyValues.addPropertyValue("enableDefaultTransactions", true);
    }).register(beanFactory);
    org.springframework.boot.autoconfigure.http.ContextBootstrapInitializer.registerHttpMessageConvertersAutoConfiguration_StringHttpMessageConverterConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.http.ContextBootstrapInitializer.registerStringHttpMessageConverterConfiguration_stringHttpMessageConverter(beanFactory);
    org.springframework.boot.autoconfigure.http.ContextBootstrapInitializer.registerJacksonHttpMessageConvertersConfiguration_MappingJackson2HttpMessageConverterConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.http.ContextBootstrapInitializer.registerMappingJackson2HttpMessageConverterConfiguration_mappingJackson2HttpMessageConverter(beanFactory);
    org.springframework.boot.autoconfigure.http.ContextBootstrapInitializer.registerJacksonHttpMessageConvertersConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration", HttpMessageConvertersAutoConfiguration.class)
        .instanceSupplier(HttpMessageConvertersAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("messageConverters", HttpMessageConverters.class).withFactoryMethod(HttpMessageConvertersAutoConfiguration.class, "messageConverters", ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(HttpMessageConvertersAutoConfiguration.class).messageConverters(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.data.web.config.ProjectingArgumentResolverRegistrar", ProjectingArgumentResolverRegistrar.class)
        .instanceSupplier(ProjectingArgumentResolverRegistrar::new).register(beanFactory);
    org.springframework.data.web.config.ContextBootstrapInitializer.registerProjectingArgumentResolverRegistrar_projectingArgumentResolverBeanPostProcessor(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.data.web.config.SpringDataWebConfiguration", SpringDataWebConfiguration.class).withConstructor(ApplicationContext.class, ObjectFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SpringDataWebConfiguration(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("pageableResolver", PageableHandlerMethodArgumentResolver.class).withFactoryMethod(SpringDataWebConfiguration.class, "pageableResolver")
        .instanceSupplier(() -> beanFactory.getBean(SpringDataWebConfiguration.class).pageableResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("sortResolver", SortHandlerMethodArgumentResolver.class).withFactoryMethod(SpringDataWebConfiguration.class, "sortResolver")
        .instanceSupplier(() -> beanFactory.getBean(SpringDataWebConfiguration.class).sortResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.data.web.config.SpringDataJacksonConfiguration", SpringDataJacksonConfiguration.class)
        .instanceSupplier(SpringDataJacksonConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("jacksonGeoModule", GeoModule.class).withFactoryMethod(SpringDataJacksonConfiguration.class, "jacksonGeoModule")
        .instanceSupplier(() -> beanFactory.getBean(SpringDataJacksonConfiguration.class).jacksonGeoModule()).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration", SpringDataWebAutoConfiguration.class).withConstructor(SpringDataWebProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new SpringDataWebAutoConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("pageableCustomizer", PageableHandlerMethodArgumentResolverCustomizer.class).withFactoryMethod(SpringDataWebAutoConfiguration.class, "pageableCustomizer")
        .instanceSupplier(() -> beanFactory.getBean(SpringDataWebAutoConfiguration.class).pageableCustomizer()).register(beanFactory);
    BeanDefinitionRegistrar.of("sortCustomizer", SortHandlerMethodArgumentResolverCustomizer.class).withFactoryMethod(SpringDataWebAutoConfiguration.class, "sortCustomizer")
        .instanceSupplier(() -> beanFactory.getBean(SpringDataWebAutoConfiguration.class).sortCustomizer()).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.data.web-org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties", SpringDataWebProperties.class)
        .instanceSupplier(SpringDataWebProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration", ProjectInfoAutoConfiguration.class).withConstructor(ProjectInfoProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new ProjectInfoAutoConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.info-org.springframework.boot.autoconfigure.info.ProjectInfoProperties", ProjectInfoProperties.class)
        .instanceSupplier(ProjectInfoProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerJdbcTemplateConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerJdbcTemplateConfiguration_jdbcTemplate(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerNamedParameterJdbcTemplateConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerNamedParameterJdbcTemplateConfiguration_namedParameterJdbcTemplate(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration", JdbcTemplateAutoConfiguration.class)
        .instanceSupplier(JdbcTemplateAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.jdbc-org.springframework.boot.autoconfigure.jdbc.JdbcProperties", JdbcProperties.class)
        .instanceSupplier(JdbcProperties::new).register(beanFactory);
    org.springframework.boot.sql.init.dependency.ContextBootstrapInitializer.registerDatabaseInitializationDependencyConfigurer_DependsOnDatabaseInitializationPostProcessor(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration$LiquibaseConfiguration", LiquibaseAutoConfiguration.LiquibaseConfiguration.class).withConstructor(LiquibaseProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new LiquibaseAutoConfiguration.LiquibaseConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("liquibase", SpringLiquibase.class).withFactoryMethod(LiquibaseAutoConfiguration.LiquibaseConfiguration.class, "liquibase", ObjectProvider.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(LiquibaseAutoConfiguration.LiquibaseConfiguration.class).liquibase(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.liquibase-org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties", LiquibaseProperties.class)
        .instanceSupplier(LiquibaseProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration", LiquibaseAutoConfiguration.class)
        .instanceSupplier(LiquibaseAutoConfiguration::new).register(beanFactory);
    org.springframework.boot.autoconfigure.liquibase.ContextBootstrapInitializer.registerLiquibaseAutoConfiguration_liquibaseDefaultDdlModeProvider(beanFactory);
    org.springframework.boot.autoconfigure.mail.ContextBootstrapInitializer.registerMailSenderPropertiesConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.mail.ContextBootstrapInitializer.registerMailSenderPropertiesConfiguration_mailSender(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration", MailSenderAutoConfiguration.class)
        .instanceSupplier(MailSenderAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.mail-org.springframework.boot.autoconfigure.mail.MailProperties", MailProperties.class)
        .instanceSupplier(MailProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration", NettyAutoConfiguration.class).withConstructor(NettyProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new NettyAutoConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.netty-org.springframework.boot.autoconfigure.netty.NettyProperties", NettyProperties.class)
        .instanceSupplier(NettyProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.security.servlet.ContextBootstrapInitializer.registerErrorPageSecurityFilterConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.security.servlet.ContextBootstrapInitializer.registerErrorPageSecurityFilterConfiguration_errorPageSecurityInterceptor(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration", SecurityAutoConfiguration.class)
        .instanceSupplier(SecurityAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("authenticationEventPublisher", DefaultAuthenticationEventPublisher.class).withFactoryMethod(SecurityAutoConfiguration.class, "authenticationEventPublisher", ApplicationEventPublisher.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(SecurityAutoConfiguration.class).authenticationEventPublisher(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.security-org.springframework.boot.autoconfigure.security.SecurityProperties", SecurityProperties.class)
        .instanceSupplier(SecurityProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration", SecurityFilterAutoConfiguration.class)
        .instanceSupplier(SecurityFilterAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("securityFilterChainRegistration", DelegatingFilterProxyRegistrationBean.class).withFactoryMethod(SecurityFilterAutoConfiguration.class, "securityFilterChainRegistration", SecurityProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(SecurityFilterAutoConfiguration.class).securityFilterChainRegistration(attributes.get(0)))).register(beanFactory);
    org.springframework.boot.autoconfigure.sql.init.ContextBootstrapInitializer.registerDataSourceInitializationConfiguration(beanFactory);
    org.springframework.boot.autoconfigure.sql.init.ContextBootstrapInitializer.registerDataSourceInitializationConfiguration_dataSourceScriptDatabaseInitializer(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration", SqlInitializationAutoConfiguration.class)
        .instanceSupplier(SqlInitializationAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.sql.init-org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties", SqlInitializationProperties.class)
        .instanceSupplier(SqlInitializationProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration", TaskSchedulingAutoConfiguration.class)
        .instanceSupplier(TaskSchedulingAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("scheduledBeanLazyInitializationExcludeFilter", LazyInitializationExcludeFilter.class).withFactoryMethod(TaskSchedulingAutoConfiguration.class, "scheduledBeanLazyInitializationExcludeFilter")
        .instanceSupplier(() -> TaskSchedulingAutoConfiguration.scheduledBeanLazyInitializationExcludeFilter()).register(beanFactory);
    BeanDefinitionRegistrar.of("taskSchedulerBuilder", TaskSchedulerBuilder.class).withFactoryMethod(TaskSchedulingAutoConfiguration.class, "taskSchedulerBuilder", TaskSchedulingProperties.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(TaskSchedulingAutoConfiguration.class).taskSchedulerBuilder(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.task.scheduling-org.springframework.boot.autoconfigure.task.TaskSchedulingProperties", TaskSchedulingProperties.class)
        .instanceSupplier(TaskSchedulingProperties::new).register(beanFactory);
    org.springframework.boot.autoconfigure.jdbc.ContextBootstrapInitializer.registerDataSourceTransactionManagerAutoConfiguration_JdbcTransactionManagerConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration", DataSourceTransactionManagerAutoConfiguration.class)
        .instanceSupplier(DataSourceTransactionManagerAutoConfiguration::new).register(beanFactory);
    org.springframework.transaction.annotation.ContextBootstrapInitializer.registerProxyTransactionManagementConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.transaction.config.internalTransactionAdvisor", BeanFactoryTransactionAttributeSourceAdvisor.class).withFactoryMethod(ProxyTransactionManagementConfiguration.class, "transactionAdvisor", TransactionAttributeSource.class, TransactionInterceptor.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(ProxyTransactionManagementConfiguration.class).transactionAdvisor(attributes.get(0), attributes.get(1)))).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("transactionAttributeSource", TransactionAttributeSource.class).withFactoryMethod(ProxyTransactionManagementConfiguration.class, "transactionAttributeSource")
        .instanceSupplier(() -> beanFactory.getBean(ProxyTransactionManagementConfiguration.class).transactionAttributeSource()).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("transactionInterceptor", TransactionInterceptor.class).withFactoryMethod(ProxyTransactionManagementConfiguration.class, "transactionInterceptor", TransactionAttributeSource.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(ProxyTransactionManagementConfiguration.class).transactionInterceptor(attributes.get(0)))).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.transaction.config.internalTransactionalEventListenerFactory", TransactionalEventListenerFactory.class).withFactoryMethod(AbstractTransactionManagementConfiguration.class, "transactionalEventListenerFactory")
        .instanceSupplier(() -> AbstractTransactionManagementConfiguration.transactionalEventListenerFactory()).customize((bd) -> bd.setRole(2)).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration$JdkDynamicAutoProxyConfiguration", TransactionAutoConfiguration.EnableTransactionManagementConfiguration.JdkDynamicAutoProxyConfiguration.class)
        .instanceSupplier(TransactionAutoConfiguration.EnableTransactionManagementConfiguration.JdkDynamicAutoProxyConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration", TransactionAutoConfiguration.EnableTransactionManagementConfiguration.class)
        .instanceSupplier(TransactionAutoConfiguration.EnableTransactionManagementConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$TransactionTemplateConfiguration", TransactionAutoConfiguration.TransactionTemplateConfiguration.class)
        .instanceSupplier(TransactionAutoConfiguration.TransactionTemplateConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("transactionTemplate", TransactionTemplate.class).withFactoryMethod(TransactionAutoConfiguration.TransactionTemplateConfiguration.class, "transactionTemplate", PlatformTransactionManager.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(TransactionAutoConfiguration.TransactionTemplateConfiguration.class).transactionTemplate(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration", TransactionAutoConfiguration.class)
        .instanceSupplier(TransactionAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("platformTransactionManagerCustomizers", TransactionManagerCustomizers.class).withFactoryMethod(TransactionAutoConfiguration.class, "platformTransactionManagerCustomizers", ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(TransactionAutoConfiguration.class).platformTransactionManagerCustomizers(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.transaction-org.springframework.boot.autoconfigure.transaction.TransactionProperties", TransactionProperties.class)
        .instanceSupplier(TransactionProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration", RestTemplateAutoConfiguration.class)
        .instanceSupplier(RestTemplateAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("restTemplateBuilderConfigurer", RestTemplateBuilderConfigurer.class).withFactoryMethod(RestTemplateAutoConfiguration.class, "restTemplateBuilderConfigurer", ObjectProvider.class, ObjectProvider.class, ObjectProvider.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(RestTemplateAutoConfiguration.class).restTemplateBuilderConfigurer(attributes.get(0), attributes.get(1), attributes.get(2)))).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("restTemplateBuilder", RestTemplateBuilder.class).withFactoryMethod(RestTemplateAutoConfiguration.class, "restTemplateBuilder", RestTemplateBuilderConfigurer.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(RestTemplateAutoConfiguration.class).restTemplateBuilder(attributes.get(0)))).customize((bd) -> bd.setLazyInit(true)).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration$TomcatWebServerFactoryCustomizerConfiguration", EmbeddedWebServerFactoryCustomizerAutoConfiguration.TomcatWebServerFactoryCustomizerConfiguration.class)
        .instanceSupplier(EmbeddedWebServerFactoryCustomizerAutoConfiguration.TomcatWebServerFactoryCustomizerConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("tomcatWebServerFactoryCustomizer", TomcatWebServerFactoryCustomizer.class).withFactoryMethod(EmbeddedWebServerFactoryCustomizerAutoConfiguration.TomcatWebServerFactoryCustomizerConfiguration.class, "tomcatWebServerFactoryCustomizer", Environment.class, ServerProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(EmbeddedWebServerFactoryCustomizerAutoConfiguration.TomcatWebServerFactoryCustomizerConfiguration.class).tomcatWebServerFactoryCustomizer(attributes.get(0), attributes.get(1)))).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration", EmbeddedWebServerFactoryCustomizerAutoConfiguration.class)
        .instanceSupplier(EmbeddedWebServerFactoryCustomizerAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration", HttpEncodingAutoConfiguration.class).withConstructor(ServerProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new HttpEncodingAutoConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("characterEncodingFilter", CharacterEncodingFilter.class).withFactoryMethod(HttpEncodingAutoConfiguration.class, "characterEncodingFilter")
        .instanceSupplier(() -> beanFactory.getBean(HttpEncodingAutoConfiguration.class).characterEncodingFilter()).register(beanFactory);
    org.springframework.boot.autoconfigure.web.servlet.ContextBootstrapInitializer.registerHttpEncodingAutoConfiguration_localeCharsetMappingsCustomizer(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration", MultipartAutoConfiguration.class).withConstructor(MultipartProperties.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> new MultipartAutoConfiguration(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("multipartConfigElement", MultipartConfigElement.class).withFactoryMethod(MultipartAutoConfiguration.class, "multipartConfigElement")
        .instanceSupplier(() -> beanFactory.getBean(MultipartAutoConfiguration.class).multipartConfigElement()).register(beanFactory);
    BeanDefinitionRegistrar.of("multipartResolver", StandardServletMultipartResolver.class).withFactoryMethod(MultipartAutoConfiguration.class, "multipartResolver")
        .instanceSupplier(() -> beanFactory.getBean(MultipartAutoConfiguration.class).multipartResolver()).register(beanFactory);
    BeanDefinitionRegistrar.of("spring.servlet.multipart-org.springframework.boot.autoconfigure.web.servlet.MultipartProperties", MultipartProperties.class)
        .instanceSupplier(MultipartProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.oas.configuration.OpenApiMappingConfiguration", OpenApiMappingConfiguration.class)
        .instanceSupplier(OpenApiMappingConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("openApiModule", JacksonModuleRegistrar.class).withFactoryMethod(OpenApiMappingConfiguration.class, "openApiModule")
        .instanceSupplier(() -> beanFactory.getBean(OpenApiMappingConfiguration.class).openApiModule()).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.oas.configuration.OpenApiWebMvcConfiguration", OpenApiWebMvcConfiguration.class)
        .instanceSupplier(OpenApiWebMvcConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("webMvcOpenApiTransformer", WebMvcOpenApiTransformationFilter.class).withFactoryMethod(OpenApiWebMvcConfiguration.class, "webMvcOpenApiTransformer", String.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> beanFactory.getBean(OpenApiWebMvcConfiguration.class).webMvcOpenApiTransformer(attributes.get(0)))).register(beanFactory);
    BeanDefinitionRegistrar.of("webMvcOpenApiTransformationFilterRegistry", ResolvableType.forClassWithGenerics(OrderAwarePluginRegistry.class, WebMvcOpenApiTransformationFilter.class, DocumentationType.class))
        .instanceSupplier(PluginRegistryFactoryBean::new).customize((bd) -> bd.getPropertyValues().addPropertyValue("type", WebMvcOpenApiTransformationFilter.class)).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration", OpenApiDocumentationConfiguration.class)
        .instanceSupplier(OpenApiDocumentationConfiguration::new).register(beanFactory);
    springfox.boot.starter.autoconfigure.ContextBootstrapInitializer.registerSwaggerUiWebMvcConfiguration(beanFactory);
    BeanDefinitionRegistrar.of("swaggerUiConfigurer", SwaggerUiWebMvcConfigurer.class).withFactoryMethod(SwaggerUiWebMvcConfiguration.class, "swaggerUiConfigurer")
        .instanceSupplier(() -> beanFactory.getBean(SwaggerUiWebMvcConfiguration.class).swaggerUiConfigurer()).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration", OpenApiAutoConfiguration.class)
        .instanceSupplier(OpenApiAutoConfiguration::new).register(beanFactory);
    BeanDefinitionRegistrar.of("springfox.documentation-springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties", SpringfoxConfigurationProperties.class)
        .instanceSupplier(SpringfoxConfigurationProperties::new).register(beanFactory);
    BeanDefinitionRegistrar.of("org.springframework.orm.jpa.SharedEntityManagerCreator#0", EntityManager.class).withFactoryMethod(SharedEntityManagerCreator.class, "createSharedEntityManager", EntityManagerFactory.class)
        .instanceSupplier((instanceContext) -> instanceContext.create(beanFactory, (attributes) -> SharedEntityManagerCreator.createSharedEntityManager(attributes.get(0)))).customize((bd) -> {
      bd.setPrimary(true);
      bd.setLazyInit(true);
      bd.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("entityManagerFactory"));
    }).register(beanFactory);
  }
}
