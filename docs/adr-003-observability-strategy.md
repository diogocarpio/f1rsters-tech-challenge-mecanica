# ADR-003: Estratégia de Observabilidade (New Relic)

## Status
**Proposto** → **Aceito** → **Implementado**

## Contexto
O Tech Challenge Mecânica requer uma estratégia completa de observabilidade para monitorar a aplicação, infraestrutura Kubernetes e serviços AWS. É necessário coletar métricas, logs e traces para garantir disponibilidade, performance e capacidade de troubleshooting efetivo.

## Decisão
**Implementar New Relic como solução de observabilidade unificada, com integração via Java Agent para APM, nri-bundle Helm chart para Kubernetes e NRQL para dashboards e alertas.**

## Justificativa

### Fatores Considerados

#### 1. Ferramenta de Observabilidade
**Opções Avaliadas:**

- **New Relic**
  - Vantagens: Solução all-in-one (APM, Infra, Logs, Synthetics), integração nativa com Kubernetes, excelente UI, NRQL poderoso
  - Desvantagens: Custo mais alto que algumas alternativas
  
- **Datadog**
  - Vantagens: Excelente para infraestrutura, integrações vastas, UI intuitiva
  - Desvantagens: Custo mais alto, curva de aprendizado para configuração avançada
  
- **Prometheus + Grafana**
  - Vantagens: Open source, custo zero (self-hosted), comunidade ativa
  - Desvantagens: Complexidade de setup e manutenção, requires infraestrutura adicional, menor integração com AWS
  
- **CloudWatch (AWS Native)**
  - Vantagens: Nativo AWS, custo incluído em serviços AWS, integração perfeita
  - Desvantagens: UI menos intuitiva, menor funcionalidade de APM, logs não estruturados por padrão

**Decisão:** New Relic escolhido - melhor balanço entre funcionalidade, integração e facilidade de uso

#### 2. Componentes de Observabilidade
**Três Pilares:**

- **Métricas (Metrics)**: Dados numéricos agregados ao longo do tempo
  - Latência das APIs
  - Consumo de CPU e memória
  - Healthchecks
  - Uptime
  - Volume de ordens de serviço
  
- **Logs (Logging)**: Eventos discretos com contexto
  - Logs estruturados em JSON
  - Correlação de requisições (correlation ID)
  - Logs de aplicação e infraestrutura
  
- **Traces (Distributed Tracing)**: Rastreamento de requisições através de serviços
  - Distributed tracing via Java Agent
  - Spans para cada operação
  - Correlação entre serviços

**Decisão:** Três pilares implementados - visibilidade completa

#### 3. Integração com Aplicação
**Java Agent New Relic:**

- **APM (Application Performance Monitoring)**:
  - Monitoramento de transações HTTP
  - Métricas de latência, throughput, error rate
  - Database query monitoring
  - External service calls
  
- **Distributed Tracing**:
  - Traces automáticos para requisições HTTP
  - Spans para operações de banco de dados
  - Correlação entre serviços
  
- **Custom Metrics**:
  - Métricas de negócio (ordens de serviço criadas, status changes)
  - Métricas customizadas via Micrometer

**Decisão:** Java Agent escolhido - integração transparente e automática

#### 4. Integração com Kubernetes
**nri-bundle Helm Chart:**

- **Kubernetes Integration**:
  - Coleta de métricas de pods, nodes, containers
  - Monitoramento de recursos (CPU, memory, network)
  - Health checks de pods
  
- **Events**:
  - Eventos Kubernetes (pod scheduling, crashes, restarts)
  - Alertas baseados em eventos
  
- **Infrastructure Monitoring**:
  - Monitoramento de nodes
  - Monitoramento de namespaces
  - Monitoramento de deployments

**Decisão:** nri-bundle escolhido - integração completa e fácil de configurar

#### 5. Dashboards Obrigatórios
**Requisitos do Tech Challenge:**

- **Volume diário de ordens de serviço**
  - Métrica: `ordem_servico.created.total`
  - Widget: Billboard com timeseries de 1 dia
  
- **Tempo médio por status**
  - Métrica: `ordem_servico.status.lead_time.seconds`
  - Widget: Line chart facetado por status (DIAGNOSTICO, EM_EXECUCAO, FINALIZADA)
  
- **Erros e falhas de integração**
  - Métrica: `ordem_servico.processing.failure.total`
  - Widget: Line chart facetado por reason
  
- **Saúde e recursos do ambiente**
  - Métricas: CPU, memory do Kubernetes
  - Widget: Area chart com clusterName

**Decisão:** Todos os dashboards implementados via Terraform (newrelic.tf)

#### 6. Alertas Configurados
**Alertas NRQL:**

- **API Latency p95**:
  - Query: `SELECT percentile(duration, 95) FROM Transaction WHERE appName = 'tech-challenge-mecanica'`
  - Critical: > 1.5s
  - Warning: > 0.8s
  
- **Uptime**:
  - Query: `SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck WHERE monitorName = 'tech-challenge-mecanica-healthcheck'`
  - Critical: < 99.5%
  - Warning: < 99.9%
  
- **Healthcheck failures**:
  - Query: `SELECT count(*) FROM Transaction WHERE appName = 'tech-challenge-mecanica' AND name LIKE '%/actuator/health%' AND httpResponseCode != '200'`
  - Critical: > 3 falhas em 5 minutos
  
- **Falhas processamento OS**:
  - Query: `SELECT sum(newrelic.timeslice.value) FROM Metric WHERE metricName = 'ordem_servico.processing.failure.total'`
  - Critical: > 5 falhas em 10 minutos

**Decisão:** Todos os alertas implementados via Terraform (newrelic.tf)

### Alternativas Consideradas

#### Prometheus + Grafana + Loki
**Vantagens:**
- Open source, custo zero
- Comunidade ativa e vasta
- Flexibilidade máxima

**Desvantagens:**
- Complexidade de setup e manutenção
- Requer infraestrutura adicional (Prometheus server, Grafana, Loki)
- Menor integração com AWS e Kubernetes
- Curva de aprendizado íngreme

**Decisão:** Não escolhido - New Relic oferece solução gerenciada e mais fácil de implementar

#### Datadog
**Vantagens:**
- Excelente para infraestrutura
- Integrações vastas
- UI intuitiva

**Desvantagens:**
- Custo mais alto que New Relic
- Menor foco em APM
- Configuração mais complexa para distributed tracing

**Decisão:** Não escolhido - New Relic oferece melhor custo/benefício para o caso de uso

#### CloudWatch + X-Ray
**Vantagens:**
- Nativo AWS, sem custo adicional de licença
- Integração perfeita com serviços AWS
- X-Ray para distributed tracing

**Desvantagens:**
- UI menos intuitiva
- Menor funcionalidade de APM
- Logs não estruturados por padrão
- Menor flexibilidade para dashboards customizados

**Decisão:** Não escolhido - New Relic oferece melhor experiência de usuário e funcionalidade

## Implementação

### Aplicação (Java Agent)
- **Configuração**: `newrelic.yml` na raiz do projeto
- **Variáveis de ambiente**:
  - `NEW_RELIC_ENABLED=true`
  - `NEW_RELIC_LICENSE_KEY=<license_key>`
  - `NEW_RELIC_APP_NAME=tech-challenge-mecanica`
  - `NEW_RELIC_DISTRIBUTED_TRACING_ENABLED=true`
  - `NEW_RELIC_METRICS_ENABLED=true`
  - `NEW_RELIC_ACCOUNT_ID=<account_id>`
  - `NEW_RELIC_API_KEY=<api_key>`

### Kubernetes (nri-bundle)
- **Helm Chart**: `nri-bundle` via Terraform
- **Configuração**:
  - `global.licenseKey`: License key New Relic
  - `global.cluster`: Nome do cluster EKS
  - Namespace: `newrelic`

### Terraform (Dashboards e Alertas)
- **Arquivo**: `aws/terraform/newrelic.tf`
- **Recursos**:
  - `newrelic_alert_policy`: Política de alertas
  - `newrelic_nrql_alert_condition`: Condições de alerta (latência, uptime, healthcheck, falhas OS)
  - `newrelic_one_dashboard`: Dashboard com widgets obrigatórios

### Logs Estruturados
- **Formato**: JSON via Logback
- **Configuração**: `src/main/resources/logback-spring.xml`
- **Campos**: timestamp, level, logger, message, correlationId, userId, etc.

### Correlação de Requisições
- **Correlation ID**: Header `X-Correlation-ID` gerado automaticamente
- **Filter**: `RequestCorrelationFilter` injeta correlation ID no MDC
- **Logs**: Correlation ID incluído em todos os logs da requisição

### Métricas Customizadas
- **Micrometer**: Integração com Spring Boot Actuator
- **Métricas de negócio**:
  - `ordem_servico.created.total`: Contador de ordens criadas
  - `ordem_servico.status.lead_time.seconds`: Tempo por status
  - `ordem_servico.processing.failure.total`: Falhas de processamento

## Consequências

### Positivas
- **Visibilidade completa**: Métricas, logs e traces em uma única plataforma
- **Alertas proativos**: Notificação de problemas antes que afetem usuários
- **Troubleshooting eficiente**: Correlação entre métricas, logs e traces
- **Dashboards customizados**: Visibilidade específica para o negócio
- **Integração nativa**: Fácil integração com Kubernetes e AWS
- **Auto-instrumentação**: Java Agent coleta dados automaticamente

### Negativas
- **Custo**: New Relic tem custo baseado em hosts e ingestão de dados
- **Vendor lock-in**: Dependência de plataforma proprietária
- **Complexidade inicial**: Configuração e tuning requer conhecimento
- **Overhead de agente**: Java Agent adiciona overhead (~5-10% CPU)

### Mitigações
- **Custo**: Monitoramento de ingestão e otimização de métricas
- **Vendor lock-in**: Uso de padrões abertos (OpenTelemetry) para possível migração
- **Complexidade**: Documentação detalhada e treinamento
- **Overhead**: Configuração otimizada do agente e sampling de traces

## Referências
- [New Relic Documentation](https://docs.newrelic.com/)
- [New Relic Kubernetes Integration](https://docs.newrelic.com/docs/kubernetes-pixie/kubernetes-integration/get-started/introduction-kubernetes-integration)
- [New Relic Java Agent](https://docs.newrelic.com/docs/apm/agents/java-agent/getting-started/introduction-new-relic-java)
- [NRQL Reference](https://docs.newrelic.com/docs/query-your-data/nrql-get-started/nrql-syntax)

## Aprovação
- [x] Aluno 1 (Autenticação)
- [x] Aluno 2 (Kubernetes)
- [x] Aluno 3 (Aplicação)
- [ ] Professor/Reviewer

## Histórico de Mudanças
| Data | Versão | Autor | Mudança |
|------|--------|-------|---------|
| 14/09/2026 | 1.0 | Equipe | Versão inicial |
