package com.f1rsters.tech_challenge_mecanica.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tech Challenge Mecanica API")
                        .version("v1.0.0")
                        .description("""
                                API REST para gerenciamento de oficina mecânica.
                                
                                **Funcionalidades principais:**
                                - Gerenciamento de clientes e veículos
                                - Controle de serviços e peças com estoque
                                - Criação e acompanhamento de ordens de serviço
                                - Autenticação JWT com controle de acesso por roles
                                - Endpoint público para acompanhamento de status de OS
                                
                                **Autenticação:**
                                A API utiliza JWT Bearer tokens para autenticação. Faça login em `/api/auth/login` para obter o token.
                                
                                **Roles disponíveis:**
                                - ADMIN: Acesso completo a todos os recursos
                                - ATENDENTE: Gestão de clientes e ordens de serviço
                                - MECANICO: Atualização de status de ordens de serviço
                                - ESTOQUISTA: Gestão de peças e estoque
                                """)
                        .contact(new Contact()
                                .name("F1rsters Team")
                                .email("contact@f1rsters.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desenvolvimento"),
                        new Server()
                                .url("https://api.f1rsters-mecanica.com")
                                .description("Servidor de produção")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("""
                                                Autenticação JWT Bearer.
                                                
                                                Para autenticar, faça login em `/api/auth/login` e use o token retornado no header Authorization:
                                                `Authorization: Bearer <seu_token_jwt>`
                                                """)));
    }
}

