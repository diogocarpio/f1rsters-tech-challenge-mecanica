# Lambda Function - Autenticação (Tech Challenge Parte 1)

## Descrição

Função AWS Lambda responsável pela autenticação de clientes via CPF e geração de tokens JWT. Esta função é parte integrante do Tech Challenge - Parte do Aluno 1.

## Funcionalidades

- ✅ Validação de CPF (algoritmo oficial com dígitos verificadores)
- ✅ Consulta de cliente no banco de dados PostgreSQL
- ✅ Verificação de status do cliente
- ✅ Geração de token JWT para clientes autorizados
- ✅ Tratamento de erros adequado (CPF inválido, cliente inexistente, status não permitido)
- ✅ Mascaramento de dados sensíveis em logs e respostas

## Estrutura do Projeto

```
auth-function/
├── src/
│   ├── main/java/com/f1rsters/tech_challenge_mecanica/lambda/
│   │   ├── AuthHandler.java          # Handler principal da Lambda
│   │   ├── CpfValidator.java         # Validador de CPF
│   │   ├── JwtService.java           # Serviço de geração/validação JWT
│   │   ├── DatabaseService.java      # Serviço de acesso ao banco de dados
│   │   └── ClientInfo.java           # DTO de informações do cliente
│   └── test/java/com/f1rsters/tech_challenge_mecanica/lambda/
│       └── CpfValidatorTest.java     # Testes de validação de CPF
├── pom.xml                           # Dependências Maven
└── README.md                         # Este arquivo
```

## Tecnologias

- **Java 17** - Linguagem de desenvolvimento
- **AWS Lambda** - Plataforma serverless
- **AWS SDK for Java 2.x** - Integração com serviços AWS
- **PostgreSQL JDBC** - Conexão com banco de dados
- **JWT (jjwt)** - Geração e validação de tokens
- **JUnit 5** - Framework de testes
- **Mockito** - Framework de mocking para testes

## Variáveis de Ambiente

A função Lambda requer as seguintes variáveis de ambiente configuradas:

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `DB_HOST` | Endpoint do RDS PostgreSQL | `tech-challenge-postgres.xxxx.sa-east-1.rds.amazonaws.com:5432` |
| `DB_NAME` | Nome do banco de dados | `oficina` |
| `DB_USERNAME` | Usuário do banco de dados | `oficinauser` |
| `DB_PASSWORD` | Senha do banco de dados | `senha_segura` |
| `JWT_SECRET` | Segredo JWT codificado em Base64 | `QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo=` |

## Build e Deploy Local

### Pré-requisitos

- Java JDK 17
- Maven 3.8+
- AWS CLI configurado

### Build do Projeto

```bash
cd aws/lambda/auth-function
mvn clean package
```

O arquivo JAR será gerado em `target/auth-function.jar`

### Upload para S3

```bash
aws s3 cp target/auth-function.jar s3://f1rsters-tech-challenge-lambda-artifacts/auth-function.jar
```

### Deploy da Lambda

```bash
aws lambda update-function-code \
  --function-name f1rsters-tech-challenge-mecanica-auth-function \
  --s3-bucket f1rsters-tech-challenge-lambda-artifacts \
  --s3-key auth-function.jar
```

## Testes

### Executar Testes Unitários

```bash
cd aws/lambda/auth-function
mvn test
```

### Testar Localmente (SAM CLI)

Para testar a função localmente, você pode usar o AWS SAM CLI:

```bash
sam local invoke AuthFunction --event events/test-event.json
```

## Fluxo de Autenticação

```
1. Cliente envia CPF via API Gateway
   ↓
2. API Gateway roteia para Lambda Function
   ↓
3. Lambda valida formato do CPF
   ↓
4. Lambda consulta cliente no RDS PostgreSQL
   ↓
5. Lambda verifica status do cliente
   ↓
6. Lambda gera token JWT (se autorizado)
   ↓
7. Lambda retorna token JWT ou erro
```

## Respostas da API

### Sucesso (200 OK)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "client": {
    "id": 1,
    "nome": "Cliente Exemplo",
    "cpfMascarado": "***.456.***-89"
  }
}
```

### Erro - CPF Inválido (400 Bad Request)

```json
{
  "error": "CPF inválido",
  "statusCode": 400
}
```

### Erro - Cliente Não Encontrado (404 Not Found)

```json
{
  "error": "Cliente não encontrado",
  "statusCode": 404
}
```

### Erro - Status Não Permitido (403 Forbidden)

```json
{
  "error": "Cliente com status não permitido",
  "statusCode": 403
}
```

## Critérios de Aceite (Tech Challenge Parte 1)

- [x] Criar a Function Serverless
- [x] Implementar validação do CPF informado
- [x] Consultar a existência do cliente na base de dados
- [x] Consultar o status do cliente
- [x] Gerar um token JWT para clientes válidos e autorizados
- [x] Retornar respostas de erro adequadas
- [x] Organizar variáveis de ambiente e configurações sensíveis de forma segura
- [x] Criar testes para os principais cenários da função
- [x] Criar pipeline de CI/CD para build, testes e deploy automático
- [x] Configurar as regras de branch e Pull Request do repositório

## Integração com API Gateway

A função é integrada com o API Gateway através do Terraform. Os endpoints configurados são:

### Endpoint Público (Autenticação)
- **Método**: POST
- **Rota**: `/auth/login`
- **Integração**: AWS_PROXY com Lambda
- **Descrição**: Endpoint público para autenticação via CPF

### Endpoint Protegido (Exemplo)
- **Método**: GET
- **Rota**: `/clientes/me`
- **Integração**: AWS_PROXY com Lambda
- **Authorizer**: JWT Authorizer
- **Descrição**: Endpoint protegido que requer JWT válido

### JWT Authorizer
- **Tipo**: JWT
- **Identity Source**: `$request.header.Authorization`
- **Audience**: `tech-challenge-api`
- **Issuer**: `tech-challenge-auth-lambda`

## Segurança

- Credenciais de banco armazenadas em variáveis de ambiente da Lambda
- Segredo JWT configurado via variável de ambiente
- CPFs mascarados em logs e respostas
- Conexão com RDS via VPC privada
- IAM roles com permissões mínimas necessárias

## Monitoramento e Logs

Os logs da função Lambda podem ser visualizados no AWS CloudWatch:

```bash
aws logs tail /aws/lambda/f1rsters-tech-challenge-mecanica-auth-function --follow
```

## Troubleshooting

### Erro de conexão com banco
- Verifique se as variáveis de ambiente `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` estão configuradas
- Verifique se o security group do RDS permite conexões da Lambda

### Erro de JWT
- Verifique se a variável `JWT_SECRET` está configurada e em Base64
- Certifique-se de que a chave tem pelo menos 256 bits

### Timeout da função
- Aumente o timeout da Lambda se necessário (configurado para 30s)
- Verifique se a conexão com o RDS está dentro da VPC

## Contribuição

Este projeto é parte do Tech Challenge FIAP. Para contribuições, siga as regras de branch e Pull Request configuradas no repositório principal.

## Documentação Adicional

- [Diagrama de Sequência da Autenticação](../../docs/authentication-sequence-diagram.md)
- [RFC - Estratégia de Autenticação](../../docs/rfc-authentication-strategy.md)
- [Guia de Testes da API](../../docs/api-testing-guide.md)

## Qualidade de Código

O projeto utiliza as seguintes ferramentas de qualidade de código:
- **Checkstyle**: Validação de estilo de código (Google Java Style)
- **SpotBugs**: Análise estática para encontrar bugs
- **JUnit 5**: Framework de testes unitários
- **Mockito**: Framework de mocking para testes

Essas validações são executadas automaticamente no pipeline CI/CD.
