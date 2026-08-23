# Arquitetura AWS - Tech Challenge Mecanica

## Visão Geral

Esta documentação descreve a arquitetura AWS implementada para o Tech Challenge Parte 1, focada em autenticação serverless usando Lambda, API Gateway e RDS.

## Diagrama de Arquitetura

```
┌─────────────────┐
│   Client/App    │
└────────┬────────┘
         │ HTTPS Request
         ↓
┌─────────────────────────────────────┐
│      AWS API Gateway (HTTP API)     │
│  - POST /auth/login                  │
│  - JWT Validation (rotas protegidas) │
└────────┬────────────────────────────┘
         │
         ↓
┌─────────────────────────────────────┐
│   AWS Lambda Function (Auth)        │
│  - Java 17 Runtime                  │
│  - CPF Validation                   │
│  - Client Database Query            │
│  - JWT Generation                   │
└────────┬────────────────────────────┘
         │
         ↓
┌─────────────────────────────────────┐
│     AWS RDS PostgreSQL              │
│  - Database: oficina                │
│  - Instance: db.t3.micro            │
│  - VPC Private Subnets              │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│   Supporting Services               │
│  - S3 (Terraform State)             │
│  - S3 (Lambda Artifacts)            │
│  - DynamoDB (Terraform Locks)       │
│  - CloudWatch (Logs & Metrics)      │
│  - IAM (Roles & Policies)           │
└─────────────────────────────────────┘
```

## Componentes AWS

### 1. API Gateway
- **Tipo**: HTTP API
- **Função**: Gateway de entrada para requisições HTTP
- **Endpoints**:
  - `POST /auth/login` - Autenticação via CPF
- **Integração**: AWS Proxy com Lambda Function
- **Features**:
  - Roteamento automático para Lambda
  - Suporte a CORS (se necessário)
  - Logs de acesso no CloudWatch

### 2. Lambda Function (Auth)
- **Nome**: `f1rsters-tech-challenge-mecanica-auth-function`
- **Runtime**: Java 17
- **Handler**: `com.f1rsters.tech_challenge_mecanica.lambda.AuthHandler::handleRequest`
- **Timeout**: 30 segundos
- **Memory**: 512 MB
- **Funções**:
  - Validação de CPF (algoritmo oficial)
  - Consulta de cliente no RDS PostgreSQL
  - Verificação de status do cliente
  - Geração de token JWT
  - Tratamento de erros adequado

### 3. RDS PostgreSQL
- **Engine**: PostgreSQL 15.4
- **Instance Class**: db.t3.micro (Free Tier eligible)
- **Storage**: 20 GB GP2
- **Multi-AZ**: Não (para reduzir custos)
- **Backup**: 7 dias retention
- **Security**: Encrypted at rest
- **VPC**: Private subnets apenas
- **Database Name**: oficina
- **Features**:
  - Automatic minor version upgrades
  - Automated backups
  - Enhanced monitoring (opcional)

### 4. S3 Buckets

#### Terraform State Bucket
- **Nome**: `f1rsters-tech-challenge-terraform-state`
- **Função**: Armazenar estado do Terraform
- **Features**:
  - Versioning enabled
  - Server-side encryption
  - Access logging (opcional)

#### Lambda Artifacts Bucket
- **Nome**: `f1rsters-tech-challenge-lambda-artifacts`
- **Função**: Armazenar JARs da Lambda Function
- **Features**:
  - Versioning enabled
  - Lifecycle policies (opcional)

### 5. DynamoDB
- **Table**: `terraform-locks`
- **Função**: Lock state para Terraform
- **Billing Mode**: PAY_PER_REQUEST
- **Partition Key**: LockID (String)

### 6. IAM Roles & Policies

#### Lambda Execution Role
- **Nome**: `f1rsters-tech-challenge-mecanica-lambda-auth-role`
- **Managed Policies**:
  - `AWSLambdaBasicExecutionRole`
- **Inline Policies**:
  - `rds-db:connect` - Acesso ao RDS
  - `logs:CreateLogGroup`, `logs:CreateLogStream`, `logs:PutLogEvents` - CloudWatch Logs

### 7. CloudWatch
- **Logs**: Logs da Lambda Function
- **Metrics**: Métricas de invocação, erros, duração
- **Alarms** (opcional):
  - Erros da Lambda
  - Throttles da Lambda
  - Latência alta

## Fluxo de Autenticação

### 1. Login com CPF
```
Client → API Gateway → Lambda → RDS → Lambda → API Gateway → Client
```

**Passo a passo:**
1. Cliente envia POST `/auth/login` com CPF
2. API Gateway roteia para Lambda Function
3. Lambda valida formato do CPF
4. Lambda consulta cliente no RDS PostgreSQL
5. Lambda verifica status do cliente (ATIVO, APROVADO, VERIFICADO)
6. Lambda gera token JWT (15 minutos de expiração)
7. Lambda retorna token JWT + informações do cliente (CPF mascarado)

### 2. Acesso a Rotas Protegidas
```
Client → API Gateway → JWT Validation → Lambda → RDS → Lambda → API Gateway → Client
```

**Passo a passo:**
1. Cliente envia requisição com header `Authorization: Bearer <token>`
2. API Gateway valida token JWT
3. Se válido, roteia para Lambda Function
4. Lambda processa requisição
5. Lambda retorna resultado

## Variáveis de Ambiente

### Lambda Function
| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `DB_HOST` | Endpoint RDS | `tech-challenge-postgres.xxxx.sa-east-1.rds.amazonaws.com:5432` |
| `DB_NAME` | Nome do database | `oficina` |
| `DB_USERNAME` | Usuário do database | `oficinauser` |
| `DB_PASSWORD` | Senha do database | `*****` |
| `JWT_SECRET` | Segredo JWT (Base64) | `QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo=` |

## Security Considerations

### Network Security
- RDS em VPC private subnets (sem acesso público)
- Security Groups restringem acesso ao RDS
- API Gateway com HTTPS apenas

### Data Security
- RDS encrypted at rest
- Senhas em variáveis de ambiente (não em código)
- CPFs mascarados em logs e respostas
- JWT tokens com expiração curta (15 minutos)

### Access Control
- IAM roles com least privilege
- Credenciais AWS em GitHub Actions Secrets
- Terraform state encrypted em S3

## Cost Optimization

### Free Tier Usage
- Lambda: 400.000 GB-segundos/mês (suficiente)
- API Gateway: 1 milhão de chamadas/mês (suficiente)
- RDS db.t3.micro: 750 horas/mês (suficiente)
- S3: 5 GB de armazenamento (suficiente)
- DynamoDB: 25 GB de armazenamento + 200 unidades de capacidade (suficiente)

### Cost Saving Measures
- Usar db.t3.micro (Free Tier)
- Desligar recursos quando não em uso
- Configurar lifecycle policies no S3
- Monitorar custos com AWS Budgets

## Monitoring & Observability

### CloudWatch Logs
- Logs da Lambda Function automaticamente
- Estrutura: `/aws/lambda/f1rsters-tech-challenge-mecanica-auth-function`

### CloudWatch Metrics
- Invocations, Errors, Duration, Throttles
- IteratorAge (para streams, se aplicável)

### Alarms Sugeridos
- Lambda Error Rate > 5%
- Lambda Duration > 25s (warning), > 28s (critical)
- RDS CPU > 80%
- RDS Connections > 80% max

## Disaster Recovery

### Backups
- RDS automated backups (7 dias)
- S3 versioning (recuperação de arquivos)
- Terraform state em S3 (versioning)

### Recovery Time Objective (RTO)
- Lambda: ~5 minutos (deploy via CI/CD)
- RDS: ~30 minutos (restore from snapshot)

### Recovery Point Objective (RPO)
- Lambda: Código no Git (0 perda)
- RDS: 15 minutos (automated backups)

## Scalability

### Lambda
- Auto-scaling automático
- Concurrent executions limit (configurável)
- Cold start mitigation (provisioned concurrency se necessário)

### API Gateway
- Auto-scaling automático
- Burst capacity
- Throttling limits configuráveis

### RDS
- Vertical scaling (change instance class)
- Read replicas (se necessário)
- Multi-AZ (para produção crítica)

## Deployment Strategy

### CI/CD Pipeline
1. Build Lambda Function (Maven)
2. Executar testes unitários
3. Upload JAR para S3
4. Update Lambda function code
5. Terraform plan (PR)
6. Terraform apply (merge)

### Environments
- **dev**: Desenvolvimento (auto-deploy)
- **homolog**: Homologação (manual approval)
- **prod**: Produção (manual approval)

## Troubleshooting

### Lambda Issues
- **Cold starts**: Aumentar timeout/memory
- **Memory limits**: Monitorar CloudWatch metrics
- **Timeout errors**: Otimizar código ou aumentar timeout

### RDS Issues
- **Connection errors**: Verificar security groups
- **Performance**: Analisar slow queries, aumentar instance class
- **Storage full**: Aumentar allocated storage

### API Gateway Issues
- **4xx errors**: Validar request format
- **5xx errors**: Verificar Lambda logs
- **Throttling**: Aumentar limits

## Future Enhancements

### Short Term
- Adicionar autorização baseada em roles no JWT
- Implementar refresh tokens
- Adicionar rate limiting no API Gateway
- Configurar WAF para proteção

### Long Term
- Migrar para Cognito para autenticação completa
- Implementar EventBridge para eventos assíncronos
- Adicionar Step Functions para workflows complexos
- Implementar X-Ray para distributed tracing

## References

- [AWS Lambda Best Practices](https://docs.aws.amazon.com/lambda/latest/dg/best-practices.html)
- [API Gateway Best Practices](https://docs.aws.amazon.com/apigateway/latest/developerguide/best-practices.html)
- [RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
