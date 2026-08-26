# RFC: Estratégia de Autenticação - Tech Challenge Mecânica

## Status
**Proposta** → **Em Revisão** → **Aprovada** → **Implementada**

## Contexto e Motivação

Este documento define a estratégia de autenticação para o Tech Challenge Mecânica, especificamente para a Parte do Aluno 1, que é responsável pela camada de segurança e autenticação da solução.

### Problemas a Resolver
1. Necessidade de autenticar clientes de forma segura via CPF
2. Proteção de rotas sensíveis da aplicação
3. Integração entre diferentes componentes (Lambda, API Gateway, aplicação principal)
4. Garantir conformidade com boas práticas de segurança

## Proposta

### Arquitetura de Autenticação

A autenticação seguirá o fluxo **CPF → Lambda → JWT → APIs Protegidas**:

1. **Autenticação Inicial**: Cliente envia CPF para endpoint público
2. **Validação**: Lambda valida CPF, consulta cliente e status no banco
3. **Token Generation**: Lambda gera JWT token para clientes autorizados
4. **Acesso Protegido**: Cliente usa JWT para acessar rotas protegidas

### Tecnologias Escolhidas

| Componente | Tecnologia | Justificativa |
|-------------|------------|---------------|
| Validação CPF | Java (algoritmo oficial) | Precisão e controle total |
| Banco de Dados | PostgreSQL RDS | Conformidade com arquitetura existente |
| Token JWT | JJWT (Java JWT) | Biblioteca madura e bem mantida |
| API Gateway | AWS API Gateway HTTP | Integração nativa com Lambda |
| Authorizer | JWT Authorizer | Validação automática de tokens |
| Serverless | AWS Lambda | Escalabilidade e custo otimizado |

### Estrutura do Token JWT

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "clientId": 123,
    "cpf": "52998224725",
    "nome": "Cliente Exemplo",
    "status": "ATIVO",
    "sub": "52998224725",
    "aud": "tech-challenge-api",
    "iss": "tech-challenge-auth-lambda",
    "iat": 1693000000,
    "exp": 1693000900
  }
}
```

**Configurações do Token:**
- **Algoritmo**: HS256 (HMAC-SHA256)
- **Expiração**: 15 minutos
- **Issuer**: tech-challenge-auth-lambda
- **Audience**: tech-challenge-api
- **Claims**: clientId, cpf, nome, status

### Status de Cliente Permitidos

Os seguintes status permitem autenticação:
- **ATIVO**: Cliente regular e ativo
- **APROVADO**: Cliente aprovado mas ainda não ativo
- **VERIFICADO**: Cliente verificado e pronto para uso

Status que **bloqueiam** autenticação:
- **INATIVO**: Cliente inativo
- **BLOQUEADO**: Cliente bloqueado por violação
- **PENDENTE**: Cliente em processo de cadastro
- **CANCELADO**: Cliente cancelado

### Endpoints da API

#### Endpoint Público (Autenticação)
```
POST /auth/login
Content-Type: application/json

Request:
{
  "cpf": "529.982.247-25"
}

Response (200 OK):
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "client": {
    "id": 1,
    "nome": "Cliente Exemplo",
    "cpfMascarado": "***.982.***-25"
  }
}

Response (400 Bad Request):
{
  "error": "CPF inválido",
  "statusCode": 400
}

Response (404 Not Found):
{
  "error": "Cliente não encontrado",
  "statusCode": 404
}

Response (403 Forbidden):
{
  "error": "Cliente com status não permitido",
  "statusCode": 403
}
```

#### Endpoint Protegido (Exemplo)
```
GET /clientes/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Response (200 OK):
{
  "id": 1,
  "nome": "Cliente Exemplo",
  "cpfMascarado": "***.982.***-25",
  "status": "ATIVO"
}

Response (401 Unauthorized):
{
  "error": "Token inválido ou expirado",
  "statusCode": 401
}
```

## Segurança

### Proteção de Dados Sensíveis
- **CPF**: Sempre mascarado em logs e respostas (***.982.***-25)
- **JWT Secret**: Armazenado em variável de ambiente (Base64 encoded)
- **Credenciais DB**: Variáveis de ambiente da Lambda
- **Conexão RDS**: Via VPC privada (sem acesso público)

### Validações de Segurança
1. **CPF**: Validação completa com dígitos verificadores
2. **JWT**: Assinatura HMAC-SHA256, expiração, audience, issuer
3. **Status**: Verificação de status antes de gerar token
4. **Rate Limiting**: Configurado no API Gateway (recomendado)
5. **HTTPS**: Obrigatório para todas as requisições

### IAM Roles
Permissões mínimas necessárias:
- Lambda: Acesso RDS (rds-db:connect)
- API Gateway: Invoke Lambda
- S3: Acesso ao bucket de artifacts

## Integração com Outros Alunos

### Aluno 2 (Kubernetes)
- **URLs**: API Gateway endpoint será exposto via Ingress
- **Healthchecks**: Lambda health check endpoint
- **Métricas**: CloudWatch metrics para Lambda e API Gateway
- **Logs**: CloudWatch Logs integrados com sistema de logs centralizado

### Aluno 3 (Aplicação Principal)
- **Estrutura DB**: Tabela `cliente` com campos `id`, `nome`, `cpf_cnpj`, `status`
- **Status**: Valores padronizados para autenticação
- **Integração**: Aplicação consome APIs protegidas com JWT

## Performance e Escalabilidade

### Lambda Configuration
- **Runtime**: Java 17
- **Memory**: 256 MB
- **Timeout**: 15 segundos
- **Concurrent Executions**: Auto-scaling

### API Gateway Configuration
- **Type**: HTTP API (mais performático que REST API)
- **Throttling**: Configurado para evitar abuse
- **Caching**: Desabilitado para endpoints de autenticação

### RDS Configuration
- **Instance**: db.t3.micro (Free Tier)
- **Storage**: 10 GB gp3
- **Backup**: 1 dia (Free Tier limit)

## Monitoramento e Observabilidade

### Logs
- **Lambda**: CloudWatch Logs com máscara de CPF
- **API Gateway**: Access logs habilitados
- **RDS**: Slow query log habilitado

### Métricas
- **Lambda**: Invocations, Errors, Duration, Throttles
- **API Gateway**: Count, Latency, 4XX, 5XX
- **RDS**: Connections, CPU, Storage

### Alerts
- **Lambda Error Rate**: > 1% por 5 minutos
- **API Gateway 5XX**: > 0.5% por 5 minutos
- **RDS CPU**: > 80% por 5 minutos

## Plano de Implementação

### Fase 1: Core Authentication (✅ Concluído)
- [x] Criar Lambda Function
- [x] Implementar validação de CPF
- [x] Implementar consulta ao banco
- [x] Implementar geração de JWT
- [x] Configurar API Gateway básico

### Fase 2: Security & Protection (✅ Concluído)
- [x] Configurar JWT Authorizer
- [x] Proteger rotas sensíveis
- [x] Implementar mascaramento de dados
- [x] Configurar variáveis de ambiente

### Fase 3: CI/CD & Quality (✅ Concluído)
- [x] Criar pipeline de CI/CD
- [x] Adicionar validações de qualidade
- [x] Configurar branch protection
- [x] Automatizar deploy

### Fase 4: Documentation & Testing (Em Progresso)
- [x] Criar diagrama de sequência
- [x] Criar RFC de estratégia
- [ ] Documentar testes com Postman
- [ ] Criar testes de integração

### Fase 5: Integration (Pendente)
- [ ] Integrar com aplicação principal
- [ ] Configurar healthchecks
- [ ] Implementar rate limiting
- [ ] Configurar alerts

## Alternativas Consideradas

### Alternativa 1: Cognito User Pools
**Vantagens**: Gerenciamento completo de usuários, MMS nativo
**Desvantagens**: Custo adicional, complexidade desnecessária para CPF
**Decisão**: Não utilizado - CPF validation é específica do domínio

### Alternativa 2: API Gateway REST API
**Vantagens**: Mais recursos, authorizers customizados
**Desvantagens**: Custo mais alto, latência maior
**Decisão**: HTTP API escolhido - melhor custo/benefício

### Alternativa 3: Session-based Authentication
**Vantagens**: Simples, sem JWT
**Desvantagens**: Escalabilidade ruim, stateful
**Decisão**: JWT escolhido - stateless, melhor escalabilidade

## Riscos e Mitigações

| Risco | Impacto | Probabilidade | Mitigação |
|-------|---------|--------------|------------|
| JWT Secret comprometido | Alto | Baixo | Rotation regular, monitoramento |
| CPF brute force | Médio | Médio | Rate limiting, CAPTCHA |
| RDS connection leak | Médio | Baixo | Connection pooling, monitoring |
| Lambda cold start | Baixo | Alto | Provisioned concurrency (se necessário) |
| Token expiration issues | Baixo | Médio | Refresh token (futuro) |

## Decisões Arquiteturais

### DA-001: Usar CPF como identificador principal
**Razão**: CPF é o identificador natural no domínio de oficina mecânica
**Trade-off**: Menos flexível que email, mas mais específico

### DA-002: Token expiration de 15 minutos
**Razão**: Balance entre segurança e UX
**Trade-off**: Requer renovação frequente, mas reduz janela de ataque

### DA-003: Status-based authorization
**Razão**: Flexibilidade para diferentes estados do cliente
**Trade-off**: Complexidade adicional, mas necessário para o domínio

### DA-004: HTTP API vs REST API
**Razão**: Custo menor e performance melhor
**Trade-off**: Menos recursos, mas suficientes para o caso de uso

## Referências

- [RFC 7519 - JSON Web Token (JWT)](https://tools.ietf.org/html/rfc7519)
- [AWS Lambda Best Practices](https://docs.aws.amazon.com/lambda/latest/dg/best-practices.html)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [Receita Federal - Algoritmo CPF](http://www.receita.fazenda.gov.br/aplicacoes/atcta/cpf/funcoes.js)

## Aprovação

- [ ] Aluno 1 (Autenticação)
- [ ] Aluno 2 (Kubernetes)
- [ ] Aluno 3 (Aplicação)
- [ ] Professor/Reviewer

## Histórico de Mudanças

| Data | Versão | Autor | Mudança |
|------|--------|-------|---------|
| 26/08/2026 | 1.0 | Aluno 1 | Versão inicial |
