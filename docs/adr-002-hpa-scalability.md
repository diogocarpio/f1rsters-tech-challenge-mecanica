# ADR-002: Horizontal Pod Autoscaler (HPA) e Estratégia de Escalabilidade

## Status
**Proposto** → **Aceito** → **Implementado**

## Contexto
A aplicação Tech Challenge Mecânica precisa suportar variações de carga de trabalho, incluindo picos de tráfego durante horários comerciais e períodos de baixa atividade. É necessário implementar uma estratégia de escalabilidade que garanta disponibilidade e performance sem desperdício de recursos.

## Decisão
**Implementar Horizontal Pod Autoscaler (HPA) no Kubernetes com métricas de CPU e memória, configurado para escalar de 1 a 5 réplicas.**

## Justificativa

### Fatores Considerados

#### 1. Tipo de Escalabilidade
**Horizontal vs Vertical:**

- **Horizontal Scaling (HPA)**: Adicionar mais pods
  - **Vantagens**: Melhor resiliência, distribuição de carga, zero downtime
  - **Desvantagens**: Maior complexidade de gerenciamento, overhead de rede
  
- **Vertical Scaling**: Aumentar recursos de pods existentes
  - **Vantagens**: Simplicidade, menor overhead
  - **Desvantagens**: Downtime durante scale-up, limite de recursos por nó

**Decisão:** Horizontal Scaling escolhido - melhor resiliência e sem downtime

#### 2. Métricas de Escalamento
**Métricas Disponíveis:**

- **CPU Utilization**: Métrica padrão, fácil de configurar
- **Memory Utilization**: Importante para aplicações com uso intensivo de memória
- **Custom Metrics**: Métricas de aplicação (ex: requests per second)
- **External Metrics**: Métricas de serviços externos (ex: SQS queue length)

**Decisão:** CPU e Memory escolhidos - métricas padrão e suficientes para o caso de uso

#### 3. Limites de Réplicas
**Considerações:**

- **Mínimo (1 réplica)**: Garante pelo menos um pod rodando
- **Máximo (5 réplicas)**: Limite baseado em:
  - Capacidade do cluster (3 nós t3.medium = 6 vCPUs totais)
  - Custo otimizado (5 pods x 500m CPU = 2.5 vCPUs utilizados)
  - Capacidade de processamento estimada (5 pods = ~500 req/s)

**Decisão:** 1-5 réplicas - balance entre disponibilidade e custo

#### 4. Thresholds de Escalamento
**CPU Threshold:**
- **Scale-up**: 70% de utilização
  - Justificativa: Permite buffer de 30% antes de atingir limite
  - Evita escalamento agressivo desnecessário
  
- **Scale-down**: 40% de utilização
  - Justificativa: Hysteresis para evitar flapping
  - Permite redução de custo quando carga diminui

**Memory Threshold:**
- **Scale-up**: 75% de utilização
  - Justificativa: Memória é crítica, threshold mais conservador
  - Evita OOM (Out of Memory) errors
  
- **Scale-down**: 50% de utilização
  - Justificativa: Hysteresis maior para memória

#### 5. Comportamento de Escalamento
**Configurações HPA:**

- **Stabilization Window (scale-up)**: 60 segundos
  - Justificativa: Evita escalamento rápido para picos temporários
  
- **Stabilization Window (scale-down)**: 300 segundos (5 minutos)
  - Justificativa: Evita scale-down agressivo, permite estabilização
  
- **Scale-up Period**: 15 segundos
  - Justificativa: Resposta rápida para aumento de carga
  
- **Scale-down Period**: 60 segundos
  - Justificativa: Resposta mais conservadora para redução

### Alternativas Consideradas

#### Vertical Pod Autoscaler (VPA)
**Vantagens:**
- Ajuste automático de requests/limits
- Simplicidade de configuração

**Desvantagens:**
- Requer restart de pods para aplicar mudanças
- Não aumenta resiliência (não adiciona réplicas)
- Conflito com HPA se ambos configurados

**Decisão:** Não escolhido - HPA oferece melhor resiliência

#### Cluster Autoscaler
**Vantagens:**
- Escala o cluster automaticamente (adiciona nós)
- Útil para cargas muito grandes

**Desvantagens:**
- Complexidade adicional
- Custo aumentado (mais nós = mais custo)
- Overhead de provisionamento de nós (minutos)

**Decisão:** Não escolhido - HPA é suficiente para o escopo atual, Cluster Autoscaler pode ser adicionado no futuro

#### Custom Metrics (Requests per Second)
**Vantagens:**
- Escalamento baseado em carga real de negócio
- Mais preciso para aplicações web

**Desvantagens:**
- Requer instalação de Prometheus Adapter
- Complexidade adicional de configuração
- Overhead de coleta de métricas customizadas

**Decisão:** Não escolhido - CPU/Memory são suficientes e mais simples

### Configuração HPA

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: oficina-app-hpa
  namespace: oficina
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: oficina-app
  minReplicas: 1
  maxReplicas: 5
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 75
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
        - type: Percent
          value: 100
          periodSeconds: 15
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 50
          periodSeconds: 60
```

### Configuração de Recursos dos Pods

```yaml
resources:
  requests:
    cpu: 500m
    memory: 512Mi
  limits:
    cpu: 1000m
    memory: 1Gi
```

**Justificativa:**
- **Requests**: Garantia mínima de recursos para o pod
- **Limits**: Limite máximo para evitar starving de outros pods
- **Ratio 2:1**: Boa prática para evitar overcommit excessivo

## Consequências

### Positivas
- **Alta disponibilidade**: Múltiplas réplicas distribuem carga e aumentam resiliência
- **Performance consistente**: Escalamento automático mantém performance sob carga
- **Custo otimizado**: Scale-down reduz custo quando carga diminui
- **Zero downtime**: Escalamento horizontal não interrompe serviço
- **Auto-gerenciamento**: HPA gerencia escalamento automaticamente

### Negativas
- **Complexidade**: Configuração e tuning de HPA requer conhecimento
- **Overhead**: Múltiplos pods consomem mais recursos de rede
- **Latência de escalamento**: Pode levar 1-2 minutos para escalar completamente
- **Métricas limitadas**: CPU/Memory podem não refletir carga de negócio real

### Mitigações
- **Complexidade**: Documentação detalhada e monitoramento
- **Overhead**: Configuração otimizada de requests/limits
- **Latência**: Configuração de stabilization windows para resposta rápida
- **Métricas limitadas**: Monitoramento de métricas de aplicação via New Relic

## Implementação

### Kubernetes Manifest
- **Arquivo**: `k8s/app-hpa.yaml`
- **Namespace**: `oficina`
- **Target**: Deployment `oficina-app`

### Terraform
- **Arquivo**: `infra/main.tf`
- **Resource**: `kubernetes_horizontal_pod_autoscaler_v2`
- **Variáveis**: `hpa_min_replicas`, `hpa_max_replicas`, `hpa_cpu_threshold`, `hpa_memory_threshold`

### Monitoramento
- **New Relic**: Dashboard com métricas de HPA (replicas, CPU, memory)
- **Kubernetes Metrics Server**: Requerido para HPA funcionar
- **Alertas**: Alertas para scale-up agressivo (indica problema de performance)

### Testes
- **Teste de carga**: Usar k6 ou JMeter para simular carga e validar escalamento
- **Teste de falha**: Remover pods manualmente e validar auto-recovery
- **Teste de scale-down**: Reduzir carga e validar redução de réplicas

## Referências
- [Kubernetes HPA Documentation](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
- [Kubernetes Resource Management](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)
- [AWS EKS Best Practices - Autoscaling](https://aws.github.io/aws-eks-best-practices/scalability/)

## Aprovação
- [x] Aluno 1 (Autenticação)
- [x] Aluno 2 (Kubernetes)
- [x] Aluno 3 (Aplicação)
- [ ] Professor/Reviewer

## Histórico de Mudanças
| Data | Versão | Autor | Mudança |
|------|--------|-------|---------|
| 14/09/2026 | 1.0 | Equipe | Versão inicial |
