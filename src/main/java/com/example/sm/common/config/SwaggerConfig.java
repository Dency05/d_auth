package com.example.sm.common.config;
import com.example.sm.common.decorator.ExtendRelativePathProvider;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.ArrayList;
import java.util.List;
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket eventApi(){
        return new Docket(
                DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.sm.auth.controller"))
                .paths(PathSelectors.regex("/*.*"))
                .build()
                .globalOperationParameters(getParameters())
                .pathProvider(new ExtendRelativePathProvider())
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .apiInfo(apiEndPointsInfo());
    }
    private List<Parameter> getParameters(){
        List<Parameter> parameters =  new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("timezone")
                .description("Current Timezone")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .defaultValue("CST")
                .required(true)
                .build());
        parameters.add(new ParameterBuilder()
                .name("Api-Version")
                .description("API Version")
                .modelRef(new ModelRef("string",new AllowableListValues(Lists.newArrayList("V1"),"ApiVersion")))
                .parameterType("header")
                .defaultValue("V1")
                .required(true)
                .build());
        return parameters;
    }
    private ApiKey apiKey() {
        return new ApiKey("TOKEN", "TOKEN", "header");
    }
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/*.*"))
                .build();
    }
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("TOKEN", authorizationScopes));
    }
    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder()
                .version("1.0.0")
                .build();
    }
}
