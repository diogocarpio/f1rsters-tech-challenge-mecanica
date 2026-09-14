# ADR-003 — Logs JSON e correlação por requisição

- **Status:** Aceita
- **Data:** 2026-09-12

## Contexto

A aplicação precisa fornecer logs estruturados e uma forma de acompanhar uma requisição entre API Gateway, Lambda e aplicação principal. A solução também deve ser compatível com a integração New Relic adotada pelo projeto.

## Decisão

Usar Logback com `LogstashEncoder` para produzir logs JSON no console e em arquivo. A correlação será feita pelos headers:

```text
X-Request-Id
X-Correlation-ID
```

O `RequestCorrelationFilter`:

- preserva os identificadores recebidos;
- gera UUIDs quando os headers não existem;
- devolve os valores nos headers da resposta;
- adiciona `request_id`, `correlation_id`, método, URI e endereço remoto ao MDC;
- adiciona `trace_id` quando o header `traceparent` estiver presente;
- registra início, conclusão e status da requisição;
- limpa o MDC ao final do processamento.

## Consequências

- Os logs ficam disponíveis em JSON para coleta e análise.
- A configuração utiliza `logstash-logback-encoder`.
- A aplicação pode enviar métricas e traces ao New Relic conforme as variáveis de ambiente.
- Gateway e Lambda devem propagar os identificadores para correlação ponta a ponta.
- O conteúdo dos logs não deve incluir CPF completo, tokens ou credenciais.
