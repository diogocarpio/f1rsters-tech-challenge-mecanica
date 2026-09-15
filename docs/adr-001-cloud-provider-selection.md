# ADR-001: Escolha do Provedor de Nuvem (AWS)

## Status
**Proposto** → **Aceito** → **Implementado**

## Contexto
O Tech Challenge Mecânica requer uma infraestrutura em nuvem para hospedar a aplicação, banco de dados gerenciado, funções serverless e serviços de observabilidade. É necessário escolher um provedor de nuvem que atenda aos requisitos de escalabilidade, custo, disponibilidade e conformidade com as melhores práticas de arquitetura.

## Decisão
**Escolher AWS (Amazon Web Services) como provedor de nuvem principal.**

## Justificativa

### Fatores Considerados

#### 1. Custo e Free Tier
- **AWS Free Tier**: Oferece 12 meses de serviços gratuitos para novos contas, incluindo:
  - 750 horas/mês de EC2 t2.micro ou t3.micro
  - 750 horas/mês de RDS db.t3.micro
  - 1 milhão de requisições/mês no API Gateway
  - 400.000 GB-segundos/mês no Lambda
- **Estimativa de custo mensal**: ~$50-100 após o período de Free Tier
- **Comparação**: Azure e GCP também oferecem Free Tier, mas AWS tem limites mais generosos para o caso de uso

#### 2. Maturidade e Estabilidade
- **AWS**: Lançado em 2006, provedor mais maduro do mercado
- **Serviços**: Mais de 200 serviços totalmente gerenciados
- **Estabilidade**: SLA de 99.99% para a maioria dos serviços críticos
- **Comunidade**: Maior comunidade de usuários e documentação disponível

#### 3. Integração com Serviços Requeridos
- **Lambda**: Serviço serverless nativo com excelente integração com API Gateway
- **API Gateway**: Suporte a HTTP API (mais performático e econômico que REST API)
- **RDS PostgreSQL**: Banco de dados gerenciado com backups automáticos, encryption e escalabilidade
- **EKS**: Serviço Kubernetes gerenciado com integração nativa com outros serviços AWS
- **CloudWatch**: Monitoramento e logs integrados com todos os serviços
- **S3**: Armazenamento durável para Terraform state e Lambda artifacts

#### 4. Disponibilidade Regional
- **Região sa-east-1 (São Paulo)**: Disponível para baixa latência no Brasil
- **Multi-AZ**: Suporte a múltiplas zonas de disponibilidade para alta disponibilidade
- **Edge Locations**: CloudFront para CDN global (se necessário no futuro)

#### 5. Ferramentas e Ecosystem
- **Terraform**: Suporte nativo e maduro para AWS
- **CI/CD**: Integração nativa com GitHub Actions, AWS CodePipeline, CodeBuild
- **Observabilidade**: Integração nativa com New Relic, Datadog, Prometheus
- **Kubernetes**: EKS com suporte a Helm, Kustomize e outras ferramentas

#### 6. Segurança e Conformidade
- **IAM**: Sistema de permissões granular e robusto
- **VPC**: Isolamento de rede com subnets privadas e públicas
- **Encryption**: Encryption at rest e in transit por padrão
- **Compliance**: Certificações SOC 1/2/3, ISO 27001, PCI DSS, HIPAA

#### 7. Curva de Aprendizado
- **Documentação**: Extensa documentação oficial e tutoriais
- **Comunidade**: Grande quantidade de exemplos, blogs e cursos
- **Suporte**: Diversas opções de suporte técnico (Community, Business, Enterprise)

### Alternativas Consideradas

#### Azure (Microsoft Azure)
**Vantagens:**
- Integração excelente com ecossistema Microsoft (.NET, Azure DevOps)
- Preços competitivos em alguns serviços
- Região Brazil South disponível

**Desvantagens:**
- Menor comunidade e documentação comparado a AWS
- Curva de aprendizado mais íngreme para equipes sem experiência Microsoft
- Algumas funcionalidades menos maduras que AWS

**Decisão:** Não escolhido - AWS oferece melhor maturidade e comunidade

#### GCP (Google Cloud Platform)
**Vantagens:**
- Melhor suporte a Kubernetes (GKE é considerado o melhor K8s gerenciado)
- Preços competitivos e transparentes
- Excelente suporte a Big Data e ML

**Desvantagens:**
- Menor oferta de serviços regionais no Brasil
- Comunidade menor que AWS
- Algumas funcionalidades menos maduras (ex: Lambda equivalent)

**Decisão:** Não escolhido - AWS tem melhor disponibilidade regional e comunidade

#### Multi-Cloud
**Vantagens:**
- Redução de vendor lock-in
- Melhor resiliência com provedores diferentes

**Desvantagens:**
- Complexidade operacional significativamente maior
- Custo aumentado devido a duplicação de recursos
- Curva de aprendizado muito íngreme
- Desnecessário para o escopo do Tech Challenge

**Decisão:** Não escolhido - Complexidade não justificada para o projeto atual

### Serviços AWS Selecionados

| Serviço | Uso | Justificativa |
|---------|-----|---------------|
| **EKS** | Cluster Kubernetes | K8s gerenciado com integração nativa AWS |
| **RDS PostgreSQL** | Banco de dados | PostgreSQL gerenciado com backups e escalabilidade |
| **Lambda** | Função de autenticação | Serverless para autenticação via CPF |
| **API Gateway** | Gateway de API | HTTP API para roteamento e autorização JWT |
| **S3** | Armazenamento | Terraform state, Lambda artifacts, backups |
| **CloudWatch** | Monitoramento | Logs e métricas integradas |
| **IAM** | Segurança | Controle de acesso granular |
| **VPC** | Rede | Isolamento de rede com subnets privadas |

## Consequências

### Positivas
- **Custo otimizado**: Free Tier reduz custos iniciais significativamente
- **Escalabilidade**: Serviços auto-scaling (Lambda, EKS HPA, RDS)
- **Alta disponibilidade**: Multi-AZ e SLA de 99.99%
- **Integração nativa**: Serviços AWS se integram perfeitamente
- **Comunidade robusta**: Facilita troubleshooting e aprendizado
- **Observabilidade**: CloudWatch + New Relic para monitoramento completo

### Negativas
- **Vendor lock-in**: Dependência de ecossistema AWS
- **Complexidade**: Curva de aprendizado inicial para equipe sem experiência AWS
- **Custo pós-Free Tier**: Custos podem aumentar significativamente após 12 meses

### Mitigações
- **Vendor lock-in**: Usar Terraform para Infraestrutura como Código, facilitando migração futura
- **Complexidade**: Documentação detalhada e treinamento da equipe
- **Custo**: Monitoramento de custos com AWS Budgets e otimização contínua

## Implementação

### Arquitetura AWS

```
┌─────────────────────────────────────────────────────────┐
│                     VPC (sa-east-1)                      │
│  ┌───────────────────────────────────────────────────┐  │
│  │              Public Subnets                        │  │
│  │  - API Gateway (HTTP API)                         │  │
│  │  - NAT Gateway                                     │  │
│  └───────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────┐  │
│  │              Private Subnets                      │  │
│  │  - EKS Cluster (Kubernetes)                        │  │
│  │  - RDS PostgreSQL                                 │  │
│  │  - Lambda Function (Auth)                          │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                  Supporting Services                     │
│  - S3 (Terraform State, Lambda Artifacts)              │
│  - CloudWatch (Logs, Metrics, Alarms)                  │
│  - IAM (Roles, Policies)                                │
│  - DynamoDB (Terraform Locks)                           │
└─────────────────────────────────────────────────────────┘
```

### Terraform Modules
- `aws/terraform/main.tf`: Configuração principal AWS
- `aws/terraform/newrelic.tf`: Integração New Relic
- `infra/main.tf`: Configuração Kubernetes local

### CI/CD
- GitHub Actions para build, testes e deploy
- Terraform para provisionamento de infraestrutura
- Kubectl/Helm para deploy no EKS

## Referências
- [AWS Free Tier](https://aws.amazon.com/free/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [EKS Best Practices](https://aws.github.io/aws-eks-best-practices/)
- [RDS PostgreSQL Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html)

## Aprovação
- [x] Aluno 1 (Autenticação)
- [x] Aluno 2 (Kubernetes)
- [x] Aluno 3 (Aplicação)
- [ ] Professor/Reviewer

## Histórico de Mudanças
| Data | Versão | Autor | Mudança |
|------|--------|-------|---------|
| 14/09/2026 | 1.0 | Equipe | Versão inicial |
