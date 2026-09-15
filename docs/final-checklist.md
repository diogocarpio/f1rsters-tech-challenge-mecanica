# Checklist Final — Tech Challenge Fase 3

Legenda:

- **Atendido:** implementação ou documentação confirmada.
- **Configurado:** estrutura existe, mas a execução/evidência final ainda precisa ser confirmada.
- **Dependente:** responsabilidade ou informação de outro integrante/grupo.
- **Pendente:** ainda não existe no material analisado.

## Autenticação e API Gateway

| Item | Status | Evidência/observação |
|---|---|---|
| API Gateway | Atendido | Terraform da Lambda |
| Validação de CPF | Atendido | Lambda `CpfValidator` |
| Consulta de cliente e status | Atendido | Lambda + campo `Cliente.status` |
| Geração de JWT | Atendido | JWT com CPF, issuer, audience e status |
| Rota protegida por CPF | Atendido | `GET /api/clientes/me` na aplicação |
| Encaminhamento da rota protegida pelo Gateway | Dependente | Alinhar com responsável pela Lambda/API Gateway |

## Repositórios e CI/CD

| Item | Status | Evidência/observação |
|---|---|---|
| Repositório da aplicação | Atendido | Link no diagrama de componentes |
| Repositório Kubernetes | Atendido | Link no diagrama de componentes |
| Repositório do banco | Atendido | Link no diagrama de componentes |
| Repositório Lambda | Atendido | Link no diagrama de componentes |
| README nos quatro repositórios | Configurado | Revisar conteúdo final de cada repositório |
| Workflow CI/CD nos quatro repositórios | Configurado | Validar execução final de cada um |
| `main/master` protegida | Pendente | Configuração administrativa do GitHub |
| Pull Requests obrigatórios | Pendente | Configuração administrativa do GitHub |
| Deploy automático de homologação | Configurado | Validar evidência final |
| Deploy automático de produção | Configurado | Validar evidência final |

## Infraestrutura

| Item | Status | Evidência/observação |
|---|---|---|
| PostgreSQL gerenciado | Atendido | Repositório Terraform do banco |
| Terraform do banco | Atendido | RDS, state remoto e pipeline |
| Kubernetes | Atendido | Repositório Terraform Kubernetes/EKS |
| HPA | Atendido | Configuração Kubernetes |
| Aplicação configurada para RDS | Pendente | Deployment atual ainda referencia `postgres-db` |
| Imagem publicada | Pendente | Confirmar registry e publicação |
| Deploy da aplicação no Kubernetes | Pendente | Pipeline da aplicação ainda precisa ser concluído |

## Observabilidade

| Item | Status | Evidência/observação |
|---|---|---|
| Integração New Relic | Configurado | Terraform, Helm, Java Agent e Micrometer |
| Latência de APIs | Configurado | Métricas e alerta NRQL |
| CPU e memória Kubernetes | Configurado | `nri-bundle` e dashboard |
| Healthchecks | Atendido | Actuator e probes Kubernetes |
| Uptime | Configurado | Alerta definido; validar monitor ativo |
| Falhas de processamento de OS | Configurado | Métricas e alerta NRQL |
| Logs JSON | Atendido | Logback/LogstashEncoder |
| Correlação entre requisições | Atendido | `X-Request-Id`, `X-Correlation-ID` e MDC |
| Dashboard de volume diário de OS | Configurado | Dashboard New Relic definido |
| Tempo médio por status | Configurado | Métrica e widget definidos |
| Erros e falhas de integração | Configurado | Métricas e widget definidos |
| Evidência de dashboard ativo | Pendente | Depende do ambiente New Relic |

## Documentação

| Item | Status | Evidência/observação |
|---|---|---|
| Diagrama de componentes | Atendido | `docs/component-diagram.md` |
| Sequência de autenticação | Atendido | `docs/authentication-sequence-diagram.md` |
| Sequência de abertura de OS | Atendido | `docs/order-service-sequence-diagram.md` |
| RFC de autenticação | Atendido | `docs/rfc-authentication-strategy.md` |
| ADRs | Atendido | `docs/adr/` |
| Justificativa do banco | Atendido | `docs/database-model.md` |
| Diagrama ER | Atendido | `docs/database-model.md` |
| Relacionamentos | Atendido | `docs/database-model.md` |
| Avaliação de índices | Atendido | `docs/database-model.md` |
| Links dos quatro repositórios | Atendido | `docs/component-diagram.md` |
| Checklist final | Atendido | Este documento |

## Entrega final

| Item | Status | Evidência/observação |
|---|---|---|
| Vídeo de até 15 minutos | Pendente | Gravar após validação integrada |
| Autenticação com CPF no vídeo | Pendente | Depende do fluxo integrado |
| Execução de pipeline no vídeo | Pendente | Depende das pipelines finais |
| Deploy automatizado no vídeo | Pendente | Depende do deploy da aplicação |
| API protegida no vídeo | Pendente | Depende do Gateway → aplicação |
| Dashboard ao vivo no vídeo | Pendente | Depende do New Relic ativo |
| Logs e traces no vídeo | Pendente | Depende do ambiente integrado |
| PDF único | Pendente | Consolidar após evidências finais |
| Usuário `soat-architecture` nos quatro repositórios | Pendente | Confirmar nas configurações GitHub |
