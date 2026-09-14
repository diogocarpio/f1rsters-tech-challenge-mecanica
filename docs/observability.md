# Observabilidade da Aplicação

## Logs estruturados

A aplicação utiliza Logback com `LogstashEncoder` para emitir logs JSON no console e em arquivo. A configuração inclui o nome da aplicação, ambiente e os valores disponíveis no MDC.

O `RequestCorrelationFilter` registra o início e a conclusão das requisições com:

- método HTTP;
- caminho requisitado;
- status HTTP;
- endereço remoto;
- request ID;
- correlation ID;
- trace ID, quando o header `traceparent` estiver presente.

Dados sensíveis, como CPF completo, credenciais e tokens, não devem ser adicionados às mensagens de log.

## Correlação de requisições

Os headers utilizados são:

```text
X-Request-Id
X-Correlation-ID
```

Comportamento:

1. Se os headers forem recebidos, os valores são preservados.
2. Se estiverem ausentes, a aplicação gera UUIDs.
3. Os valores são devolvidos nos headers da resposta.
4. Os identificadores são adicionados ao MDC como `request_id` e `correlation_id`.
5. O header `traceparent`, quando recebido, é registrado como `trace_id`.
6. O MDC é limpo ao final da requisição.

O API Gateway e a Lambda devem propagar os mesmos headers para permitir correlação entre os componentes. A integração com New Relic é controlada pelas variáveis de ambiente documentadas no README.
