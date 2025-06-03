package ir.snp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resource-server.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "keycloak_oauth";
        
        // Extract realm from jwkSetUri
        String realm = "master"; // Default realm
        if (jwkSetUri != null && jwkSetUri.contains("/realms/")) {
            String[] parts = jwkSetUri.split("/realms/");
            if (parts.length > 1) {
                String[] realmParts = parts[1].split("/");
                if (realmParts.length > 0) {
                    realm = realmParts[0];
                }
            }
        }
        
        // Build token URL based on realm
        String authServerUrl = jwkSetUri.substring(0, jwkSetUri.indexOf("/realms/"));
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl(tokenUrl)
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "User profile")
                                                        .addString("email", "Email address")
                                                )
                                        )
                                )
                        )
                )
                .info(new Info()
                        .title("Expense Service API")
                        .description("API for managing expenses through the gateway")
                        .version("1.0"));
    }
}
