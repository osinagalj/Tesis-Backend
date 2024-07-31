package com.unicen.core.configuration;


import com.unicen.core.dto.PublicAuthenticationToken;
import com.unicen.core.security.GenericAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Import(BeanValidatorPluginsConfiguration.class)
@EnableSwagger2
@Configuration
public class ApiDocumentationConfiguration {

    @Autowired(required = false)
    CoreApiDocumentationConfiguration apiDocumentationConfiguration;

    @Bean
    public Docket api() {
        Class<?>[] ignoredClasses = { GenericAuthenticationToken.class, PublicAuthenticationToken.class };
        if (apiDocumentationConfiguration != null) {
            return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiDocumentationConfiguration.apiInfo()).securityContexts(List.of(securityContext()))
                    .securitySchemes(List.of(apiDocumentationConfiguration.apiKey())).select()
                    .apis(RequestHandlerSelectors.basePackage(apiDocumentationConfiguration.basePackageName())).paths(PathSelectors.any()).build()
                    .ignoredParameterTypes(ignoredClasses);
        }
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.none()).paths(PathSelectors.none()).build();
    }

    private SecurityContext securityContext() {
        List<SecurityReference> auth = defaultAuth();
        if (apiDocumentationConfiguration != null) {
            auth = apiDocumentationConfiguration.defaultAuth();
        }
        return SecurityContext.builder().securityReferences(auth).build();
    }

    @Bean
    UiConfiguration uiConfig() {
        if (apiDocumentationConfiguration != null)
            return apiDocumentationConfiguration.uiConfig();
        return UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false).defaultModelsExpandDepth(1).defaultModelExpandDepth(1)
                .displayRequestDuration(false).docExpansion(DocExpansion.NONE).filter(false).maxDisplayedTags(null).operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false).showCommonExtensions(false).tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS).validatorUrl(null).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Bearer", authorizationScopes));
    }

}