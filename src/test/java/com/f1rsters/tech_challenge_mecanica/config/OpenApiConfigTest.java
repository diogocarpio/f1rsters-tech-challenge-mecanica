package com.f1rsters.tech_challenge_mecanica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void shouldConfigureOpenApiInfoAndJwtSecurityScheme() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.openApi();

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Tech Challenge Mecanica API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("v1");
        assertThat(openAPI.getInfo().getDescription()).isEqualTo("API de gerenciamento da oficina mecanica");

        assertThat(openAPI.getSecurity()).isNotNull();
        assertThat(openAPI.getSecurity()).isNotEmpty();

        SecurityRequirement requirement = openAPI.getSecurity().get(0);
        assertThat(requirement).isNotNull();
        assertThat(requirement).containsKey("bearerAuth");

        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
        assertThat(scheme.getName()).isEqualTo("bearerAuth");
    }
}
