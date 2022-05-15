package com.unicen.core.configuration;

import springfox.documentation.service.*;
import springfox.documentation.swagger.web.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface EnsolversCoreApiDocumentationConfiguration {
    String swaggerUsername();

    String swaggerPassword();

    default String basePackageName() {
        return "com.ensolvers.core";
    }

    default ApiInfo apiInfo() {
        return new ApiInfo("Ensolvers Core Generic API", "", "1.0", "", new Contact("Ensolvers", "", ""), "", "", Collections.emptyList());
    }

    default ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    default List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Bearer", authorizationScopes));
    }

    default UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false).defaultModelsExpandDepth(1).defaultModelExpandDepth(1)
                .displayRequestDuration(false).docExpansion(DocExpansion.NONE).filter(false).maxDisplayedTags(null).operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false).showCommonExtensions(false).tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS).validatorUrl(null).build();
    }
}
