package ir.snp.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "keycloak_oauth",
        type = SecuritySchemeType.OAUTH2,
        flows = @io.swagger.v3.oas.annotations.security.OAuthFlows(
                password = @io.swagger.v3.oas.annotations.security.OAuthFlow(
                        tokenUrl = "${springdoc.swagger-ui.oauth.token-url}"
                )
        )
)
public class SwaggerSecurityConfig {

    @Bean
    public GroupedOpenApi expensesApi() {
        return GroupedOpenApi.builder()
                .group("expenses-api")
                .pathsToMatch("/api/expenses/**", "/api/thresholds/**")
                .build();
    }
}
