# Diagrama de Sequência - Abertura de Ordem de Serviço

## Visão Geral
Este diagrama ilustra o fluxo completo de abertura de uma ordem de serviço, desde a requisição inicial até a finalização.

## Diagrama de Sequência



## Fluxo de Atualização de Status

```mermaid
sequenceDiagram
    participant Cliente as Cliente (Frontend)
    participant API as API REST
    participant Controller as OrdemServicoController
    participant Service as OrdemServicoService
    participant RepoOS as OrdemServicoRepository
    participant DB as PostgreSQL

    Cliente->>API: PATCH /api/admin/ordens-servico/{id}/status
    Note over Cliente,DB: Body: {novoStatus: "DIAGNOSTICO"}
    
    API->>Controller: atualizarStatus(id, dto)
    Controller->>Service: atualizarStatus(id, novoStatus)
    
    Service->>RepoOS: findById(id)
    RepoOS->>DB: SELECT * FROM ordem_servico WHERE id = ?
    DB-->>RepoOS: OrdemServico
    RepoOS-->>Service: OrdemServico
    
    alt Ordem de Serviço não encontrada
        Service-->>Controller: 404 Not Found
        Controller-->>API: 404 Not Found
        API-->>Cliente: {error: "Ordem de serviço não encontrada"}
    else Ordem de Serviço encontrada
        Service->>Service: validarTransicaoStatus(statusAtual, novoStatus)
        
        alt Transição inválida
            Service-->>Controller: 400 Bad Request
            Controller-->>API: 400 Bad Request
            API-->>Cliente: {error: "Transição de status inválida"}
        else Transição válida
            Service->>Service: atualizarStatus(ordemServico, novoStatus)
            Service->>RepoOS: save(ordemServico)
            RepoOS->>DB: UPDATE ordem_servico SET status = ? WHERE id = ?
            DB-->>RepoOS: OrdemServico
            RepoOS-->>Service: OrdemServico
            
            Service-->>Controller: OrdemServico (status atualizado)
            Controller-->>API: 200 OK
            API-->>Cliente: {id, status: novoStatus, ...}
        end
    end
```

## Fluxo de Resposta de Orçamento

```mermaid
sequenceDiagram
    participant Cliente as Cliente (Frontend)
    participant API as API REST
    participant Controller as OrdemServicoController
    participant Service as OrdemServicoService
    participant RepoOS as OrdemServicoRepository
    participant DB as PostgreSQL

    Cliente->>API: POST /api/admin/ordens-servico/{id}/orcamento/resposta
    Note over Cliente,DB: Body: {aprovado: true/false, observacao: "..."}
    
    API->>Controller: responderOrcamento(id, dto)
    Controller->>Service: processarRespostaOrcamento(id, dto)
    
    Service->>RepoOS: findById(id)
    RepoOS->>DB: SELECT * FROM ordem_servico WHERE id = ?
    DB-->>RepoOS: OrdemServico
    RepoOS-->>Service: OrdemServico
    
    alt Ordem de Serviço não encontrada
        Service-->>Controller: 404 Not Found
        Controller-->>API: 404 Not Found
        API-->>Cliente: {error: "Ordem de serviço não encontrada"}
    else Ordem de Serviço encontrada
        alt Status não é AGUARDANDO_APROVACAO
            Service-->>Controller: 400 Bad Request
            Controller-->>API: 400 Bad Request
            API-->>Cliente: {error: "Ordem de serviço não está aguardando aprovação"}
        else Status correto
            alt Orçamento aprovado
                Service->>Service: atualizarStatus(ordemServico, EM_EXECUCAO)
            else Orçamento rejeitado
                Service->>Service: atualizarStatus(ordemServico, RECEBIDA)
            end
            
            Service->>RepoOS: save(ordemServico)
            RepoOS->>DB: UPDATE ordem_servico SET status = ? WHERE id = ?
            DB-->>RepoOS: OrdemServico
            RepoOS-->>Service: OrdemServico
            
            Service-->>Controller: OrdemServico (status atualizado)
            Controller-->>API: 200 OK
            API-->>Cliente: {id, status, ...}
        end
    end
```

## Fluxo de Notificação de Status (Integração Externa)

```mermaid
sequenceDiagram
    participant SistemaExterno as Sistema Externo
    participant API as API REST
    participant Controller as OrdemServicoController
    participant Service as OrdemServicoService
    participant RepoOS as OrdemServicoRepository
    participant DB as PostgreSQL

    SistemaExterno->>API: POST /api/admin/ordens-servico/{id}/status/notificacao
    Note over SistemaExterno,DB: Body: {status: "FINALIZADA", observacao: "..."}
    
    API->>Controller: notificarStatus(id, dto)
    Controller->>Service: processarNotificacaoStatus(id, dto)
    
    Service->>RepoOS: findById(id)
    RepoOS->>DB: SELECT * FROM ordem_servico WHERE id = ?
    DB-->>RepoOS: OrdemServico
    RepoOS-->>Service: OrdemServico
    
    alt Ordem de Serviço não encontrada
        Service-->>Controller: 404 Not Found
        Controller-->>API: 404 Not Found
        API-->>SistemaExterno: {error: "Ordem de serviço não encontrada"}
    else Ordem de Serviço encontrada
        Service->>Service: validarStatusRecebido(dto.status)
        
        alt Status inválido
            Service-->>Controller: 400 Bad Request
            Controller-->>API: 400 Bad Request
            API-->>SistemaExterno: {error: "Status inválido"}
        else Status válido
            Service->>Service: atualizarStatus(ordemServico, dto.status)
            Service->>RepoOS: save(ordemServico)
            RepoOS->>DB: UPDATE ordem_servico SET status = ? WHERE id = ?
            DB-->>RepoOS: OrdemServico
            RepoOS-->>Service: OrdemServico
            
            Service-->>Controller: OrdemServico (status atualizado)
            Controller-->>API: 200 OK
            API-->>SistemaExterno: {id, status, ...}
        end
    end
```

## Estados da Ordem de Serviço

### Status Possíveis

| Status | Descrição | Próximos Status Possíveis |
|--------|-----------|---------------------------|
| **RECEBIDA** | Ordem de serviço criada, aguardando diagnóstico | DIAGNOSTICO |
| **DIAGNOSTICO** | Veículo em diagnóstico | AGUARDANDO_APROVACAO |
| **AGUARDANDO_APROVACAO** | Orçamento gerado, aguardando aprovação do cliente | EM_EXECUCAO (aprovado), RECEBIDA (rejeitado) |
| **EM_EXECUCAO** | Serviços sendo executados | FINALIZADA |
| **FINALIZADA** | Serviços concluídos, aguardando entrega | ENTREGUE |
| **ENTREGUE** | Veículo entregue ao cliente | - |

### Regras de Transição

1. **RECEBIDA → DIAGNOSTICO**: Início do processo de diagnóstico
2. **DIAGNOSTICO → AGUARDANDO_APROVACAO**: Diagnóstico concluído, orçamento gerado
3. **AGUARDANDO_APROVACAO → EM_EXECUCAO**: Cliente aprovou o orçamento
4. **AGUARDANDO_APROVACAO → RECEBIDA**: Cliente rejeitou o orçamento (retrabalho)
5. **EM_EXECUCAO → FINALIZADA**: Serviços concluídos
6. **FINALIZADA → ENTREGUE**: Veículo entregue ao cliente

## Endpoints da API

### Criar Ordem de Serviço
```
POST /api/admin/ordens-servico
Content-Type: application/json

Request:
{
  "clienteId": 1,
  "veiculoId": 1,
  "servicosIds": [1, 2, 3],
  "pecasIds": [1, 2]
}

Response (200 OK):
{
  "id": 123,
  "cliente": {...},
  "veiculo": {...},
  "servicos": [...],
  "pecas": [...],
  "valorTotal": 1500.00,
  "status": "RECEBIDA",
  "criadoEm": "2026-09-14T10:00:00"
}
```

### Atualizar Status
```
PATCH /api/admin/ordens-servico/{id}/status
Content-Type: application/json

Request:
{
  "novoStatus": "DIAGNOSTICO"
}

Response (200 OK):
{
  "id": 123,
  "status": "DIAGNOSTICO",
  ...
}
```

### Responder Orçamento
```
POST /api/admin/ordens-servico/{id}/orcamento/resposta
Content-Type: application/json

Request:
{
  "aprovado": true,
  "observacao": "Orçamento aprovado"
}

Response (200 OK):
{
  "id": 123,
  "status": "EM_EXECUCAO",
  ...
}
```

### Notificar Status (Integração Externa)
```
POST /api/admin/ordens-servico/{id}/status/notificacao
Content-Type: application/json

Request:
{
  "status": "FINALIZADA",
  "observacao": "Serviços concluídos com sucesso"
}

Response (200 OK):
{
  "id": 123,
  "status": "FINALIZADA",
  ...
}
```

## Métricas Coletadas

As seguintes métricas são coletadas pelo New Relic para monitoramento:

- **Volume diário de ordens de serviço**: `ordem_servico.created.total`
- **Tempo médio por status**: `ordem_servico.status.lead_time.seconds`
- **Falhas de processamento**: `ordem_servico.processing.failure.total`

## Integrações

### Integração Externa para Notificação de Status
- **Endpoint**: `POST /api/admin/ordens-servico/{id}/status/notificacao`
- **Propósito**: Sistema externo notifica mudanças de status (ex: sistema de pagamento, sistema de estoque)
- **Autenticação**: Requer autenticação JWT com role ADMIN
- **Validação**: Status recebido deve ser válido para o contexto atual

## Segurança

- Todos os endpoints requerem autenticação JWT
- Role ADMIN necessária para criar e atualizar ordens de serviço
- Consulta pública de status disponível via endpoint público (sem autenticação)
- Logs estruturados em JSON com correlation ID para rastreamento
