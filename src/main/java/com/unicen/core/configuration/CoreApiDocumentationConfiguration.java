package com.unicen.core.configuration;

import springfox.documentation.service.*;
import springfox.documentation.swagger.web.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CoreApiDocumentationConfiguration {

    @Value("${swaggerUsername}")
    private String swaggerUsername;

    @Value("${swaggerPassword}")
    private String swaggerPassword;

    public String swaggerUsername() {
        return swaggerUsername;
    }

    public String swaggerPassword() {
        return swaggerPassword;
    }


    String basePackageName() {
        return "com.unicen";
    }

    ApiInfo apiInfo() {
        return new ApiInfo("Generic API", "", "1.0", "", new Contact("Unicen", "", ""), "", "", Collections.emptyList());
    }

    ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Bearer", authorizationScopes));
    }

    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false).defaultModelsExpandDepth(1).defaultModelExpandDepth(1)
                .displayRequestDuration(false).docExpansion(DocExpansion.NONE).filter(false).maxDisplayedTags(null).operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false).showCommonExtensions(false).tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS).validatorUrl(null).build();
    }
}
