# ADR-003 — Logs JSON e correlação por requisição

- **Status:** Aceita
- **Data:** 2026-09-12

## Contexto

A aplicação precisa fornecer logs estruturados e uma forma de acompanhar uma requisição entre API Gateway, Lambda e aplicação principal.

A solução deve funcionar em containers e ser consumida por ferramentas de monitoramento sem acoplar o código a um fornecedor específico.

## Decisão

Usar o logging estruturado nativo do Spring Boot no formato `logstash` e adotar o header:

```text
X-Correlation-ID
```

A aplicação:

- preserva o valor recebido;
- gera UUID quando o header não existe ou é inválido;
- devolve o valor no response header;
- coloca o valor no MDC com a chave `correlation_id`;
- registra método, caminho, status e duração;
- limpa o MDC ao final da requisição.

## Consequências

- Logs são emitidos em JSON sem dependência de encoder externo.
- A aplicação permanece independente de Datadog, New Relic ou outro fornecedor.
- Gateway e Lambda devem propagar o mesmo header para correlação ponta a ponta.
- O conteúdo dos logs não deve incluir CPF completo, tokens ou credenciais.
